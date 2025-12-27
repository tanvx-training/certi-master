package com.certimaster.common_library.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Common status enum
 */
@Getter
@RequiredArgsConstructor
public enum Status {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    DELETED("DELETED"),
    ARCHIVED("ARCHIVED");

    private final String description;
}
