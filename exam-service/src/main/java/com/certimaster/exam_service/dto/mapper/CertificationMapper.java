package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.CertificationRequest;
import com.certimaster.exam_service.dto.response.CertificationDetailResponse;
import com.certimaster.exam_service.dto.response.CertificationResponse;
import com.certimaster.exam_service.entity.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Certification entities and DTOs.
 * Handles transformation of certification data between domain and API layers.
 */
@Mapper(
    componentModel = "spring",
    uses = {TopicMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CertificationMapper {

    /**
     * Converts a Certification entity to a CertificationResponse DTO.
     *
     * @param certification the certification entity
     * @return the certification response DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    CertificationResponse toResponse(Certification certification);

    /**
     * Converts a Certification entity to a CertificationDetailResponse DTO.
     * Includes nested topic and exam information.
     *
     * @param certification the certification entity
     * @return the detailed certification response DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "topics", source = "topics")
    @Mapping(target = "exams", source = "exams")
    CertificationDetailResponse toDetailResponse(Certification certification);

    /**
     * Converts a CertificationRequest DTO to a Certification entity.
     *
     * @param request the certification request DTO
     * @return the certification entity
     */
    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "exams", ignore = true)
    Certification toEntity(CertificationRequest request);

    /**
     * Updates an existing Certification entity with data from a CertificationRequest DTO.
     * Ignores null values in the request to allow partial updates.
     *
     * @param certification the target certification entity to update
     * @param request the certification request DTO with updated data
     */
    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "exams", ignore = true)
    void updateEntity(@MappingTarget Certification certification, CertificationRequest request);
}
