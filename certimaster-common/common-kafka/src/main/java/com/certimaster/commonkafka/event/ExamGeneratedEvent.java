package com.certimaster.commonkafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Event published when exam is generated
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExamGeneratedEvent extends BaseEvent {

    private Long examFormatId;
    private String examName;
    private Integer totalQuestions;
    private Long generatedBy;

    public ExamGeneratedEvent(Long examFormatId, String examName, Integer totalQuestions) {
        super();
        this.examFormatId = examFormatId;
        this.examName = examName;
        this.totalQuestions = totalQuestions;
        this.setEventType("EXAM_GENERATED");
        init();
    }
}
