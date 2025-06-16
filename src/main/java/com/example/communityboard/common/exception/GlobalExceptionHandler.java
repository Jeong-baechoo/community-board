package com.example.communityboard.common.exception;

import com.example.communityboard.common.dto.ErrorResponse;
import com.example.communityboard.member.application.exception.DuplicateEmailException;
import com.example.communityboard.member.application.exception.DuplicateLoginIdException;
import com.example.communityboard.member.application.exception.InvalidLoginException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLoginException(
            InvalidLoginException e, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }
    
    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginIdException(
            DuplicateLoginIdException e, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request);
    }
    
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
            DuplicateEmailException e, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("입력값이 잘못되었습니다.");
        return createErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred", e);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
                "서버 오류가 발생했습니다.", request);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(status, message, request.getRequestURI());
        return ResponseEntity.status(status).body(errorResponse);
    }
}