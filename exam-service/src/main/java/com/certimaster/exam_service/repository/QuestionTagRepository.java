package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.QuestionTag;
import com.certimaster.exam_service.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {

    /**
     * Find question tags by question id.
     */
    List<QuestionTag> findByQuestionId(Long questionId);

    /**
     * Find tags by question id.
     */
    @Query("SELECT qt.tag FROM QuestionTag qt WHERE qt.question.id = :questionId")
    List<Tag> findTagsByQuestionId(@Param("questionId") Long questionId);

    /**
     * Delete all question tags by question id.
     */
    void deleteByQuestionId(Long questionId);

    /**
     * Check if question has tag.
     */
    boolean existsByQuestionIdAndTagId(Long questionId, Long tagId);
}
