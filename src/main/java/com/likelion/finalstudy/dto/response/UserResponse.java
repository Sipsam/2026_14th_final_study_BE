package com.likelion.finalstudy.dto.response;

import com.likelion.finalstudy.domain.user.Role;
import com.likelion.finalstudy.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String email;

    private String name;

    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * User 엔티티에서 UserResponse로 변환
     */
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

