package com.certimaster.authservice.service;

import com.certimaster.authservice.entity.User;

public interface EmailVerificationService {

    String createVerificationToken(User user);

    void sendVerificationEmail(String email, String token);

    void verifyEmail(String token);
}
