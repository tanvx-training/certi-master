package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.PostCategoryMapping;
import com.certimaster.blog_service.entity.PostCategoryMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PostCategoryMapping entity.
 * 
 * @see Requirements 3.2, 3.4
 */
@Repository
public interface PostCategoryMappingRepository extends JpaRepository<PostCategoryMapping, PostCategoryMappingId> {

    /**
     * Find all mappings for a post.
     */
    List<PostCategoryMapping> findByPostId(Long postId);

    /**
     * Find all mappings for a category.
     */
    List<PostCategoryMapping> findByCategoryId(Long categoryId);

    /**
     * Delete all mappings for a post.
     */
    @Modifying
    @Query("DELETE FROM PostCategoryMapping pcm WHERE pcm.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    /**
     * Delete all mappings for a category.
     * 
     * @see Requirements 3.4
     */
    @Modifying
    @Query("DELETE FROM PostCategoryMapping pcm WHERE pcm.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Check if mapping exists.
     */
    boolean existsByPostIdAndCategoryId(Long postId, Long categoryId);
}
