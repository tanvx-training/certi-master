package com.certimaster.exam_service.service;

import com.certimaster.common_library.event.ExamResultResponse;
import com.certimaster.exam_service.dto.request.AnswerQuestionRequest;
import com.certimaster.exam_service.dto.request.StartExamRequest;
import com.certimaster.exam_service.dto.response.AnswerFeedbackResponse;
import com.certimaster.exam_service.dto.response.ExamSessionResponse;
import com.certimaster.exam_service.dto.response.UserExamSessionResponse;

import java.util.List;

/**
 * Service interface for Exam Session operations.
 */
public interface ExamSessionService {

    /**
     * Start a new exam session.
     *
     * @param examId exam ID
     * @param userId user ID from auth
     * @param username username from auth
     * @param request start exam request with mode
     * @return exam session with questions
     */
    ExamSessionResponse startExam(Long examId, Long userId, String username, StartExamRequest request);

    /**
     * Submit an answer for a question.
     *
     * @param sessionId session ID
     * @param userId user ID for validation
     * @param request answer request
     * @return feedback response
     */
    AnswerFeedbackResponse submitAnswer(Long sessionId, Long userId, AnswerQuestionRequest request);

    /**
     * Complete/finish an exam session.
     * Publishes ExamCompletedEvent to Kafka and waits for result calculation.
     *
     * @param sessionId session ID
     * @param userId user ID for validation
     * @return exam result response with scores and detailed feedback
     */
    ExamResultResponse completeSession(Long sessionId, Long userId);

    /**
     * Get session details by ID.
     * Validates user ownership before returning session data.
     *
     * @param sessionId session ID
     * @param userId user ID for validation
     * @return session details with progress statistics
     */
    UserExamSessionResponse getSession(Long sessionId, Long userId);

    /**
     * Get all active (IN_PROGRESS) sessions for a user.
     *
     * @param userId user ID
     * @return list of active sessions
     */
    List<UserExamSessionResponse> getActiveSessions(Long userId);
}
