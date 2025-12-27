package com.certimaster.resultservice.service;

import com.certimaster.resultservice.dto.response.UserExamSessionResponse;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user exam session operations.
 */
public interface UserExamSessionService {

    /**
     * Get all sessions for a user.
     */
    List<UserExamSessionResponse> getSessionsByUserId(Long userId);

    /**
     * Get sessions for a user and specific exam.
     */
    List<UserExamSessionResponse> getSessionsByUserIdAndExamId(Long userId, Long examId);

    /**
     * Get active (IN_PROGRESS) session for a user and exam.
     */
    Optional<UserExamSessionResponse> getActiveSessionByUserIdAndExamId(Long userId, Long examId);

    /**
     * Get session by ID.
     */
    Optional<UserExamSessionResponse> getSessionById(Long sessionId);
}
