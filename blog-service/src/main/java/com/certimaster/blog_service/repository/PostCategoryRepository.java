package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PostCategory entity.
 * 
 * @see Requirements 3.3
 */
@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    /**
     * Find category by slug.
     * 
     * @see Requirements 3.3
     */
    Optional<PostCategory> findBySlug(String slug);

    /**
     * Find category by name.
     */
    Optional<PostCategory> findByName(String name);

    /**
     * Check if slug exists.
     */
    boolean existsBySlug(String slug);

    /**
     * Check if slug exists excluding a specific category (for updates).
     */
    boolean existsBySlugAndIdNot(String slug, Long id);

    /**
     * Check if name exists.
     */
    boolean existsByName(String name);

    /**
     * Check if name exists excluding a specific category (for updates).
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Find all categories with post count.
     * Returns categories with the count of published posts in each.
     * 
     * @see Requirements 3.3
     */
    @Query("""
            SELECT c, COUNT(DISTINCT pcm.post.id) as postCount
            FROM PostCategory c
            LEFT JOIN c.postMappings pcm
            LEFT JOIN pcm.post p ON p.status = 'PUBLISHED'
            GROUP BY c
            ORDER BY c.name
            """)
    List<Object[]> findAllWithPostCount();

    /**
     * Count published posts in a category.
     */
    @Query("""
            SELECT COUNT(DISTINCT pcm.post.id)
            FROM PostCategoryMapping pcm
            WHERE pcm.category.id = :categoryId
            AND pcm.post.status = 'PUBLISHED'
            """)
    long countPublishedPostsByCategoryId(@Param("categoryId") Long categoryId);
}
