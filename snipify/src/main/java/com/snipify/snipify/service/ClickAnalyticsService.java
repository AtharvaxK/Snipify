package com.snipify.snipify.service;

import com.snipify.snipify.dto.UrlResponseDto;
import com.snipify.snipify.model.ClickAnalytics;
import com.snipify.snipify.model.Url;
import com.snipify.snipify.repo.ClickAnalyticsRepository;
import com.snipify.snipify.repo.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClickAnalyticsService {

    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final UrlRepository urlRepository;

    @PreAuthorize("#userId == authentication.principal.id")
    public Page<UrlResponseDto> getAllUrlByUser(long userId, Pageable pageable) {
        Page<Url> url= urlRepository.findByUser_Id(userId,pageable);
        return url.map(this::convertToDto);
    }

    @PreAuthorize("@urlSecurity.isOwner(#urlId,authentication.principal.id)")
    public Page<ClickAnalytics> getAllClicksByUrlId(long urlId,Pageable pageable){
            return clickAnalyticsRepository.findAllByUrl_urlId(urlId,pageable);
    }

    @PreAuthorize("#userId == authentication.principal.id")
    public long totalClicksByUser(long userId){
        return clickAnalyticsRepository.countTotalClicksByUser(userId);
    }

    @PreAuthorize("#userId == authentication.principal.id")
    public long clicksInLast30Days(long userId){
        return clickAnalyticsRepository.countClicksByTime(userId, LocalDateTime.now().minusDays(30));
    }

    @PreAuthorize("#userId == authentication.principal.id")
    public Map<String,Long> highestClicksByCountry(long userId){
        List<Object[]>list=clickAnalyticsRepository.highestCountryByClicks(userId);
        Map<String,Long>countryInfo=new LinkedHashMap<>();

        for(Object[] obj:list){
            countryInfo.put((String) obj[0],(Long) obj[1]);
        }
        return countryInfo;
    }

    @PreAuthorize("@urlSecurity.isOwner(#urlId,authentication.principal.id)")
    public long countTotalClicksOfUrl(long urlId){
        return clickAnalyticsRepository.countByUrl_urlId(urlId);
    }

    private UrlResponseDto convertToDto(Url url) {
        return new UrlResponseDto(
                url.getUrlId(),
                url.getShortCode(),
                url.getLongUrl(),
                url.getClickCount(),
                url.getStatus(),
                url.getExpiryTime()
        );
    }


}
