package com.certimaster.blog_service.service.impl;

import com.certimaster.blog_service.service.MarkdownService;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of MarkdownService using flexmark-java and OWASP HTML Sanitizer.
 * 
 * Requirements:
 * - 11.2: Render Markdown to HTML using server-side renderer
 * - 11.3: Apply syntax highlighting for code blocks with language specification
 * - 11.4: Handle invalid Markdown gracefully (render as plain text)
 * - 11.5: Sanitize HTML output to prevent XSS attacks
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MarkdownServiceImpl implements MarkdownService {

    private final Parser markdownParser;
    private final HtmlRenderer markdownRenderer;
    private final PolicyFactory htmlSanitizer;

    @Override
    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }

        try {
            // Parse Markdown to AST
            Node document = markdownParser.parse(markdown);
            
            // Render AST to HTML
            String html = markdownRenderer.render(document);
            
            // Sanitize HTML to prevent XSS
            return sanitizeHtml(html);
        } catch (Exception e) {
            // Requirement 11.4: Handle invalid Markdown gracefully
            log.warn("Failed to render Markdown, returning escaped content: {}", e.getMessage());
            return escapeHtml(markdown);
        }
    }

    @Override
    public String sanitizeHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        // Requirement 11.5: Sanitize HTML to prevent XSS attacks
        return htmlSanitizer.sanitize(html);
    }

    /**
     * Escape HTML special characters for safe display.
     * Used as fallback when Markdown parsing fails.
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
