package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.CategoryRequest;
import com.certimaster.blog_service.dto.response.CategoryResponse;
import com.certimaster.blog_service.entity.PostCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between PostCategory entities and DTOs.
 * Handles transformation of category data between persistence and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {

    /**
     * Converts a PostCategory entity to CategoryResponse DTO.
     * Note: postCount must be set separately as it requires a query.
     *
     * @param category the category entity
     * @return the category response DTO
     */
    @Mapping(target = "postCount", ignore = true)
    CategoryResponse toResponse(PostCategory category);

    /**
     * Converts a CategoryRequest DTO to PostCategory entity.
     * Note: slug must be generated separately.
     *
     * @param request the category request DTO
     * @return the category entity
     */
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "postMappings", ignore = true)
    PostCategory toEntity(CategoryRequest request);

    /**
     * Updates an existing PostCategory entity with data from CategoryRequest DTO.
     *
     * @param category the category entity to update
     * @param request the category request DTO with new data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "postMappings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget PostCategory category, CategoryRequest request);
}
