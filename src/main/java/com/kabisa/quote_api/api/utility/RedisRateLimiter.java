package com.kabisa.quote_api.api.utility;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryConsume(String key, int limit, Duration duration) {
        String redisKey = "rate_limit:" + key;

        // Increment request counter
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count == null) {
            return true;
        }

        // Set expiration on first request
        if (count == 1L) {
            redisTemplate.expire(redisKey, duration);
        }

        return count > limit;
    }

}
