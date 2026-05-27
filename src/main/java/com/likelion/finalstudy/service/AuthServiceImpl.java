package com.likelion.finalstudy.service;

import com.likelion.finalstudy.domain.auth.RefreshToken;
import com.likelion.finalstudy.domain.user.Role;
import com.likelion.finalstudy.domain.user.User;
import com.likelion.finalstudy.dto.request.LoginRequest;
import com.likelion.finalstudy.dto.request.ReissueRequest;
import com.likelion.finalstudy.dto.request.RegisterRequest;
import com.likelion.finalstudy.dto.response.LoginResponse;
import com.likelion.finalstudy.dto.response.UserResponse;
import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.global.jwt.JwtProperties;
import com.likelion.finalstudy.global.jwt.JwtProvider;
import com.likelion.finalstudy.repository.RefreshTokenRepository;
import com.likelion.finalstudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getEmail());
        upsertRefreshToken(user, refreshToken);

        return toLoginResponse(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse reissue(ReissueRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        jwtProvider.validateRefreshToken(requestRefreshToken);

        String email = jwtProvider.extractEmail(requestRefreshToken);
        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        User user = savedRefreshToken.getUser();

        if (!user.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "토큰 사용자 정보가 올바르지 않습니다.");
        }

        if (savedRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(savedRefreshToken);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN, "만료된 리프레시 토큰입니다.");
        }

        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getEmail());
        savedRefreshToken.renew(newRefreshToken, calculateRefreshTokenExpiresAt());

        return toLoginResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    private void upsertRefreshToken(User user, String refreshToken) {
        LocalDateTime expiresAt = calculateRefreshTokenExpiresAt();
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        token -> token.renew(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.create(user, refreshToken, expiresAt))
                );
    }

    private LocalDateTime calculateRefreshTokenExpiresAt() {
        return LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getRefreshToken().getExpiration()));
    }

    private LoginResponse toLoginResponse(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessToken().getExpiration())
                .refreshExpiresIn(jwtProperties.getRefreshToken().getExpiration())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtProvider.validateToken(token);
    }
}

