package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.TagRequest;
import com.certimaster.blog_service.dto.response.TagResponse;
import com.certimaster.blog_service.entity.PostTag;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between PostTag entities and DTOs.
 * Provides null-safe mapping operations.
 */
@Component
public class TagMapper {

    /**
     * Converts a PostTag entity to TagResponse DTO.
     * Note: postCount must be set separately as it requires a query.
     *
     * @param tag the tag entity
     * @return the tag response DTO, or null if input is null
     */
    public TagResponse toResponse(PostTag tag) {
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
                .slug(tag.getSlug())
                .build();
    }

    /**
     * Converts a TagRequest DTO to PostTag entity.
     * Note: slug must be generated separately.
     *
     * @param request the tag request DTO
     * @return the tag entity, or null if input is null
     */
    public PostTag toEntity(TagRequest request) {
        if (request == null) {
            return null;
        }
        return PostTag.builder()
                .name(request.getName())
                .build();
    }

    /**
     * Updates an existing PostTag entity with data from TagRequest DTO.
     * Only updates non-null fields from the request (partial update).
     *
     * @param tag the tag entity to update
     * @param request the tag request DTO with new data
     */
    public void updateEntity(PostTag tag, TagRequest request) {
        if (tag == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            tag.setName(request.getName());
        }
    }
}
