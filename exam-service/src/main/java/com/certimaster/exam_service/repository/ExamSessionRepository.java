package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {

    /**
     * Find active session by user and exam.
     */
    @Query("SELECT s FROM ExamSession s WHERE s.userId = :userId AND s.exam.id = :examId AND s.status = 'IN_PROGRESS'")
    Optional<ExamSession> findActiveSession(@Param("userId") Long userId, @Param("examId") Long examId);

    /**
     * Find all sessions by user.
     */
    List<ExamSession> findByUserIdOrderByStartTimeDesc(Long userId);

    /**
     * Find sessions by user and exam.
     */
    List<ExamSession> findByUserIdAndExamIdOrderByStartTimeDesc(Long userId, Long examId);

    /**
     * Check if user has active session for exam.
     */
    boolean existsByUserIdAndExamIdAndStatus(Long userId, Long examId, String status);
}
