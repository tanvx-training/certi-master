package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
}
