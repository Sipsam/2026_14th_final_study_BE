package com.likelion.finalstudy.service;

import com.likelion.finalstudy.dto.request.LoginRequest;
import com.likelion.finalstudy.dto.request.RegisterRequest;
import com.likelion.finalstudy.dto.response.LoginResponse;
import com.likelion.finalstudy.dto.response.UserResponse;

/**
 * 인증 서비스 인터페이스
 */
public interface AuthService {

    /**
     * 사용자 로그인
     */
    LoginResponse login(LoginRequest request);

    /**
     * 사용자 회원가입
     */
    UserResponse register(RegisterRequest request);

    /**
     * 토큰 유효성 검증
     */
    boolean validateToken(String token);
}

