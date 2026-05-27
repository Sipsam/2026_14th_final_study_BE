package com.likelion.finalstudy.service;

import com.likelion.finalstudy.domain.user.User;
import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getMyInfo(String email) {
        return getUserByEmail(email);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.fromEntity(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

