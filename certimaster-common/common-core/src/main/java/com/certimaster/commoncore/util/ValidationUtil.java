package com.certimaster.commoncore.util;

import com.certimaster.commoncore.constant.AppConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validation utility class
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(AppConstants.REGEX_EMAIL);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(AppConstants.REGEX_USERNAME);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(AppConstants.REGEX_PASSWORD);
    private static final Pattern PHONE_PATTERN = Pattern.compile(AppConstants.REGEX_PHONE);

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return StringUtil.isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate username format
     */
    public static boolean isValidUsername(String username) {
        return StringUtil.isNotEmpty(username) && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        return StringUtil.isNotEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        return StringUtil.isNotEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate positive number
     */
    public static boolean isPositive(Number number) {
        if (number == null) {
            return false;
        }
        return number.doubleValue() > 0;
    }

    /**
     * Validate non-negative number
     */
    public static boolean isNonNegative(Number number) {
        if (number == null) {
            return false;
        }
        return number.doubleValue() >= 0;
    }

    /**
     * Validate URL format
     */
    public static boolean isValidUrl(String url) {
        if (StringUtil.isEmpty(url)) {
            return false;
        }
        try {
            var result = new URI(url).toURL();
            log.info("Validated URL: {}", result);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate UUID format
     */
    public static boolean isValidUUID(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
