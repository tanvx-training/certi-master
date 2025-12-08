package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {
}
