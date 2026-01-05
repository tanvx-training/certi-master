package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.PostTagMapping;
import com.certimaster.blog_service.entity.PostTagMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PostTagMapping entity.
 * 
 * @see Requirements 4.1, 4.3
 */
@Repository
public interface PostTagMappingRepository extends JpaRepository<PostTagMapping, PostTagMappingId> {

    /**
     * Find all mappings for a post.
     */
    List<PostTagMapping> findByPostId(Long postId);

    /**
     * Find all mappings for a tag.
     */
    List<PostTagMapping> findByTagId(Long tagId);

    /**
     * Delete all mappings for a post.
     */
    @Modifying
    @Query("DELETE FROM PostTagMapping ptm WHERE ptm.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    /**
     * Delete all mappings for a tag.
     * 
     * @see Requirements 4.3
     */
    @Modifying
    @Query("DELETE FROM PostTagMapping ptm WHERE ptm.tag.id = :tagId")
    void deleteByTagId(@Param("tagId") Long tagId);

    /**
     * Check if mapping exists.
     */
    boolean existsByPostIdAndTagId(Long postId, Long tagId);
}
