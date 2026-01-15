package com.certimaster.result_service.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing historical performance data for analytics.
 */
@Entity
@Table(name = "performance_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceHistory extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "certification_id", nullable = false)
    private Long certificationId;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
}
