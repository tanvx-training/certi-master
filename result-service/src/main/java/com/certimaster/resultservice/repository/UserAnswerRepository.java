package com.certimaster.resultservice.repository;

import com.certimaster.resultservice.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findBySessionId(Long sessionId);

    Optional<UserAnswer> findBySessionIdAndQuestionId(Long sessionId, Long questionId);

    long countBySessionIdAndIsCorrectTrue(Long sessionId);

    long countBySessionIdAndIsCorrectFalse(Long sessionId);

    long countBySessionIdAndIsFlaggedTrue(Long sessionId);
}
