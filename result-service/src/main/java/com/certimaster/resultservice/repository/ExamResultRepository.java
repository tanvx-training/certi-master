package com.certimaster.resultservice.repository;

import com.certimaster.resultservice.entity.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    /**
     * Find result by session ID.
     */
    Optional<ExamResult> findBySessionId(Long sessionId);

    /**
     * Find all results by user ID.
     */
    List<ExamResult> findByUserIdOrderByCompletedAtDesc(Long userId);

    /**
     * Find results by user and certification.
     */
    List<ExamResult> findByUserIdAndCertificationIdOrderByCompletedAtDesc(Long userId, Long certificationId);

    /**
     * Find results by user and exam.
     */
    List<ExamResult> findByUserIdAndExamIdOrderByCompletedAtDesc(Long userId, Long examId);

    /**
     * Search results with pagination.
     */
    @Query("""
            SELECT r FROM ExamResult r
            WHERE (:userId IS NULL OR r.userId = :userId)
            AND (:certificationId IS NULL OR r.certificationId = :certificationId)
            AND (:examId IS NULL OR r.examId = :examId)
            AND (:passStatus IS NULL OR :passStatus = '' OR r.passStatus = :passStatus)
            ORDER BY r.completedAt DESC
            """)
    Page<ExamResult> search(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId,
            @Param("examId") Long examId,
            @Param("passStatus") String passStatus,
            Pageable pageable
    );

    /**
     * Count passed exams by user and certification.
     */
    long countByUserIdAndCertificationIdAndPassStatus(Long userId, Long certificationId, String passStatus);

    /**
     * Get average score by user and certification.
     */
    @Query("SELECT AVG(r.percentage) FROM ExamResult r WHERE r.userId = :userId AND r.certificationId = :certificationId")
    Double getAverageScoreByUserAndCertification(@Param("userId") Long userId, @Param("certificationId") Long certificationId);

    /**
     * Get best score by user and certification.
     */
    @Query("SELECT MAX(r.percentage) FROM ExamResult r WHERE r.userId = :userId AND r.certificationId = :certificationId")
    Double getBestScoreByUserAndCertification(@Param("userId") Long userId, @Param("certificationId") Long certificationId);
}
