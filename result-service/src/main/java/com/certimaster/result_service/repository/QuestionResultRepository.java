package com.certimaster.result_service.repository;

import com.certimaster.result_service.entity.QuestionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionResultRepository extends JpaRepository<QuestionResult, Long> {

    /**
     * Find question results by exam result ID.
     */
    List<QuestionResult> findByExamResultId(Long resultId);

    /**
     * Get frequently wrong questions for a user.
     */
    @Query("""
            SELECT qr.questionId, COUNT(qr) as wrongCount
            FROM QuestionResult qr
            JOIN qr.examResult r
            WHERE r.userId = :userId AND qr.isCorrect = false
            GROUP BY qr.questionId
            ORDER BY wrongCount DESC
            """)
    List<Object[]> getFrequentlyWrongQuestions(@Param("userId") Long userId);

    /**
     * Get question accuracy for a user.
     */
    @Query("""
            SELECT qr.questionId,
                   SUM(CASE WHEN qr.isCorrect = true THEN 1 ELSE 0 END) as correct,
                   COUNT(qr) as total
            FROM QuestionResult qr
            JOIN qr.examResult r
            WHERE r.userId = :userId
            GROUP BY qr.questionId
            """)
    List<Object[]> getQuestionAccuracyForUser(@Param("userId") Long userId);
}
