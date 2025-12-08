package com.certimaster.exam_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Response DTO for question data.
 * Extends BaseDto to include id, timestamps, and audit fields.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuestionResponse extends BaseDto {

    private String type;
    
    private String content;
    
    private String explanation;
    
    private String difficulty;
    
    private Integer points;
    
    private Integer timeLimitSeconds;
    
    private String referenceUrl;
    
    private Long topicId;
    
    private List<QuestionOptionResponse> options;
}
