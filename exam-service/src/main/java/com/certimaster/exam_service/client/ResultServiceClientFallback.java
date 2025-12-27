package com.certimaster.exam_service.client;

import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.exam_service.dto.external.UserExamSessionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for ResultServiceClient.
 * Used when result-service is unavailable.
 */
@Slf4j
@Component
public class ResultServiceClientFallback implements ResultServiceClient {

    @Override
    public ResponseDto<UserExamSessionDto> checkActiveSession(Long userId, Long examId) {
        log.warn("Fallback: result-service unavailable for checkActiveSession userId={}, examId={}", userId, examId);
        return ResponseDto.success("Fallback: No active session", null);
    }

    @Override
    public ResponseDto<UserExamSessionDto> getSessionById(Long sessionId) {
        log.warn("Fallback: result-service unavailable for getSessionById sessionId={}", sessionId);
        return ResponseDto.error("SERVICE_UNAVAILABLE", "Result service is unavailable");
    }
}
