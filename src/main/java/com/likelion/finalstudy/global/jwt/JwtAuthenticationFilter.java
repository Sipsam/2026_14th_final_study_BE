package com.likelion.finalstudy.global.jwt;

import com.likelion.finalstudy.global.exception.CustomException;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.global.security.CustomAuthenticationEntryPoint;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_ERROR_CODE = "authErrorCode";
    private static final String AUTH_ERROR_MESSAGE = "authErrorMessage";

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public JwtAuthenticationFilter(JwtProvider jwtProvider,
                                   JwtProperties jwtProperties,
                                   CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(jwtProperties.getAccessToken().getHeader());

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            handleAuthenticationFailure(request, response,
                    new CustomException(ErrorCode.INVALID_TOKEN, "Bearer 토큰 형식이 아닙니다."));
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            Claims claims = jwtProvider.parseClaims(token);
            String email = claims.get("email", String.class);
            if (email == null || email.isBlank()) {
                email = claims.getSubject();
            }

            if (email == null || email.isBlank()) {
                throw new CustomException(ErrorCode.INVALID_TOKEN, "토큰에 사용자 식별 정보가 없습니다.");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (CustomException ex) {
            handleAuthenticationFailure(request, response, ex);
        }
    }

    private void handleAuthenticationFailure(HttpServletRequest request,
                                             HttpServletResponse response,
                                             CustomException ex) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        request.setAttribute(AUTH_ERROR_CODE, ex.getErrorCode().getCode());
        request.setAttribute(AUTH_ERROR_MESSAGE, ex.getResolvedMessage());
        customAuthenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException(ex.getResolvedMessage())
        );
    }
}

