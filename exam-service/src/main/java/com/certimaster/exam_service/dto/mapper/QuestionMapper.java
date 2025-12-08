package com.certimaster.exam_service.dto.mapper;

import com.certimaster.exam_service.dto.request.QuestionRequest;
import com.certimaster.exam_service.dto.request.QuestionWithOptionsRequest;
import com.certimaster.exam_service.dto.response.QuestionOptionResponse;
import com.certimaster.exam_service.dto.response.QuestionResponse;
import com.certimaster.exam_service.entity.Question;
import com.certimaster.exam_service.entity.QuestionOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Question entities and DTOs.
 * Handles transformation of question data between domain and API layers.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface QuestionMapper {

    /**
     * Converts a Question entity to a QuestionResponse DTO.
     * Maps question options with isCorrect field included.
     *
     * @param question the question entity
     * @return the question response DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "options", source = "questionOptions", qualifiedByName = "mapOptionsWithCorrect")
    QuestionResponse toResponse(Question question);

    /**
     * Converts a Question entity to a QuestionResponse DTO without isCorrect field.
     * Used for exam mode where correct answers should not be revealed.
     *
     * @param question the question entity
     * @return the question response DTO without correct answer indicators
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "options", source = "questionOptions", qualifiedByName = "mapOptionsWithoutCorrect")
    QuestionResponse toResponseWithoutCorrect(Question question);

    /**
     * Converts a QuestionRequest DTO to a Question entity.
     *
     * @param request the question request DTO
     * @return the question entity
     */
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "questionOptions", ignore = true)
    @Mapping(target = "examQuestions", ignore = true)
    Question toEntity(QuestionRequest request);

    /**
     * Converts a QuestionWithOptionsRequest DTO to a Question entity.
     *
     * @param request the question with options request DTO
     * @return the question entity
     */
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "questionOptions", ignore = true)
    @Mapping(target = "examQuestions", ignore = true)
    Question toEntity(QuestionWithOptionsRequest request);

    /**
     * Updates an existing Question entity with data from a QuestionRequest DTO.
     * Ignores null values in the request to allow partial updates.
     *
     * @param question the target question entity to update
     * @param request the question request DTO with updated data
     */
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "questionOptions", ignore = true)
    @Mapping(target = "examQuestions", ignore = true)
    void updateEntity(@MappingTarget Question question, QuestionRequest request);

    /**
     * Updates an existing Question entity with data from a QuestionWithOptionsRequest DTO.
     *
     * @param question the target question entity to update
     * @param request the question with options request DTO with updated data
     */
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "questionOptions", ignore = true)
    @Mapping(target = "examQuestions", ignore = true)
    void updateEntity(@MappingTarget Question question, QuestionWithOptionsRequest request);

    /**
     * Maps question options with isCorrect field included.
     * Used for practice mode or when showing answers.
     *
     * @param options the set of question option entities
     * @return list of question option response DTOs with isCorrect field
     */
    @Named("mapOptionsWithCorrect")
    default List<QuestionOptionResponse> mapOptionsWithCorrect(Set<QuestionOption> options) {
        if (options == null) {
            return null;
        }
        return options.stream()
            .map(option -> QuestionOptionResponse.builder()
                .id(option.getId())
                .content(option.getContent())
                .orderIndex(option.getOrderIndex())
                .isCorrect(option.getIsCorrect())
                .build())
            .collect(Collectors.toList());
    }

    /**
     * Maps question options without isCorrect field.
     * Used for exam mode where correct answers should not be revealed.
     *
     * @param options the set of question option entities
     * @return list of question option response DTOs without isCorrect field
     */
    @Named("mapOptionsWithoutCorrect")
    default List<QuestionOptionResponse> mapOptionsWithoutCorrect(Set<QuestionOption> options) {
        if (options == null) {
            return null;
        }
        return options.stream()
            .map(option -> QuestionOptionResponse.builder()
                .id(option.getId())
                .content(option.getContent())
                .orderIndex(option.getOrderIndex())
                .isCorrect(null)  // Explicitly set to null to exclude from JSON
                .build())
            .collect(Collectors.toList());
    }
}
