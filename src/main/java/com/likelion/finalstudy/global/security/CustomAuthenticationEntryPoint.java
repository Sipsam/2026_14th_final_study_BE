package com.likelion.finalstudy.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalstudy.global.exception.ErrorCode;
import com.likelion.finalstudy.global.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String AUTH_ERROR_CODE = "authErrorCode";
    private static final String AUTH_ERROR_MESSAGE = "authErrorMessage";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        Object statusCode = request.getAttribute(AUTH_ERROR_CODE);
        Object statusMessage = request.getAttribute(AUTH_ERROR_MESSAGE);

        int code = statusCode instanceof Integer ? (Integer) statusCode : ErrorCode.UNAUTHORIZED.getCode();
        String message = statusMessage instanceof String && !((String) statusMessage).isBlank()
                ? (String) statusMessage
                : ErrorCode.UNAUTHORIZED.getMessage();

        response.setStatus(code);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.fail(code, message)
        ));
    }
}


