package com.indivaragroup.jdt17.spring.rest.api.exception;

import com.indivaragroup.jdt17.spring.rest.api.models.response.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> handleResponseStatusException(ResponseStatusException exception) {
        WebResponse<String> response = WebResponse.<String>builder()
                .code(exception.getStatusCode().value())
                .status("error")
                .data(exception.getReason())
                .build();

        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<String>> handleValidationException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        WebResponse<String> response = WebResponse.<String>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status("error")
                .data(errorMessage)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<String>> handleGeneralException(Exception exception) {
        WebResponse<String> response = WebResponse.<String>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status("error")
                .data(exception.getMessage() != null ? exception.getMessage() : "Internal Server Error")
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
