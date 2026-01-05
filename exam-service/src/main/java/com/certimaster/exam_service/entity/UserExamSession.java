package com.certimaster.exam_service.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user's exam session.
 * Migrated from result-service to exam-service for local session management.
 */
@Entity
@Table(name = "user_exam_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserExamSession extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id")
    private Certification certification;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @Column(name = "mode", length = 20, nullable = false)
    private String mode;

    @Column(name = "exam_title", length = 500)
    private String examTitle;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "answered_count")
    @Builder.Default
    private Integer answeredCount = 0;

    @Column(name = "correct_count")
    @Builder.Default
    private Integer correctCount = 0;

    @Column(name = "wrong_count")
    @Builder.Default
    private Integer wrongCount = 0;

    @Column(name = "unanswered_count")
    private Integer unansweredCount;

    @Column(name = "flagged_count")
    @Builder.Default
    private Integer flaggedCount = 0;

    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @Builder.Default
    @OneToMany(mappedBy = "userExamSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserAnswer> userAnswers = new HashSet<>();
}
