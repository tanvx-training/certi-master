package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserAnswer entity.
 * Provides methods for answer management and statistics calculation.
 */
@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    /**
     * Find an answer by session ID and question ID.
     * Used to check if a question has already been answered.
     *
     * @param sessionId the session ID
     * @param questionId the question ID
     * @return the answer if found
     */
    Optional<UserAnswer> findByUserExamSessionIdAndQuestion_Id(Long sessionId, Long questionId);

    /**
     * Find all answers for a session.
     * Used to retrieve all answers when completing a session.
     *
     * @param sessionId the session ID
     * @return list of all answers in the session
     */
    List<UserAnswer> findByUserExamSessionId(Long sessionId);

    /**
     * Count answered questions in a session (where selectedOptionIds is not null).
     *
     * @param sessionId the session ID
     * @return count of answered questions
     */
    long countByUserExamSessionIdAndSelectedOptionIdsIsNotNull(Long sessionId);

    /**
     * Count correct answers in a session.
     *
     * @param sessionId the session ID
     * @return count of correct answers
     */
    long countByUserExamSessionIdAndIsCorrectTrue(Long sessionId);

    /**
     * Count wrong answers in a session.
     *
     * @param sessionId the session ID
     * @return count of wrong answers
     */
    long countByUserExamSessionIdAndIsCorrectFalse(Long sessionId);

    /**
     * Count flagged questions in a session.
     *
     * @param sessionId the session ID
     * @return count of flagged questions
     */
    long countByUserExamSessionIdAndIsFlaggedTrue(Long sessionId);
}
