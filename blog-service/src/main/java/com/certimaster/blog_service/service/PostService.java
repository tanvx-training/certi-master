package com.certimaster.blog_service.service;

import com.certimaster.blog_service.dto.request.PostRequest;
import com.certimaster.blog_service.dto.request.PostSearchRequest;
import com.certimaster.blog_service.dto.response.PostDetailResponse;
import com.certimaster.blog_service.dto.response.PostResponse;
import com.certimaster.common_library.dto.PageDto;

public interface PostService {

    PostResponse createPost(PostRequest request, Long authorId);

    PostResponse updatePost(Long id, PostRequest request, Long userId);

    PostDetailResponse getPostBySlug(String slug, Long currentUserId);

    PostDetailResponse getPostById(Long id, Long currentUserId);

    void deletePost(Long id, Long userId);

    PostResponse publishPost(Long id, Long userId);

    PostResponse archivePost(Long id, Long userId);

    void incrementViewCount(Long id);

    PageDto<PostResponse> searchPosts(PostSearchRequest request);

    PageDto<PostResponse> getPostsByAuthor(Long authorId, int page, int size);
}
