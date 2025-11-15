package com.certimaster.commonredis.pattern;

import com.certimaster.commonredis.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Cache-Aside (Lazy Loading) pattern implementation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheAsidePattern {

    private final CacheService cacheService;

    /**
     * Get from cache or load from source
     */
    public <T> T getOrLoad(String key, Class<T> type, Supplier<T> loader) {
        return getOrLoad(key, type, loader, null);
    }

    /**
     * Get from cache or load from source with TTL
     */
    public <T> T getOrLoad(String key, Class<T> type, Supplier<T> loader, Duration ttl) {
        // Try cache first
        T cachedValue = cacheService.get(key, type);
        if (cachedValue != null) {
            log.debug("Cache hit: {}", key);
            return cachedValue;
        }

        log.debug("Cache miss: {}", key);

        // Load from source
        T value = loader.get();

        // Store in cache
        if (value != null) {
            if (ttl != null) {
                cacheService.set(key, value, ttl);
            } else {
                cacheService.set(key, value);
            }
        }

        return value;
    }
}
