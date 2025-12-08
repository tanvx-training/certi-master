package com.certimaster.exam_service.controller;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.request.ExamRequest;
import com.certimaster.exam_service.dto.request.ExamSearchRequest;
import com.certimaster.exam_service.dto.response.ExamDetailResponse;
import com.certimaster.exam_service.dto.response.ExamResponse;
import com.certimaster.exam_service.service.ExamService;
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
 * REST Controller for Exam API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    /**
     * Search exams with filters and pagination.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<PageDto<ExamResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long certificationId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.debug("Search exams - keyword: {}, certificationId: {}, type: {}, status: {}",
                keyword, certificationId, type, status);

        ExamSearchRequest request = ExamSearchRequest.builder()
                .keyword(keyword)
                .certificationId(certificationId)
                .type(type)
                .status(status)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageDto<ExamResponse> result = examService.search(request);
        return ResponseEntity.ok(ResponseDto.success(result));
    }


    /**
     * Get exam by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ExamDetailResponse>> getById(@PathVariable Long id) {
        log.debug("Get exam by id: {}", id);

        ExamDetailResponse result = examService.getById(id);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get exams by certification ID.
     */
    @GetMapping("/certification/{certificationId}")
    public ResponseEntity<ResponseDto<List<ExamResponse>>> getByCertificationId(
            @PathVariable Long certificationId
    ) {
        log.debug("Get exams by certification id: {}", certificationId);

        List<ExamResponse> result = examService.getByCertificationId(certificationId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Create a new exam.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<ExamResponse>> create(
            @Valid @RequestBody ExamRequest request
    ) {
        log.debug("Create exam with title: {}", request.getTitle());

        ExamResponse result = examService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Exam created successfully", result));
    }

    /**
     * Update an existing exam.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<ExamResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request
    ) {
        log.debug("Update exam with id: {}", id);

        ExamResponse result = examService.update(id, request);
        return ResponseEntity.ok(ResponseDto.success("Exam updated successfully", result));
    }

    /**
     * Soft delete an exam.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        log.debug("Delete exam with id: {}", id);

        examService.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Exam deleted successfully", null));
    }
}
