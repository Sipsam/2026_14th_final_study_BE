package com.likelion.finalstudy.service;

import com.likelion.finalstudy.domain.user.Role;
import com.likelion.finalstudy.domain.user.User;
import com.likelion.finalstudy.dto.request.LoginRequest;
import com.likelion.finalstudy.dto.request.RegisterRequest;
import com.likelion.finalstudy.dto.response.LoginResponse;
import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.global.jwt.JwtProperties;
import com.likelion.finalstudy.global.jwt.JwtProvider;
import com.likelion.finalstudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public UserResponse signup(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.createUser(request.getEmail(), encodedPassword, request.getName(), Role.USER);
        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessToken().getExpiration())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtProvider.validateToken(token);
    }
}

