package com.certimaster.blog_service.service;

/**
 * Service interface for slug generation and validation.
 * 
 * Requirements:
 * - 2.1: Generate slug by converting to lowercase, replacing spaces with hyphens, removing special characters
 * - 2.2: Append numeric suffix for uniqueness when slug already exists
 * - 2.3: Validate uniqueness before saving
 */
public interface SlugService {

    /**
     * Generate a slug from the given title.
     * Converts to lowercase, replaces spaces with hyphens, removes special characters.
     *
     * @param title the title to generate slug from
     * @return the generated slug
     */
    String generateSlug(String title);

    /**
     * Generate a unique slug from the given title.
     * If the generated slug already exists, appends a numeric suffix.
     *
     * @param title the title to generate slug from
     * @return a unique slug
     */
    String generateUniqueSlug(String title);

    /**
     * Generate a unique slug from the given title, excluding a specific post.
     * Used when updating a post to check uniqueness against other posts.
     *
     * @param title the title to generate slug from
     * @param excludePostId the post ID to exclude from uniqueness check
     * @return a unique slug
     */
    String generateUniqueSlug(String title, Long excludePostId);

    /**
     * Check if a slug already exists.
     *
     * @param slug the slug to check
     * @return true if the slug exists, false otherwise
     */
    boolean isSlugExists(String slug);

    /**
     * Check if a slug already exists, excluding a specific post.
     *
     * @param slug the slug to check
     * @param excludePostId the post ID to exclude from the check
     * @return true if the slug exists (excluding the specified post), false otherwise
     */
    boolean isSlugExists(String slug, Long excludePostId);
}
