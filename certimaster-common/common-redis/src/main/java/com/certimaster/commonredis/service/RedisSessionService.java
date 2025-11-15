package com.certimaster.commonredis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * Session management service using Redis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSessionService {

    private final CacheService cacheService;

    private static final String SESSION_PREFIX = "session:";
    private static final Duration DEFAULT_SESSION_TTL = Duration.ofHours(2);

    /**
     * Create new session
     */
    public String createSession(String userId, Map<String, Object> attributes) {
        String sessionId = UUID.randomUUID().toString();
        String sessionKey = SESSION_PREFIX + sessionId;

        Map<String, Object> sessionData = new java.util.HashMap<>(attributes);
        sessionData.put("userId", userId);
        sessionData.put("createdAt", System.currentTimeMillis());

        cacheService.hashSetAll(sessionKey, sessionData);
        cacheService.expire(sessionKey, DEFAULT_SESSION_TTL);

        log.info("Session created: {} for user: {}", sessionId, userId);
        return sessionId;
    }

    /**
     * Get session data
     */
    public Map<String, Object> getSession(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        return cacheService.hashGetAll(sessionKey);
    }

    /**
     * Update session attribute
     */
    public void updateSessionAttribute(String sessionId, String key, Object value) {
        String sessionKey = SESSION_PREFIX + sessionId;
        cacheService.hashSet(sessionKey, key, value);
        // Refresh TTL
        cacheService.expire(sessionKey, DEFAULT_SESSION_TTL);
    }

    /**
     * Delete session
     */
    public void deleteSession(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        cacheService.delete(sessionKey);
        log.info("Session deleted: {}", sessionId);
    }

    /**
     * Extend session
     */
    public void extendSession(String sessionId, Duration additionalTime) {
        String sessionKey = SESSION_PREFIX + sessionId;
        if (cacheService.exists(sessionKey)) {
            cacheService.expire(sessionKey, additionalTime);
        }
    }
}
