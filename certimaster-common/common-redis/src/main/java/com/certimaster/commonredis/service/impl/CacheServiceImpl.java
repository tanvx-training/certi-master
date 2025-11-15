package com.certimaster.commonredis.service.impl;

import com.certimaster.commonredis.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis cache service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // ========================================================================
    // STRING OPERATIONS
    // ========================================================================

    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Set cache: {}", key);
        } catch (Exception e) {
            log.error("Error setting cache for key: {}", key, e);
        }
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
            log.debug("Set cache with TTL: {} ({}s)", key, ttl.getSeconds());
        } catch (Exception e) {
            log.error("Error setting cache with TTL for key: {}", key, e);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting if absent for key: {}", key, e);
            return false;
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Duration ttl) {
        try {
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting if absent with TTL for key: {}", key, e);
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }

            // Direct cast if same type
            if (type.isInstance(value)) {
                return (T) value;
            }

            // Convert using ObjectMapper
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("Error getting cache for key: {}", key, e);
            return null;
        }
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        T value = get(key, type);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Delete cache: {}", key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error deleting cache for key: {}", key, e);
            return false;
        }
    }

    @Override
    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            log.debug("Delete {} cache keys", count);
            return count;
        } catch (Exception e) {
            log.error("Error deleting cache for keys: {}", keys, e);
            return 0;
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking existence for key: {}", key, e);
            return false;
        }
    }

    @Override
    public Long increment(String key) {
        return increment(key, 1L);
    }

    @Override
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Error incrementing key: {}", key, e);
            return null;
        }
    }

    @Override
    public Long decrement(String key) {
        return decrement(key, 1L);
    }

    @Override
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Error decrementing key: {}", key, e);
            return null;
        }
    }

    // ========================================================================
    // HASH OPERATIONS
    // ========================================================================

    @Override
    public void hashSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            log.debug("Set hash: {} - {}", key, field);
        } catch (Exception e) {
            log.error("Error setting hash for key: {} field: {}", key, field, e);
        }
    }

    @Override
    public void hashSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            log.debug("Set hash all: {} ({} fields)", key, map.size());
        } catch (Exception e) {
            log.error("Error setting hash all for key: {}", key, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T hashGet(String key, String field, Class<T> type) {
        try {
            Object value = redisTemplate.opsForHash().get(key, field);
            if (value == null) {
                return null;
            }

            if (type.isInstance(value)) {
                return (T) value;
            }

            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("Error getting hash for key: {} field: {}", key, field, e);
            return null;
        }
    }

    @Override
    public Map<String, Object> hashGetAll(String key) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            return entries.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            Map.Entry::getValue
                    ));
        } catch (Exception e) {
            log.error("Error getting hash all for key: {}", key, e);
            return new HashMap<>();
        }
    }

    @Override
    public long hashDelete(String key, String... fields) {
        try {
            return redisTemplate.opsForHash().delete(key, (Object[]) fields);
        } catch (Exception e) {
            log.error("Error deleting hash fields for key: {}", key, e);
            return 0;
        }
    }

    @Override
    public boolean hashExists(String key, String field) {
        try {
            return redisTemplate.opsForHash().hasKey(key, field);
        } catch (Exception e) {
            log.error("Error checking hash field existence: {} - {}", key, field, e);
            return false;
        }
    }

    @Override
    public Set<String> hashKeys(String key) {
        try {
            Set<Object> keys = redisTemplate.opsForHash().keys(key);
            return keys.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error getting hash keys for key: {}", key, e);
            return new HashSet<>();
        }
    }

    @Override
    public Long hashIncrement(String key, String field, long delta) {
        try {
            return redisTemplate.opsForHash().increment(key, field, delta);
        } catch (Exception e) {
            log.error("Error incrementing hash field: {} - {}", key, field, e);
            return null;
        }
    }

    // ========================================================================
    // LIST OPERATIONS
    // ========================================================================

    @Override
    public long listLeftPush(String key, Object value) {
        try {
            Long size = redisTemplate.opsForList().leftPush(key, value);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error left pushing to list: {}", key, e);
            return 0;
        }
    }

    @Override
    public long listRightPush(String key, Object value) {
        try {
            Long size = redisTemplate.opsForList().rightPush(key, value);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error right pushing to list: {}", key, e);
            return 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T listLeftPop(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForList().leftPop(key);
            if (value == null) {
                return null;
            }

            if (type.isInstance(value)) {
                return (T) value;
            }

            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("Error left popping from list: {}", key, e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T listRightPop(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForList().rightPop(key);
            if (value == null) {
                return null;
            }

            if (type.isInstance(value)) {
                return (T) value;
            }

            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("Error right popping from list: {}", key, e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> listRange(String key, long start, long end, Class<T> type) {
        try {
            List<Object> values = redisTemplate.opsForList().range(key, start, end);
            if (values == null || values.isEmpty()) {
                return new ArrayList<>();
            }

            return values.stream()
                    .map(v -> objectMapper.convertValue(v, type))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting list range: {}", key, e);
            return new ArrayList<>();
        }
    }

    @Override
    public long listSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error getting list size: {}", key, e);
            return 0;
        }
    }

    @Override
    public void listTrim(String key, long start, long end) {
        try {
            redisTemplate.opsForList().trim(key, start, end);
        } catch (Exception e) {
            log.error("Error trimming list: {}", key, e);
        }
    }

    // ========================================================================
    // SET OPERATIONS
    // ========================================================================

    @Override
    public long setAdd(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Error adding to set: {}", key, e);
            return 0;
        }
    }

    @Override
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Error removing from set: {}", key, e);
            return 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> setMembers(String key, Class<T> type) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(key);
            if (members == null || members.isEmpty()) {
                return new HashSet<>();
            }

            return members.stream()
                    .map(m -> objectMapper.convertValue(m, type))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error getting set members: {}", key, e);
            return new HashSet<>();
        }
    }

    @Override
    public boolean setIsMember(String key, Object value) {
        try {
            Boolean result = redisTemplate.opsForSet().isMember(key, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking set membership: {}", key, e);
            return false;
        }
    }

    @Override
    public long setSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error getting set size: {}", key, e);
            return 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T setRandomMember(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForSet().randomMember(key);

            if (type.isInstance(value)) {
                return (T) value;
            }

            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("Error getting random set member: {}", key, e);
            return null;
        }
    }

    // ========================================================================
    // KEY OPERATIONS
    // ========================================================================

    @Override
    public boolean expire(String key, Duration ttl) {
        try {
            Boolean result = redisTemplate.expire(key, ttl.toMillis(), TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting expiration for key: {}", key, e);
            return false;
        }
    }

    @Override
    public Duration getExpire(String key) {
        try {
            long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl > 0 ? Duration.ofSeconds(ttl) : Duration.ZERO;
        } catch (Exception e) {
            log.error("Error getting expiration for key: {}", key, e);
            return Duration.ZERO;
        }
    }

    @Override
    public boolean persist(String key) {
        try {
            Boolean result = redisTemplate.persist(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error persisting key: {}", key, e);
            return false;
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Error getting keys by pattern: {}", pattern, e);
            return new HashSet<>();
        }
    }

    @Override
    public Set<String> keysWithPrefix(String prefix) {
        return keys(prefix + "*");
    }

    @Override
    public void rename(String oldKey, String newKey) {
        try {
            redisTemplate.rename(oldKey, newKey);
        } catch (Exception e) {
            log.error("Error renaming key: {} to {}", oldKey, newKey, e);
        }
    }

    // ========================================================================
    // SORTED SET OPERATIONS (continued)
    // ========================================================================

    @Override
    public boolean sortedSetAdd(String key, Object value, double score) {
        try {
            Boolean result = redisTemplate.opsForZSet().add(key, value, score);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error adding to sorted set: {}", key, e);
            return false;
        }
    }

    @Override
    public Long sortedSetRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            log.error("Error getting rank in sorted set: {}", key, e);
            return null;
        }
    }

    @Override
    public Double sortedSetScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("Error getting score in sorted set: {}", key, e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> sortedSetRange(String key, long start, long end, Class<T> type) {
        try {
            Set<Object> values = redisTemplate.opsForZSet().range(key, start, end);
            if (values == null || values.isEmpty()) {
                return new LinkedHashSet<>();
            }

            return values.stream()
                    .map(v -> objectMapper.convertValue(v, type))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.error("Error getting sorted set range: {}", key, e);
            return new LinkedHashSet<>();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> sortedSetRangeByScore(String key, double min, double max, Class<T> type) {
        try {
            Set<Object> values = redisTemplate.opsForZSet().rangeByScore(key, min, max);
            if (values == null || values.isEmpty()) {
                return new LinkedHashSet<>();
            }

            return values.stream()
                    .map(v -> objectMapper.convertValue(v, type))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.error("Error getting sorted set range by score: {}", key, e);
            return new LinkedHashSet<>();
        }
    }

    @Override
    public long sortedSetRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForZSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Error removing from sorted set: {}", key, e);
            return 0;
        }
    }

    @Override
    public long sortedSetSize(String key) {
        try {
            Long size = redisTemplate.opsForZSet().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error getting sorted set size: {}", key, e);
            return 0;
        }
    }
}
