package com.certimaster.resultservice.service.impl;

import com.certimaster.resultservice.dto.mapper.UserExamSessionMapper;
import com.certimaster.resultservice.dto.response.UserExamSessionResponse;
import com.certimaster.resultservice.repository.UserExamSessionRepository;
import com.certimaster.resultservice.service.UserExamSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserExamSessionService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserExamSessionServiceImpl implements UserExamSessionService {

    private final UserExamSessionRepository sessionRepository;
    private final UserExamSessionMapper sessionMapper;

    @Override
    public List<UserExamSessionResponse> getSessionsByUserId(Long userId) {
        log.debug("Getting sessions for user {}", userId);
        return sessionMapper.toResponseList(
                sessionRepository.findByUserIdOrderByStartTimeDesc(userId)
        );
    }

    @Override
    public List<UserExamSessionResponse> getSessionsByUserIdAndExamId(Long userId, Long examId) {
        log.debug("Getting sessions for user {} and exam {}", userId, examId);
        return sessionMapper.toResponseList(
                sessionRepository.findByUserIdAndExamIdOrderByStartTimeDesc(userId, examId)
        );
    }

    @Override
    public Optional<UserExamSessionResponse> getActiveSessionByUserIdAndExamId(Long userId, Long examId) {
        log.debug("Getting active session for user {} and exam {}", userId, examId);
        return sessionRepository.findActiveByUserIdAndExamId(userId, examId)
                .map(sessionMapper::toResponse);
    }

    @Override
    public Optional<UserExamSessionResponse> getSessionById(Long sessionId) {
        log.debug("Getting session by id {}", sessionId);
        return sessionRepository.findById(sessionId)
                .map(sessionMapper::toResponse);
    }
}
