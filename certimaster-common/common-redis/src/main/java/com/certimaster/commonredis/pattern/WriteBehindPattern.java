package com.certimaster.commonredis.pattern;

import com.certimaster.commonredis.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Write-Behind (Write-Back) pattern implementation
 * Updates cache immediately and database asynchronously
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WriteBehindPattern {

    private final CacheService cacheService;

    /**
     * Update cache immediately, database asynchronously
     */
    @Async
    public <T> void update(String key, T value, Consumer<T> databaseWriter) {
        update(key, value, databaseWriter, null);
    }

    /**
     * Update cache immediately with TTL, database asynchronously
     */
    @Async
    public <T> void update(String key, T value, Consumer<T> databaseWriter, Duration ttl) {
        // Update cache immediately
        if (ttl != null) {
            cacheService.set(key, value, ttl);
        } else {
            cacheService.set(key, value);
        }

        log.debug("Cache updated (write-behind): {}", key);

        // Update database asynchronously
        try {
            databaseWriter.accept(value);
            log.debug("Database updated (write-behind): {}", key);
        } catch (Exception e) {
            log.error("Error updating database (write-behind) for key: {}", key, e);
            // Consider implementing retry mechanism or dead letter queue
        }
    }
}
