package com.likelion.finalstudy.controller;

import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.global.response.ApiResponse;
import com.likelion.finalstudy.service.UserService;
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

/**
 * 사용자 관련 API
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "인증된 사용자 API")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보", description = "로그인한 사용자의 정보를 조회")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"code\":200,\"message\":\"내 정보 조회에 성공했습니다.\",\"data\":{\"id\":1,\"email\":\"test@example.com\",\"name\":\"홍길동\",\"role\":\"USER\",\"createdAt\":\"2026-05-27T15:00:00\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(schema = @Schema(implementation = com.likelion.finalstudy.global.response.ApiResponse.class)))
    })
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

