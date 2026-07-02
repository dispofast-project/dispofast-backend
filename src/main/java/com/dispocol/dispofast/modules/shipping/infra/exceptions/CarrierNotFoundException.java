package com.dispocol.dispofast.modules.shipping.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarrierNotFoundException extends RuntimeException {
  public CarrierNotFoundException(String message) {
    super(message);
  }

  public CarrierNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
