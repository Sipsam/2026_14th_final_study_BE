package com.likelion.finalstudy.service;

import com.likelion.finalstudy.dto.response.UserResponse;

/**
 * 사용자 서비스 인터페이스
 */
public interface UserService {

    /**
     * 사용자명으로 사용자 정보 조회
     */
    UserResponse getUserByUsername(String username);

    /**
     * 사용자ID로 사용자 정보 조회
     */
    UserResponse getUserById(Long userId);

    /**
     * 사용자가 존재하는지 확인
     */
    boolean existsByUsername(String username);

    /**
     * 이메일이 존재하는지 확인
     */
    boolean existsByEmail(String email);
}

