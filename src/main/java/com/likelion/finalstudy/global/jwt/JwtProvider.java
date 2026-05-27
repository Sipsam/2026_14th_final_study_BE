package com.likelion.finalstudy.global.jwt;

import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 제공자
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(Long userId, String email) {
        return generateToken(userId, email, jwtProperties.getAccessToken().getExpiration(), ACCESS_TOKEN_TYPE);
    }

    public String generateRefreshToken(Long userId, String email) {
        return generateToken(userId, email, jwtProperties.getRefreshToken().getExpiration(), REFRESH_TOKEN_TYPE);
    }

    private String generateToken(Long userId, String email, long expiration, String tokenType) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiresAt = new Date(now + expiration);

        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 하위 호환용 Access Token 생성
     */
    public String generateAccessToken(String email) {
        return generateAccessToken(null, email);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseClaims(token);
        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        return REFRESH_TOKEN_TYPE.equals(tokenType);
    }

    public void validateRefreshToken(String token) {
        Claims claims = parseClaims(token);
        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!REFRESH_TOKEN_TYPE.equals(tokenType)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "리프레시 토큰이 아닙니다.");
        }
    }

    /**
     * 토큰에서 email(subject) 추출
     */
    public String extractEmail(String token) {
        Claims claims = parseClaims(token);
        String email = claims.get(CLAIM_EMAIL, String.class);
        return email != null ? email : claims.getSubject();
    }

    public String extractSubject(String token) {
        return extractEmail(token);
    }

    /**
     * 토큰에서 userId 추출
     */
    public Long extractUserId(String token) {
        Claims claims = parseClaims(token);
        Object value = claims.get(CLAIM_USER_ID);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer integerValue) {
            return integerValue.longValue();
        }
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return Long.parseLong(stringValue);
        }

        throw new CustomException(ErrorCode.INVALID_TOKEN, "토큰의 userId 형식이 올바르지 않습니다.");
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (CustomException e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "토큰이 비어 있습니다.");
        }

        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN, ErrorCode.EXPIRED_TOKEN.getMessage());
        } catch (MalformedJwtException | UnsupportedJwtException | SecurityException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "잘못된 JWT 형식입니다.");
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "토큰 문자열이 비어 있습니다.");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
    }
}

