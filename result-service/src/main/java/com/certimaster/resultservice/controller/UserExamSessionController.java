package com.certimaster.resultservice.controller;

import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.resultservice.dto.response.UserExamSessionResponse;
import com.certimaster.resultservice.service.UserExamSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for User Exam Session API.
 * Provides internal API for exam-service to check user sessions.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user-exam-sessions")
@RequiredArgsConstructor
public class UserExamSessionController {

    private final UserExamSessionService sessionService;

    /**
     * Get all sessions for a user.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<UserExamSessionResponse>>> getSessionsByUser(
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.debug("Get sessions for user {}", userId);
        List<UserExamSessionResponse> sessions = sessionService.getSessionsByUserId(userId);
        return ResponseEntity.ok(ResponseDto.success(sessions));
    }

    /**
     * Get sessions for a user and specific exam.
     */
    @GetMapping("/exam/{examId}")
    public ResponseEntity<ResponseDto<List<UserExamSessionResponse>>> getSessionsByUserAndExam(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long examId
    ) {
        log.debug("Get sessions for user {} and exam {}", userId, examId);
        List<UserExamSessionResponse> sessions = sessionService.getSessionsByUserIdAndExamId(userId, examId);
        return ResponseEntity.ok(ResponseDto.success(sessions));
    }

    /**
     * Get active session for a user and exam.
     * This is the main API used by exam-service to check if user has an active session.
     */
    @GetMapping("/exam/{examId}/active")
    public ResponseEntity<ResponseDto<UserExamSessionResponse>> getActiveSession(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long examId
    ) {
        log.debug("Get active session for user {} and exam {}", userId, examId);
        return sessionService.getActiveSessionByUserIdAndExamId(userId, examId)
                .map(session -> ResponseEntity.ok(ResponseDto.success(session)))
                .orElse(ResponseEntity.ok(ResponseDto.success("No active session found", null)));
    }

    /**
     * Get session by ID.
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<ResponseDto<UserExamSessionResponse>> getSessionById(
            @PathVariable Long sessionId
    ) {
        log.debug("Get session by id {}", sessionId);
        return sessionService.getSessionById(sessionId)
                .map(session -> ResponseEntity.ok(ResponseDto.success(session)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Internal API: Check if user has active session for exam.
     * Used by exam-service via Feign client for:
     * 1. Checking existing active session before starting new exam
     * 2. Polling for newly created session after Kafka event
     */
    @GetMapping("/internal/check-active")
    public ResponseEntity<ResponseDto<UserExamSessionResponse>> checkActiveSession(
            @RequestParam Long userId,
            @RequestParam Long examId
    ) {
        log.debug("Internal check active session for user {} and exam {}", userId, examId);
        return sessionService.getActiveSessionByUserIdAndExamId(userId, examId)
                .map(session -> ResponseEntity.ok(ResponseDto.success(session)))
                .orElse(ResponseEntity.ok(ResponseDto.success("No active session", null)));
    }
}
