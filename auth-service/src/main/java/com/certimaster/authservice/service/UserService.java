package com.certimaster.authservice.service;

import com.certimaster.authservice.dto.request.LoginRequest;
import com.certimaster.authservice.dto.request.RefreshTokenRequest;
import com.certimaster.authservice.dto.request.RegisterRequest;
import com.certimaster.authservice.dto.response.LoginResponse;
import com.certimaster.authservice.dto.response.RegisterResponse;
import com.certimaster.authservice.dto.response.UserResponse;
import com.certimaster.commonlibrary.dto.ResponseDto;

public interface UserService {

    ResponseDto<RegisterResponse> register(RegisterRequest registerRequest);

    ResponseDto<LoginResponse> login(LoginRequest loginRequest);

    ResponseDto<LoginResponse> refreshToken(RefreshTokenRequest request);

    ResponseDto<Void> logout(String authorization);

    ResponseDto<Void> verifyEmail(String token);

    ResponseDto<UserResponse> getCurrent(String authorization);
}
