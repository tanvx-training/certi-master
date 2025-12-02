package com.certimaster.examservice.entity;

import com.certimaster.commonlibrary.entity.BaseEntity;
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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "provider", length = 100)
    private String provider;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "level", length = 50)
    private String level;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "passing_score")
    private BigDecimal passingScore;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status", length = 20)
    private String status;

    @Builder.Default
    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Topic> topics = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Exam> exams = new HashSet<>();
}
