package com.snipify.snipify.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component

public class RateLimiter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ip=request.getHeader("X-Forwarded-For");
        String ipAddress=null;
        if(ip!=null&&!ip.isBlank()){
            String []ipArray=ip.split(",");
            if (ipArray[0]!=null&&!"Unknown".equals(ipArray[0].trim())){
                ipAddress=ipArray[0];
            }
        }

        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        String key="rate_limit:"+ipAddress;

        Long num=redisTemplate.opsForValue().increment(key);

        if(num != null &&num ==1){
            redisTemplate.expire(key, Duration.ofSeconds(60));
            filterChain.doFilter(request,response);
        }
        else if(num != null&&num>1 &&num<=40){
            filterChain.doFilter(request,response);
        }
        else{
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"error\": \"Too Many Requests. Try again in 60 seconds.\"}");
        }
    }
}
