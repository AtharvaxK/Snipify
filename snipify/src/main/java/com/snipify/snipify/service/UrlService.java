package com.snipify.snipify.service;

import com.github.f4b6a3.tsid.TsidCreator;
import com.snipify.snipify.CustomExceptions.LinkExpiredException;
import com.snipify.snipify.CustomExceptions.LinkNotFoundException;
import com.snipify.snipify.Enums.Browsers;
import com.snipify.snipify.Enums.Status;
import com.snipify.snipify.dto.LongUrlMappingtoRedis;
import com.snipify.snipify.model.Url;
import com.snipify.snipify.model.User;
import com.snipify.snipify.repo.ClickAnalyticsRepository;
import com.snipify.snipify.repo.UrlRepository;
import com.snipify.snipify.util.Base62Engine;
import com.snipify.snipify.util.UrlUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlUtil urlUtil;
    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final EntityManager entityManager;
    private final RedisService redisService;
    private final LogAnalyticsAsyncService analyticsAsyncService;

    @Transactional
    public String shortenUrl(String longUrl , int validTime, User authenticatedUser){
        LocalDateTime currentTime=LocalDateTime.now();
        Url url=new Url();
        url.setLongUrl(longUrl);
        url.setCreatedAt(currentTime);
        url.setStatus(Status.ACTIVE);
        url.setExpiryTime(currentTime.plusMinutes(validTime));

        boolean isProU=false;
        if (authenticatedUser != null) {
            url.setUser(authenticatedUser);
            url.setPro(authenticatedUser.getPro());
           isProU=authenticatedUser.getRoles()!=null&&authenticatedUser.getRoles().stream().anyMatch(role->"PRO".equalsIgnoreCase(role.toString()));
        }


        url.setProUser(isProU);

        url.setUrlId(TsidCreator.getTsid().toLong());

        String shortCode= Base62Engine.encode(url.getUrlId());
        url.setShortCode(shortCode);
        entityManager.persist(url);


        return shortCode;
    }

    @Scheduled(cron = "*/50 * * * * *")
    @Transactional
    public void cleanExpired(){
        urlRepository.cleanExpired(Status.EXPIRED, LocalDateTime.now());
        log.info("Expired links cleaned");
    }


    public String longUrlMapping(String subdomain, String shortCode, HttpServletRequest httpServletRequest){
        long start = System.currentTimeMillis();
        LongUrlMappingtoRedis url = redisService.getFromRedis(shortCode);
        log.info("Redis fetch took: {} ms", System.currentTimeMillis() - start);
        String ip=urlUtil.getIpFromHttpHeader(httpServletRequest);

        if(url!=null){
            if(url.getStatus()==Status.EXPIRED){
                throw new LinkExpiredException("Link Expired");
            }

            if (url.getExpiryTime()!=null&&url.getExpiryTime().isBefore(LocalDateTime.now())){
                url.setStatus(Status.EXPIRED);
                redisService.setToRedis(shortCode,url,120L);
                urlRepository.updateStatus(Status.EXPIRED,url.getId());
                throw new LinkExpiredException("Link Expired");
            }
            //cache hit
            if(url.isPremium()) {

                redisService.incrementClickCounterInMemory(url.getId());
               Browsers browser=urlUtil.getBrowserFromHttpRequest(httpServletRequest);
                LocalDateTime clockedAt=LocalDateTime.now();
                String device=urlUtil.getOSFromHttpRequest(httpServletRequest);
                analyticsAsyncService.logAnalyticsAsync(url.getId(),browser,device,clockedAt,ip);


            }
                log.info("REDIRECT [Cache Hit] for shortCode '{}' took: {} ms", shortCode, (System.currentTimeMillis() - start));
                log.info("FROM REDIS");
                return url.getLongUrl();


        }
        //cache miss
        else {
            long dbStart=System.currentTimeMillis();
            Url fromDB=urlRepository.findById((long)Base62Engine.decode(shortCode)).orElse(null);
            if (fromDB==null){
                throw new LinkNotFoundException("Link does not exist");

            }

            if(fromDB.getStatus()==Status.EXPIRED){
                throw new LinkExpiredException("Link Expired");
            }

            if (fromDB.getExpiryTime()!=null&&fromDB.getExpiryTime().isBefore(LocalDateTime.now())){
                urlRepository.updateStatus(Status.EXPIRED,fromDB.getUrlId());
                throw new LinkExpiredException("Link Expired");
            }
            if(fromDB.isProUser()){

               redisService.incrementClickCounterInMemory(fromDB.getUrlId());
                Browsers browser=urlUtil.getBrowserFromHttpRequest(httpServletRequest);
                LocalDateTime clockedAt=LocalDateTime.now();
                String device=urlUtil.getOSFromHttpRequest(httpServletRequest);
                analyticsAsyncService.logAnalyticsAsync(fromDB.getUrlId(), browser,device,clockedAt,ip);


            }

            LongUrlMappingtoRedis longUrlMappingtoRedis=new LongUrlMappingtoRedis();
            longUrlMappingtoRedis.setId(fromDB.getUrlId());
            longUrlMappingtoRedis.setStatus(fromDB.getStatus());
            longUrlMappingtoRedis.setClickCount(fromDB.getClickCount());
            longUrlMappingtoRedis.setExpiryTime(fromDB.getExpiryTime());
            longUrlMappingtoRedis.setShortcode(fromDB.getShortCode());
            longUrlMappingtoRedis.setLongUrl(fromDB.getLongUrl());
            longUrlMappingtoRedis.setPremium(fromDB.isProUser());


            redisService.setToRedis(shortCode,longUrlMappingtoRedis,120L);
            log.info("FROM DB");
            log.info("DB call took: {} ms", System.currentTimeMillis() - dbStart);
            log.info("REDIRECT [Cache Miss] for shortCode '{}' took: {} ms", shortCode, (System.currentTimeMillis() - start));
            return fromDB.getLongUrl();
        }



    }
}
