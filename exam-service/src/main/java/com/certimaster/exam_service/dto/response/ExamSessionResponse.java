package com.certimaster.exam_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for an active exam session.
 * Contains all information about the current state of an exam session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSessionResponse {

    /**
     * Unique identifier for the exam session.
     */
    private String sessionId;

    /**
     * ID of the exam being taken.
     */
    private Long examId;

    /**
     * Title of the exam.
     */
    private String examTitle;

    /**
     * Mode of the exam session (PRACTICE, TIMED).
     */
    private String mode;

    /**
     * Status of the exam session.
     */
    private String status;

    /**
     * Start time of the exam session.
     */
    private LocalDateTime startTime;

    /**
     * Duration of the exam in minutes.
     */
    private Integer duration;

    /**
     * Time remaining in minutes.
     */
    private Integer timeRemaining;

    /**
     * Total number of questions in the exam.
     */
    private Integer totalQuestions;

    /**
     * Current question index (0-based).
     */
    private Integer currentQuestionIndex;

    /**
     * Number of questions answered.
     */
    private Integer answeredCount;

    /**
     * Number of questions flagged for review.
     */
    private Integer flaggedCount;

    /**
     * List of questions in the exam session.
     * Note: QuestionResponse will be created in Task 5.
     */
    private List<QuestionResponse> questions;
}
