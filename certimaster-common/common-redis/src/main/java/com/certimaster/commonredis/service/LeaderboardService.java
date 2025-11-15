package com.certimaster.commonredis.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Leaderboard service using Redis Sorted Sets
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LEADERBOARD_PREFIX = "leaderboard:";

    /**
     * Add or update user score
     */
    public void addScore(String leaderboardName, String userId, double score) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            redisTemplate.opsForZSet().add(key, userId, score);
            log.debug("Score added: {} - {} = {}", leaderboardName, userId, score);
        } catch (Exception e) {
            log.error("Error adding score to leaderboard", e);
        }
    }

    /**
     * Increment user score
     */
    public Double incrementScore(String leaderboardName, String userId, double delta) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            return redisTemplate.opsForZSet().incrementScore(key, userId, delta);
        } catch (Exception e) {
            log.error("Error incrementing score", e);
            return null;
        }
    }

    /**
     * Get user rank (0-based, 0 is highest score)
     */
    public Long getUserRank(String leaderboardName, String userId) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            return redisTemplate.opsForZSet().reverseRank(key, userId);
        } catch (Exception e) {
            log.error("Error getting user rank", e);
            return null;
        }
    }

    /**
     * Get user score
     */
    public Double getUserScore(String leaderboardName, String userId) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            return redisTemplate.opsForZSet().score(key, userId);
        } catch (Exception e) {
            log.error("Error getting user score", e);
            return null;
        }
    }

    /**
     * Get top N users
     */
    public List<LeaderboardEntry> getTopUsers(String leaderboardName, int count) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            Set<ZSetOperations.TypedTuple<Object>> topScores =
                    redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, count - 1);

            List<LeaderboardEntry> entries = new ArrayList<>();
            if (topScores != null) {
                int rank = 1;
                for (ZSetOperations.TypedTuple<Object> tuple : topScores) {
                    entries.add(new LeaderboardEntry(
                            rank++,
                            tuple.getValue().toString(),
                            tuple.getScore()
                    ));
                }
            }

            return entries;
        } catch (Exception e) {
            log.error("Error getting top users", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get users around a specific user
     */
    public List<LeaderboardEntry> getUsersAround(String leaderboardName, String userId, int range) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            Long rank = redisTemplate.opsForZSet().reverseRank(key, userId);

            if (rank == null) {
                return new ArrayList<>();
            }

            long start = Math.max(0, rank - range);
            long end = rank + range;

            Set<ZSetOperations.TypedTuple<Object>> scores =
                    redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);

            List<LeaderboardEntry> entries = new ArrayList<>();
            if (scores != null) {
                int currentRank = (int) start + 1;
                for (ZSetOperations.TypedTuple<Object> tuple : scores) {
                    entries.add(new LeaderboardEntry(
                            currentRank++,
                            tuple.getValue().toString(),
                            tuple.getScore()
                    ));
                }
            }

            return entries;
        } catch (Exception e) {
            log.error("Error getting users around", e);
            return new ArrayList<>();
        }
    }

    /**
     * Remove user from leaderboard
     */
    public void removeUser(String leaderboardName, String userId) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            redisTemplate.opsForZSet().remove(key, userId);
            log.debug("User removed from leaderboard: {}", userId);
        } catch (Exception e) {
            log.error("Error removing user from leaderboard", e);
        }
    }

    /**
     * Get total users in leaderboard
     */
    public long getTotalUsers(String leaderboardName) {
        try {
            String key = LEADERBOARD_PREFIX + leaderboardName;
            Long size = redisTemplate.opsForZSet().size(key);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Error getting leaderboard size", e);
            return 0;
        }
    }

    @Data
    @AllArgsConstructor
    public static class LeaderboardEntry {
        private int rank;
        private String userId;
        private Double score;
    }
}
