package com.certimaster.exam_service.controller;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.request.TopicRequest;
import com.certimaster.exam_service.dto.request.TopicSearchRequest;
import com.certimaster.exam_service.dto.response.TopicResponse;
import com.certimaster.exam_service.service.TopicService;
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
 * REST Controller for Topic API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /**
     * Search topics with filters and pagination.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<PageDto<TopicResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long certificationId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "orderIndex") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        log.debug("Search topics - keyword: {}, certificationId: {}", keyword, certificationId);

        TopicSearchRequest request = TopicSearchRequest.builder()
                .keyword(keyword)
                .certificationId(certificationId)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageDto<TopicResponse> result = topicService.search(request);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get topic by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<TopicResponse>> getById(@PathVariable Long id) {
        log.debug("Get topic by id: {}", id);

        TopicResponse result = topicService.getById(id);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get topics by certification ID.
     */
    @GetMapping("/certification/{certificationId}")
    public ResponseEntity<ResponseDto<List<TopicResponse>>> getByCertificationId(
            @PathVariable Long certificationId
    ) {
        log.debug("Get topics by certification id: {}", certificationId);

        List<TopicResponse> result = topicService.getByCertificationId(certificationId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Create a new topic.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<TopicResponse>> create(
            @Valid @RequestBody TopicRequest request
    ) {
        log.debug("Create topic with name: {}", request.getName());

        TopicResponse result = topicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Topic created successfully", result));
    }

    /**
     * Update an existing topic.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<TopicResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request
    ) {
        log.debug("Update topic with id: {}", id);

        TopicResponse result = topicService.update(id, request);
        return ResponseEntity.ok(ResponseDto.success("Topic updated successfully", result));
    }

    /**
     * Delete a topic.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        log.debug("Delete topic with id: {}", id);

        topicService.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Topic deleted successfully", null));
    }
}
