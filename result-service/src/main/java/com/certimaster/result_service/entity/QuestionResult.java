package com.certimaster.result_service.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entity representing the result of a single question in an exam.
 */
@Entity
@Table(name = "question_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QuestionResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private ExamResult examResult;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "user_answer_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] userAnswerIds;

    @Column(name = "correct_answer_ids", columnDefinition = "bigint[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] correctAnswerIds;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;
}
