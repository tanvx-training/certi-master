package com.certimaster.blog_service.service;

import com.certimaster.blog_service.dto.request.TagRequest;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.dto.response.TagResponse;
import com.certimaster.common_library.dto.PageDto;

import java.util.List;

/**
 * Service interface for managing blog tags.
 * 
 * Requirements:
 * - 4.1: Create tags with auto-creation of new tags when adding to posts
 * - 4.2: Retrieve posts by tag with pagination
 * - 4.3: Delete tag with cascade to mappings
 * - 4.4: List tags with post counts
 */
public interface TagService {

    /**
     * Create a new tag with auto-generated slug.
     *
     * @param request the tag creation request
     * @return the created tag response
     * @see Requirements 4.1
     */
    TagResponse createTag(TagRequest request);

    /**
     * Update an existing tag.
     *
     * @param id the tag ID
     * @param request the tag update request
     * @return the updated tag response
     */
    TagResponse updateTag(Long id, TagRequest request);

    /**
     * Get a tag by its ID.
     *
     * @param id the tag ID
     * @return the tag response
     */
    TagResponse getTagById(Long id);

    /**
     * Get a tag by its slug.
     *
     * @param slug the tag slug
     * @return the tag response
     */
    TagResponse getTagBySlug(String slug);

    /**
     * Get all tags with post counts.
     *
     * @return list of all tags with post counts
     * @see Requirements 4.4
     */
    List<TagResponse> getAllTags();

    /**
     * Get all tags with post counts (alias for getAllTags).
     *
     * @return list of all tags with post counts
     * @see Requirements 4.4
     */
    List<TagResponse> getTagsWithCounts();

    /**
     * Delete a tag.
     * Cascades deletion to all post-tag mappings.
     *
     * @param id the tag ID
     * @see Requirements 4.3
     */
    void deleteTag(Long id);

    /**
     * Get published posts by tag slug with pagination.
     *
     * @param tagSlug the tag slug
     * @param page the page number
     * @param size the page size
     * @return paginated list of posts with the tag
     * @see Requirements 4.2
     */
    PageDto<PostResponse> getPostsByTag(String tagSlug, int page, int size);

    /**
     * Assign tags to a post.
     * Creates new tags if they don't exist.
     *
     * @param postId the post ID
     * @param tagNames the list of tag names to assign
     * @see Requirements 4.1
     */
    void assignTagsToPost(Long postId, List<String> tagNames);

    /**
     * Remove all tag assignments from a post.
     *
     * @param postId the post ID
     */
    void removeAllTagsFromPost(Long postId);

    /**
     * Get or create a tag by name.
     * If the tag doesn't exist, creates it with auto-generated slug.
     *
     * @param name the tag name
     * @return the tag response
     * @see Requirements 4.1
     */
    TagResponse getOrCreateTag(String name);
}
