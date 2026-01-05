package com.certimaster.blog_service.controller;

import com.certimaster.blog_service.dto.request.CategoryRequest;
import com.certimaster.blog_service.dto.response.CategoryResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.service.CategoryService;
import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Category API.
 * 
 * Requirements:
 * - 3.1: Create category with slug generation
 * - 3.2: Assign categories to posts
 * - 3.3: Retrieve posts by category with pagination
 * - 3.4: Delete category with cascade to mappings
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Get all categories.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/categories
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<CategoryResponse>>> getAllCategories() {
        log.debug("Get all categories");

        List<CategoryResponse> result = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get category by slug.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/categories/{slug}
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ResponseDto<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        log.debug("Get category by slug: {}", slug);

        CategoryResponse result = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get posts by category slug.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/categories/{slug}/posts
     * @see Requirements 3.3
     */
    @GetMapping("/{slug}/posts")
    public ResponseEntity<ResponseDto<PageDto<PostResponse>>> getPostsByCategory(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.debug("Get posts by category slug: {}", slug);

        PageDto<PostResponse> result = categoryService.getPostsByCategory(slug, page, size);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Create a new category.
     * Requires admin permission.
     * 
     * POST /api/v1/categories
     * @see Requirements 3.1
     */
    @PostMapping
    @PreAuthorize("hasAuthority('BLOG_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {
        log.debug("Create category with name: {}", request.getName());

        CategoryResponse result = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Category created successfully", result));
    }

    /**
     * Update an existing category.
     * Requires admin permission.
     * 
     * PUT /api/v1/categories/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        log.debug("Update category with id: {}", id);

        CategoryResponse result = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ResponseDto.success("Category updated successfully", result));
    }

    /**
     * Delete a category.
     * Requires admin permission.
     * Cascades deletion to all post-category mappings.
     * 
     * DELETE /api/v1/categories/{id}
     * @see Requirements 3.4
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Void>> deleteCategory(@PathVariable Long id) {
        log.debug("Delete category with id: {}", id);

        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ResponseDto.success("Category deleted successfully", null));
    }
}
