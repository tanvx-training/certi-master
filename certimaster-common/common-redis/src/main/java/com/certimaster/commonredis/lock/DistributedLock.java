package com.certimaster.commonredis.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Distributed lock implementation using Redis
 * Provides mutual exclusion across multiple application instances
 */
@Slf4j
@RequiredArgsConstructor
public class DistributedLock {

    private final StringRedisTemplate redisTemplate;
    private final String lockKey;
    private final String lockValue;
    private final Duration lockTimeout;

    private static final String LOCK_PREFIX = "lock:";

    // Lua script for atomic unlock operation
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "    return redis.call('del', KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";

    /**
     * Try to acquire lock
     */
    public boolean tryLock() {
        try {
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(LOCK_PREFIX + lockKey, lockValue,
                            lockTimeout.toMillis(), TimeUnit.MILLISECONDS);

            if (Boolean.TRUE.equals(result)) {
                log.debug("Lock acquired: {}", lockKey);
                return true;
            }

            log.debug("Failed to acquire lock: {}", lockKey);
            return false;
        } catch (Exception e) {
            log.error("Error acquiring lock: {}", lockKey, e);
            return false;
        }
    }

    /**
     * Try to acquire lock with custom timeout
     */
    public boolean tryLock(Duration timeout) {
        try {
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(LOCK_PREFIX + lockKey, lockValue,
                            timeout.toMillis(), TimeUnit.MILLISECONDS);

            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error acquiring lock with timeout: {}", lockKey, e);
            return false;
        }
    }

    /**
     * Release lock (only if owned by current thread)
     */
    public boolean unlock() {
        try {
            Long result = redisTemplate.execute(
                    RedisScript.of(UNLOCK_SCRIPT, Long.class),
                    Collections.singletonList(LOCK_PREFIX + lockKey),
                    lockValue
            );

            boolean unlocked = result != null && result > 0;
            if (unlocked) {
                log.debug("Lock released: {}", lockKey);
            } else {
                log.warn("Failed to release lock (not owner): {}", lockKey);
            }

            return unlocked;
        } catch (Exception e) {
            log.error("Error releasing lock: {}", lockKey, e);
            return false;
        }
    }

    /**
     * Check if lock is currently held
     */
    public boolean isLocked() {
        try {
            Boolean exists = redisTemplate.hasKey(LOCK_PREFIX + lockKey);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking lock status: {}", lockKey, e);
            return false;
        }
    }

    /**
     * Extend lock timeout
     */
    public boolean extend(Duration additionalTime) {
        try {
            Boolean result = redisTemplate.expire(
                    LOCK_PREFIX + lockKey,
                    additionalTime.toMillis(),
                    TimeUnit.MILLISECONDS
            );

            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error extending lock: {}", lockKey, e);
            return false;
        }
    }
}
