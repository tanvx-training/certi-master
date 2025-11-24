package com.certimaster.commonlibrary.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Number utility class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtil {

    /**
     * Format number with default pattern
     */
    public static String format(Number number) {
        return format(number, "#,##0.00");
    }

    /**
     * Format number with custom pattern
     */
    public static String format(Number number, String pattern) {
        if (number == null) {
            return null;
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(number);
    }

    /**
     * Round to decimal places
     */
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("Decimal places must be non-negative");
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Convert to percentage
     */
    public static double toPercentage(double value, double total) {
        if (total == 0) {
            return 0;
        }
        return (value / total) * 100;
    }

    /**
     * Calculate percentage of value
     */
    public static double percentageOf(double percentage, double value) {
        return (percentage / 100) * value;
    }

    /**
     * Check if number is between min and max (inclusive)
     */
    public static boolean isBetween(Number number, Number min, Number max) {
        if (number == null) {
            return false;
        }
        double value = number.doubleValue();
        double minValue = min != null ? min.doubleValue() : Double.MIN_VALUE;
        double maxValue = max != null ? max.doubleValue() : Double.MAX_VALUE;
        return value >= minValue && value <= maxValue;
    }

    /**
     * Get min value
     */
    public static <T extends Comparable<T>> T min(T a, T b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) < 0 ? a : b;
    }

    /**
     * Get max value
     */
    public static <T extends Comparable<T>> T max(T a, T b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) > 0 ? a : b;
    }

    /**
     * Clamp value between min and max
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
