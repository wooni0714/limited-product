package com.limited.product.common.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponseDto(
        int code,
        String message
) {
    public static ErrorResponseDto of(final HttpStatus httpStatus, final String message) {
        return new ErrorResponseDto(httpStatus.value(), message);
    }
}
