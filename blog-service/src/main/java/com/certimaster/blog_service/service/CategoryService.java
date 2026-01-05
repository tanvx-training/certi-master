package com.certimaster.blog_service.service;

import com.certimaster.blog_service.dto.request.CategoryRequest;
import com.certimaster.blog_service.dto.response.CategoryResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.common_library.dto.PageDto;

import java.util.List;

/**
 * Service interface for managing blog categories.
 * 
 * Requirements:
 * - 3.1: Create category with slug generation
 * - 3.2: Assign categories to posts
 * - 3.3: Retrieve posts by category with pagination
 * - 3.4: Delete category with cascade to mappings
 */
public interface CategoryService {

    /**
     * Create a new category with auto-generated slug.
     *
     * @param request the category creation request
     * @return the created category response
     * @see Requirements 3.1
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Update an existing category.
     *
     * @param id the category ID
     * @param request the category update request
     * @return the updated category response
     */
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    /**
     * Get a category by its ID.
     *
     * @param id the category ID
     * @return the category response
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * Get a category by its slug.
     *
     * @param slug the category slug
     * @return the category response
     */
    CategoryResponse getCategoryBySlug(String slug);

    /**
     * Get all categories with post counts.
     *
     * @return list of all categories with post counts
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Delete a category.
     * Cascades deletion to all post-category mappings.
     *
     * @param id the category ID
     * @see Requirements 3.4
     */
    void deleteCategory(Long id);

    /**
     * Get published posts by category slug with pagination.
     *
     * @param categorySlug the category slug
     * @param page the page number
     * @param size the page size
     * @return paginated list of posts in the category
     * @see Requirements 3.3
     */
    PageDto<PostResponse> getPostsByCategory(String categorySlug, int page, int size);

    /**
     * Assign categories to a post.
     *
     * @param postId the post ID
     * @param categoryIds the list of category IDs to assign
     * @see Requirements 3.2
     */
    void assignCategoriesToPost(Long postId, List<Long> categoryIds);

    /**
     * Remove all category assignments from a post.
     *
     * @param postId the post ID
     */
    void removeAllCategoriesFromPost(Long postId);
}
