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

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "type", length = 50, nullable = false)
    private String type;  // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "difficulty", length = 20)
    private String difficulty; // EASY, MEDIUM, HARD

    @Column(name = "points")
    private Integer points;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "reference_url", length = 500)
    private String referenceUrl;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<QuestionOption> questionOptions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ExamQuestion> examQuestions = new HashSet<>();
}
