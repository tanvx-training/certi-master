package com.certimaster.blog_service.service;

/**
 * Service interface for Markdown processing.
 * 
 * Requirements:
 * - 11.2: Render Markdown to HTML using server-side renderer
 * - 11.3: Apply syntax highlighting for code blocks with language specification
 * - 11.4: Handle invalid Markdown gracefully (render as plain text)
 * - 11.5: Sanitize HTML output to prevent XSS attacks
 */
public interface MarkdownService {

    /**
     * Render Markdown content to HTML.
     * Applies syntax highlighting for code blocks and sanitizes output.
     *
     * @param markdown the Markdown content to render
     * @return the rendered and sanitized HTML
     */
    String renderToHtml(String markdown);

    /**
     * Sanitize HTML content to prevent XSS attacks.
     * Removes potentially dangerous elements and attributes.
     *
     * @param html the HTML content to sanitize
     * @return the sanitized HTML
     */
    String sanitizeHtml(String html);
}
