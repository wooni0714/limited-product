package com.limited.product.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto.of(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(final BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDto.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}
