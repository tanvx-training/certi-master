package com.certimaster.exam_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user exam session from result-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExamSessionDto {

    private Long id;
    private Long userId;
    private Long examId;
    private Long certificationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String mode;
    private String examTitle;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private Integer answeredCount;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer unansweredCount;
    private Integer flaggedCount;
    private Integer timeSpentSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
