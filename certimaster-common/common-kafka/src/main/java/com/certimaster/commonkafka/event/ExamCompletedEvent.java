package com.certimaster.commonkafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event published when user completes an exam
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExamCompletedEvent extends BaseEvent {

    private Long attemptId;
    private Long userId;
    private Long examFormatId;
    private BigDecimal score;
    private String status; // PASSED, FAILED
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Long durationSeconds;

    public ExamCompletedEvent(Long attemptId, Long userId, Long examFormatId,
                              BigDecimal score, String status) {
        super();
        this.attemptId = attemptId;
        this.userId = userId;
        this.examFormatId = examFormatId;
        this.score = score;
        this.status = status;
        this.setEventType("EXAM_COMPLETED");
        init();
    }
}
