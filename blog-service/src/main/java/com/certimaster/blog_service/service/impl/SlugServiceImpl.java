package com.certimaster.blog_service.service.impl;

import com.certimaster.blog_service.repository.PostRepository;
import com.certimaster.blog_service.service.SlugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Implementation of SlugService for generating URL-friendly slugs.
 * 
 * Requirements:
 * - 2.1: Generate slug by converting to lowercase, replacing spaces with hyphens, removing special characters
 * - 2.2: Append numeric suffix for uniqueness when slug already exists
 * - 2.3: Validate uniqueness before saving
 */
@Service
@RequiredArgsConstructor
public class SlugServiceImpl implements SlugService {

    private final PostRepository postRepository;

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");

    @Override
    public String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            return "";
        }

        // Normalize unicode characters (e.g., é -> e)
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Convert to lowercase
        String slug = normalized.toLowerCase();

        // Remove special characters (keep only alphanumeric, spaces, and hyphens)
        slug = NON_ALPHANUMERIC.matcher(slug).replaceAll("");

        // Replace whitespace with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Replace multiple consecutive hyphens with single hyphen
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }

    @Override
    public String generateUniqueSlug(String title) {
        return generateUniqueSlug(title, null);
    }

    @Override
    public String generateUniqueSlug(String title, Long excludePostId) {
        String baseSlug = generateSlug(title);
        
        if (baseSlug.isEmpty()) {
            baseSlug = "post";
        }

        String slug = baseSlug;
        int suffix = 1;

        while (isSlugExists(slug, excludePostId)) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }

    @Override
    public boolean isSlugExists(String slug) {
        return postRepository.existsBySlug(slug);
    }

    @Override
    public boolean isSlugExists(String slug, Long excludePostId) {
        if (excludePostId == null) {
            return postRepository.existsBySlug(slug);
        }
        return postRepository.existsBySlugAndIdNot(slug, excludePostId);
    }
}
