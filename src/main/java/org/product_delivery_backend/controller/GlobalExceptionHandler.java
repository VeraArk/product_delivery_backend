package org.product_delivery_backend.controller;

import org.product_delivery_backend.dto.ErrorResponseDto;
import org.product_delivery_backend.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponseDto> buildErrorResponse(String message, HttpStatus status, String errorType) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(message)
                .statusCode(status.value())
                .errorType(errorType)
                .build();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(ValidationException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "Validation Error");
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponseDto> handleOrderException(OrderException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "OrderStatusError");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, "Not Found");
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedException(UnauthorizedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyExistException(AlreadyExistException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "Already Exist");
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleFileProcessingException(FileProcessingException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "File Processing Error");
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidJwtException(InvalidJwtException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "Invalid JWT Token");
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidDataException(InvalidDataException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "Invalid Data");
    }
}
