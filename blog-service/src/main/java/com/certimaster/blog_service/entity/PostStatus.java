package com.certimaster.blog_service.entity;

/**
 * Enum representing the status of a blog post.
 * 
 * @see Requirements 1.3, 1.4
 */
public enum PostStatus {
    /**
     * Post is in draft mode, not visible to public
     */
    DRAFT,
    
    /**
     * Post is published and visible to public
     */
    PUBLISHED,
    
    /**
     * Post is archived and excluded from public listings
     */
    ARCHIVED
}
