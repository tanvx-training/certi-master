package com.certimaster.exam_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Response DTO for user exam session details.
 * Used for session retrieval endpoints.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserExamSessionResponse extends BaseDto {

    /**
     * User ID who owns this session.
     */
    private Long userId;

    /**
     * Exam ID for this session.
     */
    private Long examId;

    /**
     * Certification ID associated with the exam.
     */
    private Long certificationId;

    /**
     * Certification name.
     */
    private String certificationName;

    /**
     * Start time of the session.
     */
    private LocalDateTime startTime;

    /**
     * End time of the session (null if still in progress).
     */
    private LocalDateTime endTime;

    /**
     * Session status: IN_PROGRESS, COMPLETED, ABANDONED.
     */
    private String status;

    /**
     * Exam mode: PRACTICE or TIMED.
     */
    private String mode;

    /**
     * Title of the exam.
     */
    private String examTitle;

    /**
     * Total number of questions in the exam.
     */
    private Integer totalQuestions;

    /**
     * Duration of the exam in minutes.
     */
    private Integer durationMinutes;

    /**
     * Number of questions answered.
     */
    private Integer answeredCount;

    /**
     * Number of correct answers.
     */
    private Integer correctCount;

    /**
     * Number of wrong answers.
     */
    private Integer wrongCount;

    /**
     * Number of unanswered questions.
     */
    private Integer unansweredCount;

    /**
     * Number of flagged questions.
     */
    private Integer flaggedCount;

    /**
     * Total time spent in seconds.
     */
    private Integer timeSpentSeconds;
}
