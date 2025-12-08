package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.TagRequest;
import com.certimaster.exam_service.dto.response.TagResponse;
import com.certimaster.exam_service.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Tag entities and DTOs.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TagMapper {

    /**
     * Converts a Tag entity to a TagResponse DTO.
     */
    TagResponse toResponse(Tag tag);

    /**
     * Converts a TagRequest DTO to a Tag entity.
     */
    Tag toEntity(TagRequest request);

    /**
     * Updates an existing Tag entity with data from a TagRequest DTO.
     */
    void updateEntity(@MappingTarget Tag tag, TagRequest request);
}
