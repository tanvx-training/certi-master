package com.certimaster.blog_service.controller;

import com.certimaster.blog_service.dto.request.TagRequest;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.dto.response.TagResponse;
import com.certimaster.blog_service.service.TagService;
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
 * REST Controller for Tag API.
 * 
 * Requirements:
 * - 4.1: Create tags with auto-creation of new tags when adding to posts
 * - 4.2: Retrieve posts by tag with pagination
 * - 4.3: Delete tag with cascade to mappings
 * - 4.4: List tags with post counts
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * Get all tags with post counts.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/tags
     * @see Requirements 4.4
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<TagResponse>>> getAllTags() {
        log.debug("Get all tags with post counts");

        List<TagResponse> result = tagService.getTagsWithCounts();
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get tag by slug.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/tags/{slug}
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ResponseDto<TagResponse>> getTagBySlug(@PathVariable String slug) {
        log.debug("Get tag by slug: {}", slug);

        TagResponse result = tagService.getTagBySlug(slug);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Get posts by tag slug.
     * Public endpoint - no authentication required.
     * 
     * GET /api/v1/tags/{slug}/posts
     * @see Requirements 4.2
     */
    @GetMapping("/{slug}/posts")
    public ResponseEntity<ResponseDto<PageDto<PostResponse>>> getPostsByTag(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.debug("Get posts by tag slug: {}", slug);

        PageDto<PostResponse> result = tagService.getPostsByTag(slug, page, size);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    /**
     * Create a new tag.
     * Requires authentication.
     * 
     * POST /api/v1/tags
     * @see Requirements 4.1
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<TagResponse>> createTag(
            @Valid @RequestBody TagRequest request
    ) {
        log.debug("Create tag with name: {}", request.getName());

        TagResponse result = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Tag created successfully", result));
    }

    /**
     * Update an existing tag.
     * Requires admin permission.
     * 
     * PUT /api/v1/tags/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<TagResponse>> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request
    ) {
        log.debug("Update tag with id: {}", id);

        TagResponse result = tagService.updateTag(id, request);
        return ResponseEntity.ok(ResponseDto.success("Tag updated successfully", result));
    }

    /**
     * Delete a tag.
     * Requires admin permission.
     * Cascades deletion to all post-tag mappings.
     * 
     * DELETE /api/v1/tags/{id}
     * @see Requirements 4.3
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Void>> deleteTag(@PathVariable Long id) {
        log.debug("Delete tag with id: {}", id);

        tagService.deleteTag(id);
        return ResponseEntity.ok(ResponseDto.success("Tag deleted successfully", null));
    }
}
