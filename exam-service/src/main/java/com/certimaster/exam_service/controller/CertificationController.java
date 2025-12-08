package com.certimaster.exam_service.controller;

import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.request.CertificationRequest;
import com.certimaster.exam_service.dto.request.CertificationSearchRequest;
import com.certimaster.exam_service.dto.response.CertificationDetailResponse;
import com.certimaster.exam_service.dto.response.CertificationResponse;
import com.certimaster.exam_service.service.CertificationService;
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

/**
 * REST Controller for Certification API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    /**
     * Search certifications with filters and pagination.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<PageDto<CertificationResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.debug("Search certifications - keyword: {}, provider: {}, level: {}, status: {}",
                keyword, provider, level, status);

        CertificationSearchRequest request = CertificationSearchRequest.builder()
                .keyword(keyword)
                .provider(provider)
                .level(level)
                .status(status)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageDto<CertificationResponse> result = certificationService.search(request);
        return ResponseEntity.ok(ResponseDto.success(result));
    }


    /**
     * Get certification by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CertificationDetailResponse>> getById(@PathVariable Long id) {
        log.debug("Get certification by id: {}", id);

        CertificationDetailResponse result = certificationService.getById(id);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Create a new certification.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<CertificationResponse>> create(
            @Valid @RequestBody CertificationRequest request
    ) {
        log.debug("Create certification with code: {}", request.getCode());

        CertificationResponse result = certificationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Certification created successfully", result));
    }

    /**
     * Update an existing certification.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<CertificationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CertificationRequest request
    ) {
        log.debug("Update certification with id: {}", id);

        CertificationResponse result = certificationService.update(id, request);
        return ResponseEntity.ok(ResponseDto.success("Certification updated successfully", result));
    }

    /**
     * Soft delete a certification.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        log.debug("Delete certification with id: {}", id);

        certificationService.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Certification deleted successfully", null));
    }
}
