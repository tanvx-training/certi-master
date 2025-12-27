package com.certimaster.resultservice.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Response DTO for user exam session details.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserExamSessionResponse extends BaseDto {

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
}
