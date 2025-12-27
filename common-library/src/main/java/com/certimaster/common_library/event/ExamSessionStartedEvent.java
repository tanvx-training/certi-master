package com.certimaster.common_library.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when an exam session is started.
 * Sent from exam-service to result-service via Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSessionStartedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * User ID from auth-service.
     */
    private Long userId;

    /**
     * Exam ID.
     */
    private Long examId;

    /**
     * Certification ID.
     */
    private Long certificationId;

    /**
     * Exam mode: PRACTICE, TIMED.
     */
    private String mode;

    /**
     * Exam title.
     */
    private String examTitle;

    /**
     * Total number of questions in this session.
     */
    private Integer totalQuestions;

    /**
     * Duration in minutes.
     */
    private Integer durationMinutes;

    /**
     * Session start time.
     */
    private LocalDateTime startTime;

    /**
     * List of question IDs for this session.
     * Used to create UserAnswer records.
     */
    private List<Long> questionIds;

    /**
     * Event timestamp.
     */
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();
}
