package com.certimaster.exam_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for exam question data.
 * Represents the relationship between an exam and a question with ordering information.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamQuestionResponse extends BaseDto {

    /**
     * ID of the exam this question belongs to.
     */
    private Long examId;

    /**
     * ID of the question.
     */
    private Long questionId;

    /**
     * Order index of the question in the exam.
     */
    private Integer orderIndex;
    
    // Note: id, createdAt, updatedAt, createdBy, updatedBy inherited from BaseDto
}
