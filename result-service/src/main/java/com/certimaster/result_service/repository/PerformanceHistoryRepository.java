package com.certimaster.result_service.repository;

import com.certimaster.result_service.entity.PerformanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PerformanceHistoryRepository extends JpaRepository<PerformanceHistory, Long> {

    /**
     * Find history by user and certification.
     */
    List<PerformanceHistory> findByUserIdAndCertificationIdOrderByExamDateDesc(Long userId, Long certificationId);

    /**
     * Find history by user within date range.
     */
    @Query("""
            SELECT ph FROM PerformanceHistory ph
            WHERE ph.userId = :userId
            AND ph.examDate BETWEEN :startDate AND :endDate
            ORDER BY ph.examDate ASC
            """)
    List<PerformanceHistory> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Get performance trend for a user and certification.
     */
    @Query("""
            SELECT ph.examDate, AVG(ph.percentage) as avgPercentage
            FROM PerformanceHistory ph
            WHERE ph.userId = :userId AND ph.certificationId = :certificationId
            GROUP BY ph.examDate
            ORDER BY ph.examDate ASC
            """)
    List<Object[]> getPerformanceTrend(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId
    );

    /**
     * Get daily performance summary for a user.
     */
    @Query("""
            SELECT ph.examDate, COUNT(ph) as examCount, AVG(ph.percentage) as avgPercentage
            FROM PerformanceHistory ph
            WHERE ph.userId = :userId
            AND ph.examDate >= :startDate
            GROUP BY ph.examDate
            ORDER BY ph.examDate ASC
            """)
    List<Object[]> getDailyPerformanceSummary(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate
    );
}
