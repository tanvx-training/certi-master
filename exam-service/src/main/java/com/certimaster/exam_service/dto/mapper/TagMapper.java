package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.TagRequest;
import com.certimaster.exam_service.dto.response.TagResponse;
import com.certimaster.exam_service.entity.Tag;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between Tag entities and DTOs.
 * Provides null-safe mapping operations.
 */
@Component
public class TagMapper {

    /**
     * Converts a Tag entity to a TagResponse DTO.
     *
     * @param tag the tag entity
     * @return the tag response DTO, or null if input is null
     */
    public TagResponse toResponse(Tag tag) {
        if (tag == null) {
            return null;
        }
        return TagResponse.builder()
                .id(tag.getId())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .createdBy(tag.getCreatedBy())
                .updatedBy(tag.getUpdatedBy())
                .name(tag.getName())
                .status(tag.getStatus())
                .build();
    }

    /**
     * Converts a TagRequest DTO to a Tag entity.
     *
     * @param request the tag request DTO
     * @return the tag entity, or null if input is null
     */
    public Tag toEntity(TagRequest request) {
        if (request == null) {
            return null;
        }
        return Tag.builder()
                .name(request.getName())
                .status(request.getStatus())
                .build();
    }

    /**
     * Updates an existing Tag entity with data from a TagRequest DTO.
     * Only updates non-null fields from the request (partial update).
     *
     * @param tag the target tag entity to update
     * @param request the tag request DTO with updated data
     */
    public void updateEntity(Tag tag, TagRequest request) {
        if (tag == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            tag.setName(request.getName());
        }
        if (request.getStatus() != null) {
            tag.setStatus(request.getStatus());
        }
    }
}
