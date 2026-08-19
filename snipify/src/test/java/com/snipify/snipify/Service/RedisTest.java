package com.snipify.snipify.Service;

import jakarta.el.ValueReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@DataRedisTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    Environment environment;

    @Test
    void testUrlService() {
        redisTemplate.opsForValue().set("email", "Atharva@gmail.com");
        redisTemplate.opsForValue().set("name", "Atharva");
        Object salary =redisTemplate.opsForValue().get("salary");
    }
}
