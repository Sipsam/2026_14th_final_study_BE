package com.likelion.finalstudy.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detailMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    public CustomException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public String getResolvedMessage() {
        if (detailMessage == null || detailMessage.isBlank()) {
            return errorCode.getMessage();
        }
        return detailMessage;
    }
}

