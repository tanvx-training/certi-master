package com.certimaster.commonredis.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic cache service interface
 */
public interface CacheService {

    // ========================================================================
    // STRING OPERATIONS
    // ========================================================================

    /**
     * Set value with no expiration
     */
    void set(String key, Object value);

    /**
     * Set value with TTL
     */
    void set(String key, Object value, Duration ttl);

    /**
     * Set if not exists (NX)
     */
    boolean setIfAbsent(String key, Object value);

    /**
     * Set if not exists with TTL
     */
    boolean setIfAbsent(String key, Object value, Duration ttl);

    /**
     * Get value
     */
    <T> T get(String key, Class<T> type);

    /**
     * Get value with default
     */
    <T> T getOrDefault(String key, Class<T> type, T defaultValue);

    /**
     * Delete key
     */
    boolean delete(String key);

    /**
     * Delete multiple keys
     */
    long delete(Collection<String> keys);

    /**
     * Check if key exists
     */
    boolean exists(String key);

    /**
     * Increment value
     */
    Long increment(String key);

    /**
     * Increment by delta
     */
    Long increment(String key, long delta);

    /**
     * Decrement value
     */
    Long decrement(String key);

    /**
     * Decrement by delta
     */
    Long decrement(String key, long delta);

    // ========================================================================
    // HASH OPERATIONS
    // ========================================================================

    /**
     * Set hash field
     */
    void hashSet(String key, String field, Object value);

    /**
     * Set multiple hash fields
     */
    void hashSetAll(String key, Map<String, Object> map);

    /**
     * Get hash field
     */
    <T> T hashGet(String key, String field, Class<T> type);

    /**
     * Get all hash fields
     */
    Map<String, Object> hashGetAll(String key);

    /**
     * Delete hash field
     */
    long hashDelete(String key, String... fields);

    /**
     * Check if hash field exists
     */
    boolean hashExists(String key, String field);

    /**
     * Get all hash keys
     */
    Set<String> hashKeys(String key);

    /**
     * Increment hash field
     */
    Long hashIncrement(String key, String field, long delta);

    // ========================================================================
    // LIST OPERATIONS
    // ========================================================================

    /**
     * Push to list (left)
     */
    long listLeftPush(String key, Object value);

    /**
     * Push to list (right)
     */
    long listRightPush(String key, Object value);

    /**
     * Pop from list (left)
     */
    <T> T listLeftPop(String key, Class<T> type);

    /**
     * Pop from list (right)
     */
    <T> T listRightPop(String key, Class<T> type);

    /**
     * Get list range
     */
    <T> List<T> listRange(String key, long start, long end, Class<T> type);

    /**
     * Get list size
     */
    long listSize(String key);

    /**
     * Trim list
     */
    void listTrim(String key, long start, long end);

    // ========================================================================
    // SET OPERATIONS
    // ========================================================================

    /**
     * Add to set
     */
    long setAdd(String key, Object... values);

    /**
     * Remove from set
     */
    long setRemove(String key, Object... values);

    /**
     * Get all set members
     */
    <T> Set<T> setMembers(String key, Class<T> type);

    /**
     * Check if member exists in set
     */
    boolean setIsMember(String key, Object value);

    /**
     * Get set size
     */
    long setSize(String key);

    /**
     * Random member from set
     */
    <T> T setRandomMember(String key, Class<T> type);

    // ========================================================================
    // SORTED SET OPERATIONS
    // ========================================================================

    /**
     * Add to sorted set
     */
    boolean sortedSetAdd(String key, Object value, double score);

    /**
     * Get rank in sorted set
     */
    Long sortedSetRank(String key, Object value);

    /**
     * Get score in sorted set
     */
    Double sortedSetScore(String key, Object value);

    /**
     * Get range from sorted set
     */
    <T> Set<T> sortedSetRange(String key, long start, long end, Class<T> type);

    /**
     * Get range by score
     */
    <T> Set<T> sortedSetRangeByScore(String key, double min, double max, Class<T> type);

    /**
     * Remove from sorted set
     */
    long sortedSetRemove(String key, Object... values);

    /**
     * Get sorted set size
     */
    long sortedSetSize(String key);

    // ========================================================================
    // KEY OPERATIONS
    // ========================================================================

    /**
     * Set expiration
     */
    boolean expire(String key, Duration ttl);

    /**
     * Get expiration
     */
    Duration getExpire(String key);

    /**
     * Remove expiration
     */
    boolean persist(String key);

    /**
     * Get keys by pattern
     */
    Set<String> keys(String pattern);

    /**
     * Get all keys with prefix
     */
    Set<String> keysWithPrefix(String prefix);

    /**
     * Rename key
     */
    void rename(String oldKey, String newKey);
}
