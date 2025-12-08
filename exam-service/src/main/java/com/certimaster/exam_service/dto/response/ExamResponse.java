package com.certimaster.exam_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for exam data.
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExamResponse extends BaseDto {

    /**
     * Title of the exam.
     */
    private String title;

    /**
     * Type of the exam (PRACTICE, MOCK, FINAL, DIAGNOSTIC, TOPIC_WISE).
     */
    private String type;

    /**
     * Description of the exam.
     */
    private String description;

    /**
     * Duration of the exam in minutes.
     */
    private Integer durationMinutes;

    /**
     * Total number of questions in the exam.
     */
    private Integer totalQuestions;

    /**
     * Passing score required to pass the exam.
     */
    private Integer passingScore;

    /**
     * Status of the exam (ACTIVE, INACTIVE, DRAFT).
     */
    private String status;

    /**
     * ID of the certification this exam belongs to.
     */
    private Long certificationId;

    /**
     * Name of the certification this exam belongs to.
     */
    private String certificationName;
    
    // Note: id, createdAt, updatedAt, createdBy, updatedBy inherited from BaseDto
}
