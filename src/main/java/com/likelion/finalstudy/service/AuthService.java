package com.likelion.finalstudy.service;

import com.likelion.finalstudy.dto.request.LoginRequest;
import com.likelion.finalstudy.dto.request.ReissueRequest;
import com.likelion.finalstudy.dto.request.RegisterRequest;
import com.likelion.finalstudy.dto.response.LoginResponse;
import com.likelion.finalstudy.dto.response.UserResponse;

/**
 * 인증 서비스 인터페이스
 */
public interface AuthService {

    /**
     * 회원가입
     */
    UserResponse signup(RegisterRequest request);

    /**
     * 로그인
     */
    LoginResponse login(LoginRequest request);

    /**
     * Access/Refresh Token 재발급
     */
    LoginResponse reissue(ReissueRequest request);

    /**
     * 로그아웃
     */
    void logout(String email);


    /**
     * 토큰 유효성 검증
     */
    boolean validateToken(String token);
}

