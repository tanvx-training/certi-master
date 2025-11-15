package com.certimaster.commonredis.pattern;

import com.certimaster.commonredis.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Cache warming utilities
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmer {

    private final CacheService cacheService;

    /**
     * Warm cache with list of items
     */
    public <T> void warmCache(
            List<T> items,
            Function<T, String> keyGenerator,
            Duration ttl) {

        log.info("Warming cache with {} items", items.size());

        for (T item : items) {
            try {
                String key = keyGenerator.apply(item);
                cacheService.set(key, item, ttl);
            } catch (Exception e) {
                log.error("Error warming cache for item", e);
            }
        }

        log.info("Cache warming completed with list of items");
    }

    /**
     * Warm cache with map
     */
    public <T> void warmCache(Map<String, T> dataMap, Duration ttl) {
        log.info("Warming cache with {} entries", dataMap.size());

        dataMap.forEach((key, value) -> {
            try {
                cacheService.set(key, value, ttl);
            } catch (Exception e) {
                log.error("Error warming cache for key: {}", key, e);
            }
        });

        log.info("Cache warming completed with map");
    }
}
