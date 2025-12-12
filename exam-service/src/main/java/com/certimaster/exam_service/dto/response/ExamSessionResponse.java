package com.certimaster.exam_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for an active exam session.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamSessionResponse extends BaseDto {

    /**
     * ID of the exam being taken.
     */
    private Long examId;

    /**
     * Title of the exam.
     */
    private String examTitle;

    /**
     * Certification ID.
     */
    private Long certificationId;

    /**
     * Certification name.
     */
    private String certificationName;

    /**
     * Mode of the exam session (PRACTICE, EXAM).
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
     * End time of the exam session.
     */
    private LocalDateTime endTime;

    /**
     * Duration of the exam in minutes.
     */
    private Integer durationMinutes;

    /**
     * Passing score percentage.
     */
    private Integer passingScore;

    /**
     * Total number of questions in the exam.
     */
    private Integer totalQuestions;

    /**
     * Current question index (0-based).
     */
    private Integer currentQuestionIndex;

    /**
     * List of questions in the exam session (without correct answers).
     */
    private List<QuestionResponse> questions;
}
