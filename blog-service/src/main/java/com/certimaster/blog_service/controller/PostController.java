package com.certimaster.blog_service.controller;

import com.certimaster.blog_service.dto.request.PostRequest;
import com.certimaster.blog_service.dto.request.PostSearchRequest;
import com.certimaster.blog_service.dto.response.PostDetailResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.blog_service.security.SecurityUtils;
import com.certimaster.blog_service.service.PostService;
import com.certimaster.common_library.dto.PageDto;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.common_library.exception.business.UnauthorizedException;
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

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ResponseDto<PageDto<PostResponse>>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) LocalDateTime publishedFrom,
            @RequestParam(required = false) LocalDateTime publishedTo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.debug("Search posts - keyword: {}, status: {}, authorId: {}", keyword, status, authorId);

        PostSearchRequest request = PostSearchRequest.builder()
                .keyword(keyword)
                .status(status)
                .authorId(authorId)
                .categoryId(categoryId)
                .tagId(tagId)
                .publishedFrom(publishedFrom)
                .publishedTo(publishedTo)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PageDto<PostResponse> result = postService.searchPosts(request);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseDto<PostDetailResponse>> getPostBySlug(@PathVariable String slug) {
        log.debug("Get post by slug: {}", slug);

        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(null);
        PostDetailResponse result = postService.getPostBySlug(slug, currentUserId);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BLOG_CREATE')")
    public ResponseEntity<ResponseDto<PostResponse>> createPost(
            @Valid @RequestBody PostRequest request
    ) {
        log.debug("Create post with title: {}", request.getTitle());

        Long authorId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        PostResponse result = postService.createPost(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Post created successfully", result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_UPDATE') or hasAuthority('BLOG_CREATE')")
    public ResponseEntity<ResponseDto<PostResponse>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request
    ) {
        log.debug("Update post with id: {}", id);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        PostResponse result = postService.updatePost(id, request, userId);
        return ResponseEntity.ok(ResponseDto.success("Post updated successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_DELETE') or hasAuthority('BLOG_CREATE')")
    public ResponseEntity<ResponseDto<Void>> deletePost(@PathVariable Long id) {
        log.debug("Delete post with id: {}", id);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        postService.deletePost(id, userId);
        return ResponseEntity.ok(ResponseDto.success("Post deleted successfully", null));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('BLOG_PUBLISH')")
    public ResponseEntity<ResponseDto<PostResponse>> publishPost(@PathVariable Long id) {
        log.debug("Publish post with id: {}", id);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        PostResponse result = postService.publishPost(id, userId);
        return ResponseEntity.ok(ResponseDto.success("Post published successfully", result));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('BLOG_PUBLISH')")
    public ResponseEntity<ResponseDto<PostResponse>> archivePost(@PathVariable Long id) {
        log.debug("Archive post with id: {}", id);

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        PostResponse result = postService.archivePost(id, userId);
        return ResponseEntity.ok(ResponseDto.success("Post archived successfully", result));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ResponseDto<Void>> incrementViewCount(@PathVariable Long id) {
        log.debug("Increment view count for post with id: {}", id);

        postService.incrementViewCount(id);
        return ResponseEntity.ok(ResponseDto.success("View count incremented", null));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ResponseDto<PageDto<PostResponse>>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.debug("Get posts by author: {}", authorId);

        PageDto<PostResponse> result = postService.getPostsByAuthor(authorId, page, size);
        return ResponseEntity.ok(ResponseDto.success(result));
    }
}
