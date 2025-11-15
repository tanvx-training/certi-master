package com.certimaster.commonsecurity.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * General security utility methods
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generate random string for tokens
     */
    public static String generateRandomString(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Generate secure random token
     */
    public static String generateSecureToken() {
        return generateRandomString(32);
    }

    /**
     * Generate API key
     */
    public static String generateApiKey() {
        return generateRandomString(64);
    }

    /**
     * Validate password strength
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch ->
                !Character.isLetterOrDigit(ch));

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    /**
     * Check if password matches
     */
    public static boolean checkPassword(String rawPassword, String encodedPassword,
                                        PasswordEncoder encoder) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
