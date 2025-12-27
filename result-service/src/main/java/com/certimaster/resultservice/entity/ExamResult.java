package com.certimaster.resultservice.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing the final result of an exam session.
 */
@Entity
@Table(name = "exam_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExamResult extends BaseEntity {

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "certification_id", nullable = false)
    private Long certificationId;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "wrong_answers", nullable = false)
    private Integer wrongAnswers;

    @Column(name = "unanswered", nullable = false)
    private Integer unanswered;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "pass_status", nullable = false, length = 20)
    private String passStatus; // PASSED, FAILED

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Builder.Default
    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TopicPerformance> topicPerformances = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionResult> questionResults = new ArrayList<>();
}
