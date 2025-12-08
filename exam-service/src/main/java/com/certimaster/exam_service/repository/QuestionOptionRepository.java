package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    /**
     * Find options by question id ordered by orderIndex.
     */
    List<QuestionOption> findByQuestionIdOrderByOrderIndexAsc(Long questionId);

    /**
     * Delete all options by question id.
     */
    void deleteByQuestionId(Long questionId);
}
