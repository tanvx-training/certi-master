package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PostTag entity.
 * 
 * @see Requirements 4.2, 4.4
 */
@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    /**
     * Find tag by slug.
     * 
     * @see Requirements 4.2
     */
    Optional<PostTag> findBySlug(String slug);

    /**
     * Find tag by name.
     */
    Optional<PostTag> findByName(String name);

    /**
     * Check if slug exists.
     */
    boolean existsBySlug(String slug);

    /**
     * Check if slug exists excluding a specific tag (for updates).
     */
    boolean existsBySlugAndIdNot(String slug, Long id);

    /**
     * Check if name exists.
     */
    boolean existsByName(String name);

    /**
     * Check if name exists excluding a specific tag (for updates).
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Find all tags with post count.
     * Returns tags with the count of published posts for each.
     * 
     * @see Requirements 4.4
     */
    @Query("""
            SELECT t, COUNT(DISTINCT ptm.post.id) as postCount
            FROM PostTag t
            LEFT JOIN t.postMappings ptm
            LEFT JOIN ptm.post p ON p.status = 'PUBLISHED'
            GROUP BY t
            ORDER BY t.name
            """)
    List<Object[]> findAllWithPostCount();

    /**
     * Count published posts with a specific tag.
     * 
     * @see Requirements 4.4
     */
    @Query("""
            SELECT COUNT(DISTINCT ptm.post.id)
            FROM PostTagMapping ptm
            WHERE ptm.tag.id = :tagId
            AND ptm.post.status = 'PUBLISHED'
            """)
    long countPublishedPostsByTagId(@Param("tagId") Long tagId);

    /**
     * Find tags by names (for bulk lookup/creation).
     */
    List<PostTag> findByNameIn(List<String> names);
}
