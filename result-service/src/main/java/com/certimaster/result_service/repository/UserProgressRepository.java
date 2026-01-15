package com.certimaster.result_service.repository;

import com.certimaster.result_service.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    /**
     * Find progress by user and certification.
     */
    Optional<UserProgress> findByUserIdAndCertificationId(Long userId, Long certificationId);

    /**
     * Get certifications with progress for a user.
     */
    @Query("""
            SELECT up FROM UserProgress up
            WHERE up.userId = :userId
            ORDER BY up.totalExamsTaken DESC
            """)
    List<UserProgress> getCertificationsWithProgressForUser(@Param("userId") Long userId);

    /**
     * Get users with best scores for a certification (leaderboard).
     */
    @Query("""
            SELECT up FROM UserProgress up
            WHERE up.certificationId = :certificationId
            AND up.bestScore IS NOT NULL
            ORDER BY up.bestScore DESC
            """)
    List<UserProgress> getLeaderboardByCertification(@Param("certificationId") Long certificationId);
}
