package com.certimaster.result_service.repository;

import com.certimaster.result_service.entity.TopicPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicPerformanceRepository extends JpaRepository<TopicPerformance, Long> {

    /**
     * Find topic performances by result ID.
     */
    List<TopicPerformance> findByExamResultId(Long resultId);

    /**
     * Get average performance by topic for a user.
     */
    @Query("""
            SELECT tp.topicId, tp.topicName, AVG(tp.percentage) as avgPercentage, COUNT(tp) as attempts
            FROM TopicPerformance tp
            JOIN tp.examResult r
            WHERE r.userId = :userId
            GROUP BY tp.topicId, tp.topicName
            ORDER BY avgPercentage ASC
            """)
    List<Object[]> getAveragePerformanceByTopicForUser(@Param("userId") Long userId);

    /**
     * Get weak topics for a user (below threshold).
     */
    @Query("""
            SELECT tp.topicId, tp.topicName, AVG(tp.percentage) as avgPercentage
            FROM TopicPerformance tp
            JOIN tp.examResult r
            WHERE r.userId = :userId AND r.certificationId = :certificationId
            GROUP BY tp.topicId, tp.topicName
            HAVING AVG(tp.percentage) < :threshold
            ORDER BY avgPercentage ASC
            """)
    List<Object[]> getWeakTopicsForUser(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId,
            @Param("threshold") Double threshold
    );
}
