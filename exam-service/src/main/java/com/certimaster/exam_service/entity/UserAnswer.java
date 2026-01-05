package com.certimaster.exam_service.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entity representing a user's answer to a question within an exam session.
 * Migrated from result-service to exam-service for local session management.
 * Includes relationship to Question and Topic for result calculation.
 */
@Entity
@Table(name = "user_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private UserExamSession userExamSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "selected_option_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] selectedOptionIds;

    @Column(name = "correct_option_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] correctOptionIds;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "is_flagged")
    @Builder.Default
    private Boolean isFlagged = false;

    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
}
