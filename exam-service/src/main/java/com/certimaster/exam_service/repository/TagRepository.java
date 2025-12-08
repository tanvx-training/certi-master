package com.certimaster.exam_service.repository;

import com.certimaster.exam_service.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Search tags with keyword filter (excluding deleted).
     */
    @Query("""
            SELECT t FROM Tag t
            WHERE (t.status IS NULL OR t.status != 'DELETED')
            AND (:keyword IS NULL OR :keyword = ''
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:status IS NULL OR :status = '' OR t.status = :status)
            """)
    Page<Tag> search(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    /**
     * Find active tag by id.
     */
    @Query("SELECT t FROM Tag t WHERE t.id = :id AND (t.status IS NULL OR t.status != 'DELETED')")
    Optional<Tag> findActiveById(@Param("id") Long id);

    /**
     * Find tag by name (case-insensitive).
     */
    Optional<Tag> findByNameIgnoreCase(String name);

    /**
     * Check if tag exists by name (excluding a specific id for update).
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Check if tag exists by name.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find tags by ids (excluding deleted).
     */
    @Query("SELECT t FROM Tag t WHERE t.id IN :ids AND (t.status IS NULL OR t.status != 'DELETED')")
    List<Tag> findByIdIn(@Param("ids") List<Long> ids);

    /**
     * Find all active tags.
     */
    @Query("SELECT t FROM Tag t WHERE t.status IS NULL OR t.status != 'DELETED'")
    List<Tag> findAllActive();
}
