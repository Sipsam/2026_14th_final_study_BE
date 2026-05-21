package com.likelion.finalstudy.controller;

import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "사용자 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보", description = "로그인한 사용자의 정보를 조회")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        log.info("Get current user info");
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", null));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 정보 조회", description = "사용자ID로 사용자 정보를 조회")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId) {
        log.info("Get user info for userId: {}", userId);
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", null));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long userId,
            @RequestBody UserResponse userResponse) {
        log.info("Update user info for userId: {}", userId);
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 수정 성공", null));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("Delete user for userId: {}", userId);
        // 구현 예정
        return ResponseEntity.ok(ApiResponse.success("사용자 삭제 성공", null));
    }
}

