package com.certimaster.commonredis.service;

import com.certimaster.commonredis.lock.DistributedLock;
import com.certimaster.commonredis.lock.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Service for managing distributed locks
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration DEFAULT_LOCK_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration RETRY_INTERVAL = Duration.ofMillis(100);

    /**
     * Create a new distributed lock
     */
    public DistributedLock createLock(String key) {
        return createLock(key, DEFAULT_LOCK_TIMEOUT);
    }

    /**
     * Create a new distributed lock with custom timeout
     */
    public DistributedLock createLock(String key, Duration timeout) {
        String lockValue = UUID.randomUUID().toString();
        return new DistributedLock(redisTemplate, key, lockValue, timeout);
    }

    /**
     * Execute action with lock
     */
    public <T> void executeWithLock(String lockKey, Supplier<T> action) {
        executeWithLock(lockKey, DEFAULT_LOCK_TIMEOUT, action);
    }

    /**
     * Execute action with lock and custom timeout
     */
    public <T> void executeWithLock(String lockKey, Duration lockTimeout, Supplier<T> action) {
        DistributedLock lock = createLock(lockKey, lockTimeout);

        try {
            if (lock.tryLock()) {
                action.get();
            } else {
                throw new LockAcquisitionException(
                        "Failed to acquire lock: " + lockKey);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Execute action with lock, waiting if necessary
     */
    public <T> T executeWithLockWait(String lockKey, Supplier<T> action)
            throws InterruptedException {
        return executeWithLockWait(lockKey, DEFAULT_LOCK_TIMEOUT,
                DEFAULT_WAIT_TIMEOUT, action);
    }

    /**
     * Execute action with lock, waiting with custom timeouts
     */
    public <T> T executeWithLockWait(
            String lockKey,
            Duration lockTimeout,
            Duration waitTimeout,
            Supplier<T> action) throws InterruptedException {

        DistributedLock lock = createLock(lockKey, lockTimeout);
        long startTime = System.currentTimeMillis();
        long waitMs = waitTimeout.toMillis();

        while (System.currentTimeMillis() - startTime < waitMs) {
            if (lock.tryLock()) {
                try {
                    return action.get();
                } finally {
                    lock.unlock();
                }
            }

            Thread.sleep(RETRY_INTERVAL.toMillis());
        }

        throw new LockAcquisitionException(
                "Timeout waiting for lock: " + lockKey);
    }

    /**
     * Execute runnable with lock
     */
    public void executeWithLock(String lockKey, Runnable action) {
        executeWithLock(lockKey, () -> {
            action.run();
            return null;
        });
    }
}
