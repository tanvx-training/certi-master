package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.CategoryRequest;
import com.certimaster.blog_service.dto.response.CategoryResponse;
import com.certimaster.blog_service.entity.PostCategory;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between PostCategory entities and DTOs.
 * Provides null-safe mapping operations.
 */
@Component
public class CategoryMapper {

    /**
     * Converts a PostCategory entity to CategoryResponse DTO.
     * Note: postCount must be set separately as it requires a query.
     *
     * @param category the category entity
     * @return the category response DTO, or null if input is null
     */
    public CategoryResponse toResponse(PostCategory category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(category.getId())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .build();
    }

    /**
     * Converts a CategoryRequest DTO to PostCategory entity.
     * Note: slug must be generated separately.
     *
     * @param request the category request DTO
     * @return the category entity, or null if input is null
     */
    public PostCategory toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }
        return PostCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    /**
     * Updates an existing PostCategory entity with data from CategoryRequest DTO.
     * Only updates non-null fields from the request (partial update).
     *
     * @param category the category entity to update
     * @param request the category request DTO with new data
     */
    public void updateEntity(PostCategory category, CategoryRequest request) {
        if (category == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
    }
}
