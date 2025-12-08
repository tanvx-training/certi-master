package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Search questions with filters.
     */
    @Query("""
            SELECT q FROM Question q
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:topicId IS NULL OR q.topic.id = :topicId)
            AND (:type IS NULL OR :type = '' OR q.type = :type)
            AND (:difficulty IS NULL OR :difficulty = '' OR q.difficulty = :difficulty)
            """)
    Page<Question> search(
            @Param("keyword") String keyword,
            @Param("topicId") Long topicId,
            @Param("type") String type,
            @Param("difficulty") String difficulty,
            Pageable pageable
    );

    /**
     * Find questions by topic id.
     */
    List<Question> findByTopicId(Long topicId);

    /**
     * Find questions by certification id (through topic).
     */
    @Query("SELECT q FROM Question q WHERE q.topic.certification.id = :certificationId")
    List<Question> findByCertificationId(@Param("certificationId") Long certificationId);

    /**
     * Count questions by topic id.
     */
    long countByTopicId(Long topicId);

    /**
     * Find random questions by topic id.
     */
    @Query(value = "SELECT * FROM questions WHERE topic_id = :topicId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByTopicId(@Param("topicId") Long topicId, @Param("limit") int limit);
}
