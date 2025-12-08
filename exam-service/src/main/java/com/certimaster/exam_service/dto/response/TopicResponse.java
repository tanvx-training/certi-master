package com.certimaster.exam_service.dto.response;

import com.certimaster.common_library.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Response DTO for topic information.
 * Extends BaseDto to inherit id, timestamps, and audit fields.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TopicResponse extends BaseDto {

    /**
     * The ID of the certification this topic belongs to.
     */
    private Long certificationId;

    /**
     * The name of the topic.
     */
    private String name;

    /**
     * The unique code identifying the topic.
     */
    private String code;

    /**
     * A detailed description of the topic.
     */
    private String description;

    /**
     * The weight percentage of this topic in the certification.
     */
    private BigDecimal weightPercentage;

    /**
     * The order index for displaying topics.
     */
    private Integer orderIndex;
}
