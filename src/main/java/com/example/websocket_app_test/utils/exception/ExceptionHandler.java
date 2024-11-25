package com.example.websocket_app_test.utils.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<Object> errorMessages = Arrays.stream(
                Objects.requireNonNull(ex.getDetailMessageArguments())).toList();

        logger.error("valid exception: " + errorMessages.get(1));
        HttpErrorResponse response = HttpErrorResponse.of(
                (String) errorMessages.get(1),
                422);

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpErrorResponse> handleException(ApiException e) {
        logger.error("handling ApiException: {}", e.getMessage());
        var response = HttpErrorResponse.of(e.getMessage(), e.getStatus());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getStatus()));
    }

    // handle other errors as server unexpected errors
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> handleException(Exception e) {
        logger.error("Unhandled exception", e);
        var response = HttpErrorResponse.of("Unexpected error", 500);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
