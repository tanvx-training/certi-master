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
 * Event published when a user completes an exam session.
 * Contains full session data and all user answers for result calculation.
 * Sent from exam-service to result-service via Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamCompletedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Session ID from exam-service.
     */
    private Long sessionId;

    /**
     * User ID.
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
     * Exam mode (PRACTICE or TIMED).
     */
    private String mode;

    /**
     * Exam title.
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
     * Passing score percentage.
     */
    private Integer passingScore;

    /**
     * Session start time.
     */
    private LocalDateTime startTime;

    /**
     * Session end time.
     */
    private LocalDateTime endTime;

    /**
     * Number of answered questions.
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
     * Number of flagged questions.
     */
    private Integer flaggedCount;

    /**
     * Total time spent in seconds.
     */
    private Integer timeSpentSeconds;

    /**
     * List of all user answers with question metadata.
     */
    private List<UserAnswerData> answers;

    /**
     * Event timestamp.
     */
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();

    /**
     * Nested class containing user answer data with question metadata.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnswerData implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Question ID.
         */
        private Long questionId;

        /**
         * Topic ID for performance tracking.
         */
        private Long topicId;

        /**
         * Topic name.
         */
        private String topicName;

        /**
         * Selected option IDs by the user.
         */
        private Long[] selectedOptionIds;

        /**
         * Correct option IDs for the question.
         */
        private Long[] correctOptionIds;

        /**
         * Whether the answer is correct.
         */
        private Boolean isCorrect;

        /**
         * Whether the question was flagged.
         */
        private Boolean isFlagged;

        /**
         * Time spent on this question in seconds.
         */
        private Integer timeSpentSeconds;

        /**
         * When the answer was submitted.
         */
        private LocalDateTime answeredAt;

        /**
         * Question text for result display.
         */
        private String questionText;

        /**
         * Explanation for the correct answer.
         */
        private String explanation;

        /**
         * Reference material for further study.
         */
        private String reference;
    }
}
