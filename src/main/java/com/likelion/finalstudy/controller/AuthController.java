package com.likelion.finalstudy.controller;

import com.likelion.finalstudy.dto.request.LoginRequest;
import com.likelion.finalstudy.dto.request.ReissueRequest;
import com.likelion.finalstudy.dto.request.RegisterRequest;
import com.likelion.finalstudy.dto.response.LoginResponse;
import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.global.response.ApiResponse;
import com.likelion.finalstudy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access/Refresh Token 재발급")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(@Valid @RequestBody ReissueRequest request) {
        LoginResponse response = authService.reissue(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 재발급에 성공했습니다.", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자 Refresh Token 삭제")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || "anonymousUser".equalsIgnoreCase(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        authService.logout(email);
        return ResponseEntity.ok(ApiResponse.success("로그아웃에 성공했습니다.", null));
    }
}

