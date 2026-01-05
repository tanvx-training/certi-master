package com.certimaster.blog_service.repository;

import com.certimaster.blog_service.entity.Post;
import com.certimaster.blog_service.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    Page<Post> findByAuthorIdAndStatus(Long authorId, PostStatus status, Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            JOIN p.categoryMappings cm
            WHERE cm.category.id = :categoryId
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Post> findByCategoryId(
            @Param("categoryId") Long categoryId,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Post p
            JOIN p.categoryMappings cm
            WHERE cm.category.slug = :categorySlug
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Post> findByCategorySlug(
            @Param("categorySlug") String categorySlug,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Post p
            JOIN p.tagMappings tm
            WHERE tm.tag.id = :tagId
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Post> findByTagId(
            @Param("tagId") Long tagId,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Post p
            JOIN p.tagMappings tm
            WHERE tm.tag.slug = :tagSlug
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Post> findByTagSlug(
            @Param("tagSlug") String tagSlug,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Post p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount + 1 WHERE p.id = :id")
    void incrementLikesCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount - 1 WHERE p.id = :id AND p.likesCount > 0")
    void decrementLikesCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + 1 WHERE p.id = :id")
    void incrementCommentsCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount - :count WHERE p.id = :id AND p.commentsCount >= :count")
    void decrementCommentsCount(@Param("id") Long id, @Param("count") int count);
}
