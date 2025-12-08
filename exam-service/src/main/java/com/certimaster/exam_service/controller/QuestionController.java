package com.certimaster.exam_service.controller;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.request.QuestionSearchRequest;
import com.certimaster.exam_service.dto.request.QuestionWithOptionsRequest;
import com.certimaster.exam_service.dto.response.QuestionResponse;
import com.certimaster.exam_service.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Question API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Search questions with filters and pagination.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<PageDto<QuestionResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.debug("Search questions - keyword: {}, topicId: {}, type: {}, difficulty: {}",
                keyword, topicId, type, difficulty);

        QuestionSearchRequest request = QuestionSearchRequest.builder()
                .keyword(keyword)
                .topicId(topicId)
                .type(type)
                .difficulty(difficulty)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageDto<QuestionResponse> result = questionService.search(request);
        return ResponseEntity.ok(ResponseDto.success(result));
    }


    /**
     * Get question by ID (with correct answers - for admin/practice mode).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<QuestionResponse>> getById(@PathVariable Long id) {
        log.debug("Get question by id: {}", id);

        QuestionResponse result = questionService.getById(id);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get question by ID for exam mode (without correct answers).
     */
    @GetMapping("/{id}/exam")
    public ResponseEntity<ResponseDto<QuestionResponse>> getByIdForExam(@PathVariable Long id) {
        log.debug("Get question by id for exam: {}", id);

        QuestionResponse result = questionService.getByIdForExam(id);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get questions by topic ID.
     */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ResponseDto<List<QuestionResponse>>> getByTopicId(
            @PathVariable Long topicId
    ) {
        log.debug("Get questions by topic id: {}", topicId);

        List<QuestionResponse> result = questionService.getByTopicId(topicId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Create a new question with options.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<QuestionResponse>> create(
            @Valid @RequestBody QuestionWithOptionsRequest request
    ) {
        log.debug("Create question for topic: {}", request.getTopicId());

        QuestionResponse result = questionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Question created successfully", result));
    }

    /**
     * Update an existing question with options.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<QuestionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody QuestionWithOptionsRequest request
    ) {
        log.debug("Update question with id: {}", id);

        QuestionResponse result = questionService.update(id, request);
        return ResponseEntity.ok(ResponseDto.success("Question updated successfully", result));
    }

    /**
     * Delete a question.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        log.debug("Delete question with id: {}", id);

        questionService.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Question deleted successfully", null));
    }
}
