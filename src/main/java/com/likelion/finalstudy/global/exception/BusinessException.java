package com.likelion.finalstudy.global.exception;

import lombok.Getter;

/**
 * 공통 예외 클래스
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
}

