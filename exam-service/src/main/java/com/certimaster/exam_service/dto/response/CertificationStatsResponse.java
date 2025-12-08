package com.certimaster.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for certification statistics.
 * Provides aggregated data about certification usage and performance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationStatsResponse {

    /**
     * The total number of users enrolled in this certification.
     */
    private Long totalEnrollments;

    /**
     * The total number of exams taken for this certification.
     */
    private Long totalExamsTaken;

    /**
     * The average score achieved across all exam attempts.
     */
    private BigDecimal averageScore;

    /**
     * The percentage of exam attempts that resulted in a passing score.
     */
    private BigDecimal passRate;
}
