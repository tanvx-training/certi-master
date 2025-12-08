package com.certimaster.auth_service.service;

import com.certimaster.auth_service.entity.User;

public interface EmailVerificationService {

    String createVerificationToken(User user);

    void sendVerificationEmail(String email, String token);

    void verifyEmail(String token);
}
