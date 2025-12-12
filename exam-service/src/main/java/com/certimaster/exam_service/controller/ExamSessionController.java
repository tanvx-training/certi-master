package com.certimaster.exam_service.controller;

import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.request.AnswerQuestionRequest;
import com.certimaster.exam_service.dto.request.StartExamRequest;
import com.certimaster.exam_service.dto.response.AnswerFeedbackResponse;
import com.certimaster.exam_service.dto.response.ExamSessionResponse;
import com.certimaster.exam_service.service.ExamSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseDto<ExamSessionResponse>> startExam(
            @PathVariable Long examId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Username") String username,
            @Valid @RequestBody StartExamRequest request
    ) {
        log.info("Start exam {} for user {} with mode {}", examId, userId, request.getMode());

        ExamSessionResponse result = examSessionService.startExam(examId, userId, username, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Exam session started successfully", result));
    }

    /**
     * Get current session details.
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<ResponseDto<ExamSessionResponse>> getSession(
            @PathVariable Long sessionId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.debug("Get session {} for user {}", sessionId, userId);

        ExamSessionResponse result = examSessionService.getSession(sessionId, userId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Submit an answer for a question.
     */
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<ResponseDto<AnswerFeedbackResponse>> submitAnswer(
            @PathVariable Long sessionId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AnswerQuestionRequest request
    ) {
        log.debug("Submit answer for session {} question {}", sessionId, request.getQuestionId());

        AnswerFeedbackResponse result = examSessionService.submitAnswer(sessionId, userId, request);
        return ResponseEntity.ok(ResponseDto.success("Answer submitted successfully", result));
    }

    /**
     * Complete/finish an exam session.
     */
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<ResponseDto<Void>> completeSession(
            @PathVariable Long sessionId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("Complete session {} for user {}", sessionId, userId);

        examSessionService.completeSession(sessionId, userId);
        return ResponseEntity.ok(ResponseDto.success("Exam session completed successfully", null));
    }
}
