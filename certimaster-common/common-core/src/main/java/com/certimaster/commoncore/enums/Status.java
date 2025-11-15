package com.certimaster.commoncore.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Common status enum
 */
@Getter
@RequiredArgsConstructor
public enum Status {

    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    DELETED("Deleted"),
    ARCHIVED("Archived");

    private final String description;
}
