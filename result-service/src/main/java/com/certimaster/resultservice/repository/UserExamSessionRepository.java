package com.certimaster.resultservice.repository;

import com.certimaster.resultservice.entity.UserExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserExamSessionRepository extends JpaRepository<UserExamSession, Long> {

    List<UserExamSession> findByUserIdOrderByStartTimeDesc(Long userId);

    List<UserExamSession> findByUserIdAndExamIdOrderByStartTimeDesc(Long userId, Long examId);

    List<UserExamSession> findByUserIdAndCertificationIdOrderByStartTimeDesc(Long userId, Long certificationId);

    /**
     * Find active (IN_PROGRESS) session for a user and exam.
     */
    @Query("SELECT s FROM UserExamSession s WHERE s.userId = :userId AND s.examId = :examId AND s.status = 'IN_PROGRESS' ORDER BY s.startTime DESC")
    Optional<UserExamSession> findActiveByUserIdAndExamId(@Param("userId") Long userId, @Param("examId") Long examId);
}
