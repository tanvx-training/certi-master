package com.certimaster.resultservice.repository;

import com.certimaster.resultservice.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findByUserExamSessionId(Long sessionId);

    Optional<UserAnswer> findByUserExamSessionIdAndQuestionId(Long sessionId, Long questionId);

    long countByUserExamSessionIdAndIsCorrectTrue(Long sessionId);

    long countByUserExamSessionIdAndIsCorrectFalse(Long sessionId);

    long countByUserExamSessionIdAndIsFlaggedTrue(Long sessionId);

    long countByUserExamSessionIdAndSelectedOptionIdsIsNotNull(Long sessionId);
}
