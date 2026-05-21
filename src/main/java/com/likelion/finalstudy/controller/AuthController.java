package com.likelion.finalstudy.controller;

import com.likelion.finalstudy.dto.request.LoginRequest;
import com.likelion.finalstudy.dto.response.LoginResponse;
import com.likelion.finalstudy.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 인증 관련 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인", description = "사용자명과 비밀번호로 로그인")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login request: {}", request.getUsername());
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", null));
    }

    @PostMapping("/register")
    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody LoginRequest request) {
        log.info("User register request: {}", request.getUsername());
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", null));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃")
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("User logout");
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token 발급")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken() {
        log.info("Token refresh request");
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", null));
    }
}

