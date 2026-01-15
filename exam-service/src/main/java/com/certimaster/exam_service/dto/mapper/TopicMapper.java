package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.TopicRequest;
import com.certimaster.exam_service.dto.response.TopicResponse;
import com.certimaster.exam_service.entity.Topic;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between Topic entities and DTOs.
 * Handles transformation of topic data between domain and API layers.
 * Provides null-safe mapping operations with nested null handling.
 */
@Component
public class TopicMapper {

    /**
     * Converts a Topic entity to a TopicResponse DTO.
     *
     * @param topic the topic entity
     * @return the topic response DTO, or null if input is null
     */
    public TopicResponse toResponse(Topic topic) {
        if (topic == null) {
            return null;
        }
        return TopicResponse.builder()
                .id(topic.getId())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .createdBy(topic.getCreatedBy())
                .updatedBy(topic.getUpdatedBy())
                .certificationId(topic.getCertification() != null ? topic.getCertification().getId() : null)
                .name(topic.getName())
                .code(topic.getCode())
                .description(topic.getDescription())
                .weightPercentage(topic.getWeightPercentage())
                .orderIndex(topic.getOrderIndex())
                .build();
    }

    /**
     * Converts a TopicRequest DTO to a Topic entity.
     * The certification relationship must be set separately.
     *
     * @param request the topic request DTO
     * @return the topic entity, or null if input is null
     */
    public Topic toEntity(TopicRequest request) {
        if (request == null) {
            return null;
        }
        return Topic.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .weightPercentage(request.getWeightPercentage())
                .orderIndex(request.getOrderIndex())
                .build();
    }

    /**
     * Updates an existing Topic entity with data from a TopicRequest DTO.
     * Only updates non-null fields from the request (partial update).
     * The certification relationship is not updated.
     *
     * @param topic the target topic entity to update
     * @param request the topic request DTO with updated data
     */
    public void updateEntity(Topic topic, TopicRequest request) {
        if (topic == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            topic.setName(request.getName());
        }
        if (request.getCode() != null) {
            topic.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            topic.setDescription(request.getDescription());
        }
        if (request.getWeightPercentage() != null) {
            topic.setWeightPercentage(request.getWeightPercentage());
        }
        if (request.getOrderIndex() != null) {
            topic.setOrderIndex(request.getOrderIndex());
        }
    }
}
