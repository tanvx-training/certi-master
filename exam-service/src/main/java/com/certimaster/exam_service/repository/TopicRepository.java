package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    /**
     * Search topics with filters.
     */
    @Query("""
            SELECT t FROM Topic t
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:certificationId IS NULL OR t.certification.id = :certificationId)
            """)
    Page<Topic> search(
            @Param("keyword") String keyword,
            @Param("certificationId") Long certificationId,
            Pageable pageable
    );

    /**
     * Find topics by certification id ordered by orderIndex.
     */
    List<Topic> findByCertificationIdOrderByOrderIndexAsc(Long certificationId);

    /**
     * Find topic by code and certification.
     */
    Optional<Topic> findByCodeIgnoreCaseAndCertificationId(String code, Long certificationId);

    /**
     * Check if topic exists by code and certification (excluding a specific id for update).
     */
    boolean existsByCodeIgnoreCaseAndCertificationIdAndIdNot(String code, Long certificationId, Long id);

    /**
     * Check if topic exists by code and certification.
     */
    boolean existsByCodeIgnoreCaseAndCertificationId(String code, Long certificationId);

    /**
     * Check if topic exists by name and certification (excluding a specific id for update).
     */
    boolean existsByNameIgnoreCaseAndCertificationIdAndIdNot(String name, Long certificationId, Long id);

    /**
     * Check if topic exists by name and certification.
     */
    boolean existsByNameIgnoreCaseAndCertificationId(String name, Long certificationId);
}
