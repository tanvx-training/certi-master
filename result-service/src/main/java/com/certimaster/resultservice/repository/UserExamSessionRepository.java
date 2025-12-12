package com.certimaster.resultservice.repository;

import com.certimaster.resultservice.entity.UserExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserExamSessionRepository extends JpaRepository<UserExamSession, Long> {

    List<UserExamSession> findByUserIdOrderByStartTimeDesc(Long userId);

    List<UserExamSession> findByUserIdAndExamIdOrderByStartTimeDesc(Long userId, Long examId);

    List<UserExamSession> findByUserIdAndCertificationIdOrderByStartTimeDesc(Long userId, Long certificationId);
}
