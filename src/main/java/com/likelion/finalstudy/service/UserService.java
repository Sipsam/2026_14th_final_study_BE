package com.likelion.finalstudy.service;

import com.likelion.finalstudy.dto.response.UserResponse;

/**
 * 사용자 서비스 인터페이스
 */
public interface UserService {

    /**
     * 로그인한 사용자 정보 조회
     */
    UserResponse getMyInfo(String email);

    /**
     * 이메일로 사용자 정보 조회
     */
    UserResponse getUserByEmail(String email);


    /**
     * 이메일이 존재하는지 확인
     */
    boolean existsByEmail(String email);
}

