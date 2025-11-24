package com.certimaster.commonlibrary.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * UUID utility class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UUIDUtil {

    /**
     * Generate random UUID
     */
    public static String generate() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate random UUID without hyphens
     */
    public static String generateWithoutHyphens() {
        return generate().replace("-", "");
    }

    /**
     * Validate UUID format
     */
    public static boolean isValid(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return false;
        }

        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
