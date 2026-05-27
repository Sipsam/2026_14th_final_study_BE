package com.likelion.finalstudy.controller;

import com.likelion.finalstudy.dto.request.LoginRequest;
import com.likelion.finalstudy.dto.request.RegisterRequest;
import com.likelion.finalstudy.dto.response.LoginResponse;
import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.response.ApiResponse;
import com.likelion.finalstudy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 인증 관련 API
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "인증 관련 API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login request: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", response));
    }

    @PostMapping("/signup")
    @Operation(summary = "사용자 회원가입", description = "이메일, 비밀번호, 이름으로 회원가입")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody RegisterRequest request) {
        log.info("User signup request: {}", request.getEmail());
        UserResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입에 성공했습니다.", response));
    }
}

