package com.dispocol.dispofast.shared.error.configuration;

import com.dispocol.dispofast.modules.iam.infra.exceptions.UserNotFoundException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.InvalidOrderStateException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.SalesOrderAlreadyExistsException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.SalesOrderNotFoundException;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GlobalErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining("; "));
    log.warn("Validation failed: {}", errors);
    GlobalErrorResponse errorResponse =
        new GlobalErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "ValidationException",
            errors,
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GlobalErrorResponse> handleBadRequest(
      IllegalArgumentException ex, HttpServletRequest request) {
    log.warn("Bad request {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<GlobalErrorResponse> handleUnsuportedOperation(
      UnsupportedOperationException ex, HttpServletRequest request) {
    log.warn("Unsupported operation {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleUserNotFound(
      RuntimeException ex, HttpServletRequest request) {
    log.warn("El usuario no fue encontrado: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(SalesOrderNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleSalesOrderNotFound(
      SalesOrderNotFoundException ex, HttpServletRequest request) {
    log.warn("Sales order not found: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(SalesOrderAlreadyExistsException.class)
  public ResponseEntity<GlobalErrorResponse> handleSalesOrderAlreadyExists(
      SalesOrderAlreadyExistsException ex, HttpServletRequest request) {
    log.warn("Sales order already exists: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidOrderStateException.class)
  public ResponseEntity<GlobalErrorResponse> handleInvalidOrderState(
      InvalidOrderStateException ex, HttpServletRequest request) {
    log.warn("Invalid order state transition: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  public ResponseEntity<GlobalErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {
    log.error("Internal server error", ex);
    return buildErrorResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<GlobalErrorResponse> buildErrorResponseEntity(
      Exception ex, HttpServletRequest request, HttpStatus status) {
    GlobalErrorResponse errorResponse =
        new GlobalErrorResponse(
            Instant.now(),
            status.value(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(status).body(errorResponse);
  }
}
