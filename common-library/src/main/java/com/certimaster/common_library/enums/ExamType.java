package com.certimaster.common_library.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ExamType {
    PRACTICE(1),
    MOCK(2),
    FINAL(3),
    DIAGNOSTIC(4),
    TOPIC_WISE(5);

    private final int value;

    public static Optional<ExamType> fromValue(int value) {
        return Arrays.stream(ExamType.values()).filter(v -> v.getValue() == value).findFirst();
    }
}
