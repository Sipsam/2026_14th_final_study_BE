package com.likelion.finalstudy.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 공통 응답 형식
 */
@Getter
@AllArgsConstructor
@Schema(description = "공통 API 응답")
public class ApiResponse<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;
    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int code;
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;
    @Schema(description = "응답 데이터")
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

