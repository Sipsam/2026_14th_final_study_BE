package com.likelion.finalstudy.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 공통 응답 형식
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final int code;
    private final String message;
    private final T data;

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, 200, "요청이 성공적으로 처리되었습니다.", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data);
    }

    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}

