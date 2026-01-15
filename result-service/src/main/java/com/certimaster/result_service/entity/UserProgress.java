package com.certimaster.result_service.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a user's overall progress for a certification.
 */
@Entity
@Table(name = "user_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserProgress extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "certification_id", nullable = false)
    private Long certificationId;

    @Column(name = "total_exams_taken")
    @Builder.Default
    private Integer totalExamsTaken = 0;

    @Column(name = "total_questions_answered")
    @Builder.Default
    private Integer totalQuestionsAnswered = 0;

    @Column(name = "correct_answers")
    @Builder.Default
    private Integer correctAnswers = 0;

    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;

    @Column(name = "best_score", precision = 5, scale = 2)
    private BigDecimal bestScore;

    @Column(name = "latest_exam_date")
    private LocalDateTime latestExamDate;
}
