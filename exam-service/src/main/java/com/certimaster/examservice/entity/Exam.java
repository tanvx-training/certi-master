package com.certimaster.examservice.entity;

import com.certimaster.commonlibrary.entity.BaseEntity;
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
@Table(name = "exams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "title")
    private String title;

    @Column(name = "type", length = 50)
    private String type; // PRACTICE, MOCK, FINAL, DIAGNOSTIC, TOPIC_WISE

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "status", length = 20)
    private String status;

    @Builder.Default
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ExamQuestion> examQuestions = new HashSet<>();
}
