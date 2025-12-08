package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.TopicRequest;
import com.certimaster.exam_service.dto.response.TopicResponse;
import com.certimaster.exam_service.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Topic entities and DTOs.
 * Handles transformation of topic data between domain and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TopicMapper {

    /**
     * Converts a Topic entity to a TopicResponse DTO.
     *
     * @param topic the topic entity
     * @return the topic response DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "certificationId", source = "certification.id")
    TopicResponse toResponse(Topic topic);

    /**
     * Converts a TopicRequest DTO to a Topic entity.
     * The certification relationship must be set separately.
     *
     * @param request the topic request DTO
     * @return the topic entity
     */
    @Mapping(target = "certification", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Topic toEntity(TopicRequest request);

    /**
     * Updates an existing Topic entity with data from a TopicRequest DTO.
     * Ignores null values in the request to allow partial updates.
     * The certification relationship is not updated.
     *
     * @param topic the target topic entity to update
     * @param request the topic request DTO with updated data
     */
    @Mapping(target = "certification", ignore = true)
    @Mapping(target = "questions", ignore = true)
    void updateEntity(@MappingTarget Topic topic, TopicRequest request);
}
