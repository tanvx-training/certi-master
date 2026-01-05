package com.certimaster.blog_service.dto.mapper;

import com.certimaster.blog_service.dto.request.TagRequest;
import com.certimaster.blog_service.dto.response.TagResponse;
import com.certimaster.blog_service.entity.PostTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between PostTag entities and DTOs.
 * Handles transformation of tag data between persistence and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TagMapper {

    /**
     * Converts a PostTag entity to TagResponse DTO.
     * Note: postCount must be set separately as it requires a query.
     *
     * @param tag the tag entity
     * @return the tag response DTO
     */
    @Mapping(target = "postCount", ignore = true)
    TagResponse toResponse(PostTag tag);

    /**
     * Converts a TagRequest DTO to PostTag entity.
     * Note: slug must be generated separately.
     *
     * @param request the tag request DTO
     * @return the tag entity
     */
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "postMappings", ignore = true)
    PostTag toEntity(TagRequest request);

    /**
     * Updates an existing PostTag entity with data from TagRequest DTO.
     *
     * @param tag the tag entity to update
     * @param request the tag request DTO with new data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "postMappings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget PostTag tag, TagRequest request);
}
