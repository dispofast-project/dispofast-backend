package com.dispocol.dispofast.shared.error.configuration;

import com.dispocol.dispofast.modules.iam.infra.exceptions.PermissionNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.exceptions.RoleNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserAlreadyExistsException;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserNotFoundException;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.InsufficientStockException;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductAlreadyExistsException;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductNotAvailableException;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductNotFoundException;
import com.dispocol.dispofast.modules.invoices.infra.exceptions.InvoiceNotFoundException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.InvalidOrderStateException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.SalesOrderAlreadyExistsException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.SalesOrderNotFoundException;
import com.dispocol.dispofast.shared.error.ForbiddenException;
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

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<GlobalErrorResponse> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {
    log.warn("Illegal state: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<GlobalErrorResponse> handleUnsuportedOperation(
      UnsupportedOperationException ex, HttpServletRequest request) {
    log.warn("Unsupported operation {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<GlobalErrorResponse> handleForbidden(
      ForbiddenException ex, HttpServletRequest request) {
    log.warn("Forbidden: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleUserNotFound(
      UserNotFoundException ex, HttpServletRequest request) {
    log.warn("El usuario no fue encontrado: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<GlobalErrorResponse> handleUserAlreadyExists(
      UserAlreadyExistsException ex, HttpServletRequest request) {
    log.warn("El usuario ya existe: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(RoleNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleRoleNotFound(
      RoleNotFoundException ex, HttpServletRequest request) {
    log.warn("Rol no encontrado: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(PermissionNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handlePermissionNotFound(
      PermissionNotFoundException ex, HttpServletRequest request) {
    log.warn("Permiso no encontrado: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleProductNotFound(
      ProductNotFoundException ex, HttpServletRequest request) {
    log.warn("Producto no encontrado: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ProductAlreadyExistsException.class)
  public ResponseEntity<GlobalErrorResponse> handleProductAlreadyExists(
      ProductAlreadyExistsException ex, HttpServletRequest request) {
    log.warn("El producto ya existe: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ProductNotAvailableException.class)
  public ResponseEntity<GlobalErrorResponse> handleProductNotAvailable(
      ProductNotAvailableException ex, HttpServletRequest request) {
    log.warn("Producto no disponible: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<GlobalErrorResponse> handleInsufficientStock(
      InsufficientStockException ex, HttpServletRequest request) {
    log.warn("Stock insuficiente: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());
    return buildErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvoiceNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleInvoiceNotFound(
      InvoiceNotFoundException ex, HttpServletRequest request) {
    log.warn("Factura no encontrada: {}", ex.getMessage());
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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GlobalErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {
    log.error("Internal server error", ex);
    return buildErrorResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<GlobalErrorResponse> buildErrorResponseEntity(
      Exception ex, HttpServletRequest request, HttpStatus status) {
    return buildErrorResponseEntity(
        ex.getMessage(), ex.getClass().getSimpleName(), request, status);
  }

  private ResponseEntity<GlobalErrorResponse> buildErrorResponseEntity(
      String message, String error, HttpServletRequest request, HttpStatus status) {
    GlobalErrorResponse response =
        new GlobalErrorResponse(
            Instant.now(), status.value(), error, message, request.getRequestURI());
    return ResponseEntity.status(status).body(response);
  }
}
