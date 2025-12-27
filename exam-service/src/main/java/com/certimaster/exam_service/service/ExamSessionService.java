package com.certimaster.exam_service.service;

import com.certimaster.exam_service.dto.request.AnswerQuestionRequest;
import com.certimaster.exam_service.dto.request.StartExamRequest;
import com.certimaster.exam_service.dto.response.AnswerFeedbackResponse;
import com.certimaster.exam_service.dto.response.ExamSessionResponse;

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
     *
     * @param sessionId session ID
     * @param userId user ID for validation
     */
    void completeSession(Long sessionId, Long userId);
}
