package com.certimaster.auth_service.service;

import com.certimaster.auth_service.dto.request.LoginRequest;
import com.certimaster.auth_service.dto.request.RefreshTokenRequest;
import com.certimaster.auth_service.dto.request.RegisterRequest;
import com.certimaster.auth_service.dto.response.LoginResponse;
import com.certimaster.auth_service.dto.response.RegisterResponse;
import com.certimaster.auth_service.dto.response.UserResponse;
import com.certimaster.common_library.dto.ResponseDto;

public interface UserService {

    ResponseDto<RegisterResponse> register(RegisterRequest registerRequest);

    ResponseDto<LoginResponse> login(LoginRequest loginRequest);

    ResponseDto<LoginResponse> refreshToken(RefreshTokenRequest request);

    ResponseDto<Void> logout(String authorization);

    ResponseDto<Void> verifyEmail(String token);

    ResponseDto<UserResponse> getCurrent(String authorization);

    ResponseDto<UserResponse> getCurrentById(Long userId);
}
