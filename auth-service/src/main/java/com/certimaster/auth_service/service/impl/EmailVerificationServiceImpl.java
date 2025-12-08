package com.certimaster.auth_service.service.impl;

import com.certimaster.auth_service.entity.EmailVerificationToken;
import com.certimaster.auth_service.entity.User;
import com.certimaster.auth_service.repository.EmailVerificationTokenRepository;
import com.certimaster.auth_service.repository.UserRepository;
import com.certimaster.auth_service.service.EmailVerificationService;
import com.certimaster.common_library.exception.business.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public String createVerificationToken(User user) {
        log.info("Creating verification token for user: {}", user.getEmail());

        // Generate UUID token
        String token = UUID.randomUUID().toString();

        // Create token entity with 24-hour expiration
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);

        log.info("Verification token created successfully for user: {}", user.getEmail());
        return token;
    }

    @Override
    public void sendVerificationEmail(String email, String token) {
        log.info("Sending verification email to: {}", email);

        // TODO: Implement actual email sending using JavaMailSender or email service
        // For now, just log the verification link
        String verificationLink = String.format("http://localhost:8081/api/v1/auth/verify-email?token=%s", token);

        log.info("Verification link for {}: {}", email, verificationLink);
        log.warn("Email sending not implemented yet. Please implement JavaMailSender configuration.");
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);

        // Find token
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Invalid verification token: {}", token);
                    return new BusinessException("INVALID_VERIFICATION_TOKEN",
                            "Invalid or expired verification token");
                });

        // Check if already verified
        if (verificationToken.isVerified()) {
            log.warn("Token already used: {}", token);
            throw new BusinessException("TOKEN_ALREADY_USED",
                    "This verification token has already been used");
        }

        // Check if expired
        if (verificationToken.isExpired()) {
            log.warn("Token expired: {}", token);
            throw new BusinessException("VERIFICATION_TOKEN_EXPIRED",
                    "Verification token has expired");
        }

        // Update user emailVerified status
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Mark token as verified
        verificationToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }
}
