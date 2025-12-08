package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.ExamRequest;
import com.certimaster.exam_service.dto.response.ExamDetailResponse;
import com.certimaster.exam_service.dto.response.ExamQuestionResponse;
import com.certimaster.exam_service.dto.response.ExamResponse;
import com.certimaster.exam_service.entity.Exam;
import com.certimaster.exam_service.entity.ExamQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Exam entities and DTOs.
 * Handles transformation of exam data between persistence and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ExamMapper {

    /**
     * Converts an Exam entity to ExamResponse DTO.
     *
     * @param exam the exam entity
     * @return the exam response DTO
     */
    @Mapping(target = "certificationId", source = "certification.id")
    @Mapping(target = "certificationName", source = "certification.name")
    ExamResponse toResponse(Exam exam);

    /**
     * Converts an Exam entity to ExamDetailResponse DTO with nested exam questions.
     *
     * @param exam the exam entity
     * @return the detailed exam response DTO
     */
    @Mapping(target = "certificationId", source = "certification.id")
    @Mapping(target = "certificationName", source = "certification.name")
    @Mapping(target = "examQuestions", source = "examQuestions")
    ExamDetailResponse toDetailResponse(Exam exam);

    /**
     * Converts an ExamRequest DTO to Exam entity.
     * Note: Certification relationship must be set separately.
     *
     * @param request the exam request DTO
     * @return the exam entity
     */
    @Mapping(target = "certification", ignore = true)
    @Mapping(target = "examQuestions", ignore = true)
    Exam toEntity(ExamRequest request);

    /**
     * Updates an existing Exam entity with data from ExamRequest DTO.
     * Note: Certification relationship must be updated separately if needed.
     *
     * @param exam the exam entity to update
     * @param request the exam request DTO with new data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "certification", ignore = true)
    @Mapping(target = "examQuestions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget Exam exam, ExamRequest request);

    /**
     * Converts an ExamQuestion entity to ExamQuestionResponse DTO.
     *
     * @param examQuestion the exam question entity
     * @return the exam question response DTO
     */
    @Mapping(target = "examId", source = "exam.id")
    @Mapping(target = "questionId", source = "question.id")
    ExamQuestionResponse toExamQuestionResponse(ExamQuestion examQuestion);
}
