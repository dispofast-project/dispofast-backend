package com.dispocol.dispofast.shared.error.configuration;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request){
        log.warn("Bad request {}", ex.getMessage());
        return buildErrorResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<GlobalErrorResponse> handleUnsuportedOperation(UnsupportedOperationException ex, HttpServletRequest request){
        log.warn("Unsupported operation {}", ex.getMessage());
        return buildErrorResponseEntity(ex, request, HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<GlobalErrorResponse> handleGenericException(Exception ex, HttpServletRequest request){
        log.error("Internal server error", ex);
        return buildErrorResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Builds a GlobalErrorResponse and wraps it in a ResponseEntity with the given status.
     * @param ex the exception that was thrown
     * @param request the HTTP request that caused the exception
     * @param status the HTTP status to return
     * @return a ResponseEntity containing the GlobalErrorResponse
     */
    private ResponseEntity<GlobalErrorResponse> buildErrorResponseEntity(
        Exception ex,
        HttpServletRequest request,
        HttpStatus status
    ) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            Instant.now(),
            status.value(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
