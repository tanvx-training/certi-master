package com.certimaster.commonredis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Distributed rate limiter using Redis
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiter {

    private final StringRedisTemplate redisTemplate;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    // Lua script for atomic rate limiting
    private static final String RATE_LIMIT_SCRIPT =
            "local key = KEYS[1] " +
                    "local limit = tonumber(ARGV[1]) " +
                    "local window = tonumber(ARGV[2]) " +
                    "local current = redis.call('INCR', key) " +
                    "if current == 1 then " +
                    "    redis.call('EXPIRE', key, window) " +
                    "end " +
                    "if current > limit then " +
                    "    return 0 " +
                    "else " +
                    "    return 1 " +
                    "end";

    /**
     * Check if request is allowed
     */
    public boolean isAllowed(String key, int maxRequests, Duration window) {
        try {
            String rateLimitKey = RATE_LIMIT_PREFIX + key;

            Long result = redisTemplate.execute(
                    RedisScript.of(RATE_LIMIT_SCRIPT, Long.class),
                    List.of(rateLimitKey),
                    String.valueOf(maxRequests),
                    String.valueOf(window.getSeconds())
            );

            boolean allowed = result > 0;

            if (!allowed) {
                log.warn("Rate limit exceeded for key: {}", key);
            }

            return allowed;
        } catch (Exception e) {
            log.error("Error checking rate limit for key: {}", key, e);
            // Fail open - allow request on error
            return true;
        }
    }

    /**
     * Get current request count
     */
    public long getCurrentCount(String key) {
        try {
            String rateLimitKey = RATE_LIMIT_PREFIX + key;
            String value = redisTemplate.opsForValue().get(rateLimitKey);
            return value != null ? Long.parseLong(value) : 0;
        } catch (Exception e) {
            log.error("Error getting rate limit count for key: {}", key, e);
            return 0;
        }
    }

    /**
     * Reset rate limit for key
     */
    public void reset(String key) {
        try {
            String rateLimitKey = RATE_LIMIT_PREFIX + key;
            redisTemplate.delete(rateLimitKey);
            log.debug("Rate limit reset for key: {}", key);
        } catch (Exception e) {
            log.error("Error resetting rate limit for key: {}", key, e);
        }
    }

    /**
     * Get remaining requests
     */
    public long getRemaining(String key, int maxRequests) {
        long current = getCurrentCount(key);
        return Math.max(0, maxRequests - current);
    }
}
