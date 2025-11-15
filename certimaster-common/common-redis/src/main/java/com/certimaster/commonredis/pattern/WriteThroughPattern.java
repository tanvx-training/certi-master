package com.certimaster.commonredis.pattern;

import com.certimaster.commonredis.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Write-Through pattern implementation
 * Updates cache and database simultaneously
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WriteThroughPattern {

    private final CacheService cacheService;

    /**
     * Update both cache and database
     */
    public <T> void update(String key, T value, Consumer<T> databaseWriter) {
        update(key, value, databaseWriter, null);
    }

    /**
     * Update both cache and database with TTL
     */
    public <T> void update(String key, T value, Consumer<T> databaseWriter, Duration ttl) {
        try {
            // Write to database first
            databaseWriter.accept(value);

            // Then update cache
            if (ttl != null) {
                cacheService.set(key, value, ttl);
            } else {
                cacheService.set(key, value);
            }

            log.debug("Write-through completed: {}", key);
        } catch (Exception e) {
            log.error("Error in write-through for key: {}", key, e);
            // Invalidate cache on error
            cacheService.delete(key);
            throw e;
        }
    }
}
