package com.certimaster.exam_service.controller;

import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.common_library.event.ExamResultResponse;
import com.certimaster.exam_service.dto.request.AnswerQuestionRequest;
import com.certimaster.exam_service.dto.request.StartExamRequest;
import com.certimaster.exam_service.dto.response.AnswerFeedbackResponse;
import com.certimaster.exam_service.dto.response.ExamSessionResponse;
import com.certimaster.exam_service.dto.response.UserExamSessionResponse;
import com.certimaster.exam_service.security.SecurityUtils;
import com.certimaster.exam_service.service.ExamSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Exam Session API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/exam-sessions")
@RequiredArgsConstructor
public class ExamSessionController {

    private final ExamSessionService examSessionService;

    /**
     * Start a new exam session.
     */
    @PostMapping("/exam/{examId}/start")
    public ResponseEntity<ResponseDto<ExamSessionResponse>> startExam(@PathVariable Long examId,
                                                                      @Valid @RequestBody StartExamRequest request) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow();
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        log.info("Start exam {} for user {} with mode {}", examId, userId, request.getMode());

        ExamSessionResponse result = examSessionService.startExam(examId, userId, username, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Exam session started successfully", result));
    }

    /**
     * Get session details by ID.
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<ResponseDto<UserExamSessionResponse>> getSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow();
        log.debug("Get session {} for user {}", sessionId, userId);
        UserExamSessionResponse result = examSessionService.getSession(sessionId, userId);
        return ResponseEntity.ok(ResponseDto.success("Session retrieved successfully", result));
    }

    /**
     * Get all active sessions for the current user.
     */
    @GetMapping("/active")
    public ResponseEntity<ResponseDto<List<UserExamSessionResponse>>> getActiveSessions() {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow();
        log.debug("Get active sessions for user {}", userId);
        List<UserExamSessionResponse> result = examSessionService.getActiveSessions(userId);
        return ResponseEntity.ok(ResponseDto.success("Active sessions retrieved successfully", result));
    }

    /**
     * Submit an answer for a question.
     */
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<ResponseDto<AnswerFeedbackResponse>> submitAnswer(
            @PathVariable Long sessionId,
            @Valid @RequestBody AnswerQuestionRequest request
    ) {
        log.debug("Submit answer for session {} question {}", sessionId, request.getQuestionId());
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow();
        AnswerFeedbackResponse result = examSessionService.submitAnswer(sessionId, userId, request);
        return ResponseEntity.ok(ResponseDto.success("Answer submitted successfully", result));
    }

    /**
     * Complete/finish an exam session.
     */
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<ResponseDto<ExamResultResponse>> completeSession(
            @PathVariable Long sessionId
    ) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow();
        log.info("Complete session {} for user {}", sessionId, userId);
        ExamResultResponse result = examSessionService.completeSession(sessionId, userId);
        return ResponseEntity.ok(ResponseDto.success("Exam session completed successfully", result));
    }
}
