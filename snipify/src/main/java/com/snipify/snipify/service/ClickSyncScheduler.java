package com.snipify.snipify.service;

import com.snipify.snipify.CustomExceptions.DataUpdationException;
import com.snipify.snipify.repo.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClickSyncScheduler {
    private final StringRedisTemplate stringRedisTemplate;
    private final UrlRepository urlRepository;

    @Scheduled(fixedDelay = 300000)
    public void flushCountToDBFromRedis(){
        Set<String>keys=stringRedisTemplate.keys("url:clickCount:*");
        if (keys==null){
            return;
        }

        for (String key:keys) {
            Long urlId = null;
            try {
                String id = key.replace("url:clickCount:", "");
                urlId = Long.parseLong(id);

                String clickCounter = stringRedisTemplate.opsForValue().getAndDelete(key);
                if (clickCounter!=null) {
                    Long count = Long.parseLong(clickCounter);

                    urlRepository.updateClickCount(count, urlId);
                }
            } catch (Exception e) {
                log.error("Something went wrong while updating click count for url id: {}", urlId,e);
            }

        }
    }
}
