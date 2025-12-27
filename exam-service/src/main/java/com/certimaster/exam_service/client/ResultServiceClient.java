package com.certimaster.exam_service.client;

import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.external.UserExamSessionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for calling result-service APIs.
 */
@FeignClient(
        name = "result-service",
        url = "${services.result-service.url:http://localhost:8084}",
        fallback = ResultServiceClientFallback.class
)
public interface ResultServiceClient {

    /**
     * Check if user has an active session for the given exam.
     * Also used to poll for newly created session after Kafka event.
     */
    @GetMapping("/api/v1/user-exam-sessions/internal/check-active")
    ResponseDto<UserExamSessionDto> checkActiveSession(
            @RequestParam("userId") Long userId,
            @RequestParam("examId") Long examId
    );

    /**
     * Get session by ID.
     */
    @GetMapping("/api/v1/user-exam-sessions/{sessionId}")
    ResponseDto<UserExamSessionDto> getSessionById(@PathVariable("sessionId") Long sessionId);
}
