package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.Certification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    /**
     * Find certification by code (case-insensitive).
     */
    Optional<Certification> findByCodeIgnoreCase(String code);

    /**
     * Check if certification exists by code (excluding a specific id for update).
     */
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    /**
     * Check if certification exists by code.
     */
    boolean existsByCodeIgnoreCase(String code);

    /**
     * Search certifications with filters.
     */
    @Query("""
        SELECT c FROM Certification c
        WHERE (:keyword IS NULL OR :keyword = ''
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.provider) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:provider IS NULL OR :provider = '' OR LOWER(c.provider) = LOWER(:provider))
        AND (:level IS NULL OR :level = '' OR c.level = :level)
        AND (:status IS NULL OR :status = '' OR c.status = :status)
        """)
    Page<Certification> search(
            @Param("keyword") String keyword,
            @Param("provider") String provider,
            @Param("level") String level,
            @Param("status") String status,
            Pageable pageable
    );

    /**
     * Find active certification by id.
     */
    @Query("SELECT c FROM Certification c WHERE c.id = :id AND c.status != 'DELETED'")
    Optional<Certification> findActiveById(@Param("id") Long id);
}
