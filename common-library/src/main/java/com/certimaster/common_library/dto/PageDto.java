package com.certimaster.common_library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Generic pagination wrapper for list responses
 * @param <T> Type of content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDto<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private boolean empty;

    /**
     * Create PageDto from Spring Data Page
     */
    public static <T> PageDto<T> of(Page<T> page) {
        return PageDto.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * Create empty page
     */
    public static <T> PageDto<T> empty() {
        return PageDto.<T>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .first(true)
                .empty(true)
                .build();
    }
}
