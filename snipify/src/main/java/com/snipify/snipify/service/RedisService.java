package com.snipify.snipify.service;

import com.snipify.snipify.dto.LongUrlMappingtoRedis;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {



    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;

    public LongUrlMappingtoRedis getFromRedis(String key){
        try{
            Object value=redisTemplate.opsForValue().get(key);
            if(value==null){
                return null;
            }
            return objectMapper.convertValue(value, LongUrlMappingtoRedis.class);
        }
        catch (Exception e){
            log.error("Error : " +e);
            return null;
        }

    }

    public void setToRedis(String key, LongUrlMappingtoRedis url,Long ttl){
        try {
            redisTemplate.opsForValue().set(key,url,ttl, TimeUnit.SECONDS);
        }
        catch (Exception e){
            log.error("Error updating redis "+e);
        }
    }

    public void incrementClickCounterInMemory(Long urlId){
        try {

            String key = "url:clickCount:" + urlId;

            stringRedisTemplate.opsForValue().increment(key, 1);
            log.info("Counter incremented successfully for ID :{}",urlId);
        }
        catch (Exception e){
            log.error("Failed to increment the click count in-memory for ID : {}" ,urlId,e);
        }

    }



}
