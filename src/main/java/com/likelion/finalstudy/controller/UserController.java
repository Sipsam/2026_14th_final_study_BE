package com.likelion.finalstudy.controller;

import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.global.response.ApiResponse;
import com.likelion.finalstudy.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보", description = "로그인한 사용자의 정보를 조회")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || "anonymousUser".equalsIgnoreCase(email)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        log.info("Get current user info: {}", email);
        UserResponse response = userService.getMyInfo(email);
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회에 성공했습니다.", response));
    }
}

