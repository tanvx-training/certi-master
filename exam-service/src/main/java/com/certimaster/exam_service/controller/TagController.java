package com.certimaster.exam_service.controller;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.request.TagRequest;
import com.certimaster.exam_service.dto.request.TagSearchRequest;
import com.certimaster.exam_service.dto.response.TagResponse;
import com.certimaster.exam_service.service.TagService;
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
 * REST Controller for Tag API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * Search tags with filters and pagination.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<PageDto<TagResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        log.debug("Search tags - keyword: {}, status: {}", keyword, status);

        TagSearchRequest request = TagSearchRequest.builder()
                .keyword(keyword)
                .status(status)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageDto<TagResponse> result = tagService.search(request);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get all tags.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<TagResponse>>> getAll() {
        log.debug("Get all tags");

        List<TagResponse> result = tagService.getAll();
        return ResponseEntity.ok(ResponseDto.success(result));
    }


    /**
     * Get tag by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<TagResponse>> getById(@PathVariable Long id) {
        log.debug("Get tag by id: {}", id);

        TagResponse result = tagService.getById(id);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get tags by question ID.
     */
    @GetMapping("/question/{questionId}")
    public ResponseEntity<ResponseDto<List<TagResponse>>> getByQuestionId(
            @PathVariable Long questionId
    ) {
        log.debug("Get tags by question id: {}", questionId);

        List<TagResponse> result = tagService.getByQuestionId(questionId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Create a new tag.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<TagResponse>> create(
            @Valid @RequestBody TagRequest request
    ) {
        log.debug("Create tag with name: {}", request.getName());

        TagResponse result = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Tag created successfully", result));
    }

    /**
     * Update an existing tag.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<TagResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request
    ) {
        log.debug("Update tag with id: {}", id);

        TagResponse result = tagService.update(id, request);
        return ResponseEntity.ok(ResponseDto.success("Tag updated successfully", result));
    }

    /**
     * Delete a tag.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        log.debug("Delete tag with id: {}", id);

        tagService.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Tag deleted successfully", null));
    }

    /**
     * Add tags to a question.
     */
    @PostMapping("/question/{questionId}")
    public ResponseEntity<ResponseDto<Void>> addTagsToQuestion(
            @PathVariable Long questionId,
            @RequestBody List<Long> tagIds
    ) {
        log.debug("Add tags {} to question {}", tagIds, questionId);

        tagService.addTagsToQuestion(questionId, tagIds);
        return ResponseEntity.ok(ResponseDto.success("Tags added to question successfully", null));
    }

    /**
     * Remove tags from a question.
     */
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<ResponseDto<Void>> removeTagsFromQuestion(
            @PathVariable Long questionId,
            @RequestBody List<Long> tagIds
    ) {
        log.debug("Remove tags {} from question {}", tagIds, questionId);

        tagService.removeTagsFromQuestion(questionId, tagIds);
        return ResponseEntity.ok(ResponseDto.success("Tags removed from question successfully", null));
    }
}
