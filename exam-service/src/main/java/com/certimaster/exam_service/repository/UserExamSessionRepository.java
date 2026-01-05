package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.UserExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserExamSession entity.
 * Provides methods for session management and retrieval.
 */
@Repository
public interface UserExamSessionRepository extends JpaRepository<UserExamSession, Long> {

    /**
     * Find a session by user ID, exam ID, and status.
     * Used to check for existing active sessions before starting a new one.
     *
     * @param userId the user ID
     * @param examId the exam ID
     * @param status the session status (e.g., "IN_PROGRESS")
     * @return the session if found
     */
    Optional<UserExamSession> findByUserIdAndExam_IdAndStatus(Long userId, Long examId, String status);

    /**
     * Find all sessions for a user with a specific status.
     * Used to retrieve active sessions for a user.
     *
     * @param userId the user ID
     * @param status the session status
     * @return list of sessions matching the criteria
     */
    List<UserExamSession> findByUserIdAndStatus(Long userId, String status);

    /**
     * Find a session by ID and user ID.
     * Used to validate session ownership before operations.
     *
     * @param id the session ID
     * @param userId the user ID
     * @return the session if found and owned by the user
     */
    Optional<UserExamSession> findByIdAndUserId(Long id, Long userId);
}
