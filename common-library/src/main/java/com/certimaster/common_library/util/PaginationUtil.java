package com.certimaster.common_library.util;

import com.certimaster.common_library.constant.AppConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Helper class for pagination
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationUtil {

    /**
     * Create Pageable with default sorting
     */
    public static Pageable createPageable(int page, int size) {
        return createPageable(page, size,
                AppConstants.DEFAULT_SORT_BY,
                AppConstants.DEFAULT_SORT_DIRECTION);
    }

    /**
     * Create Pageable with custom sorting
     */
    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        // Validate page and size
        int validPage = Math.max(0, page);
        int validSize = Math.min(Math.max(1, size), AppConstants.MAX_PAGE_SIZE);

        // Create sort
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sort = Sort.by(sortDirection, sortBy);

        return PageRequest.of(validPage, validSize, sort);
    }

    /**
     * Create Pageable with multiple sort fields
     */
    public static Pageable createPageable(int page, int size, Sort sort) {
        int validPage = Math.max(0, page);
        int validSize = Math.min(Math.max(1, size), AppConstants.MAX_PAGE_SIZE);

        return PageRequest.of(validPage, validSize, sort);
    }
}
