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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Authentication", description = "인증/인가 및 JWT, Refresh Token 관련 API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"code\":200,\"message\":\"로그인에 성공했습니다.\",\"data\":{\"accessToken\":\"...\",\"refreshToken\":\"...\",\"tokenType\":\"Bearer\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 오류 또는 비밀번호 불일치",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(value = "{\"email\":\"test@example.com\",\"password\":\"password1234\"}"))
            )
            @Valid @RequestBody LoginRequest request) {
        log.info("User login request: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", response));
    }

    @PostMapping("/signup")
    @Operation(summary = "사용자 회원가입", description = "이메일, 비밀번호, 이름으로 회원가입")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<UserResponse>> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(value = "{\"email\":\"test@example.com\",\"password\":\"password1234\",\"name\":\"홍길동\"}"))
            )
            @Valid @RequestBody RegisterRequest request) {
        log.info("User signup request: {}", request.getEmail());
        UserResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입에 성공했습니다.", response));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access/Refresh Token 재발급")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "리프레시 토큰 만료/무효",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "토큰 재발급 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReissueRequest.class),
                            examples = @ExampleObject(value = "{\"refreshToken\":\"...\"}"))
            )
            @Valid @RequestBody ReissueRequest request) {
        LoginResponse response = authService.reissue(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 재발급에 성공했습니다.", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자 Refresh Token 삭제")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class)))
    })
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

