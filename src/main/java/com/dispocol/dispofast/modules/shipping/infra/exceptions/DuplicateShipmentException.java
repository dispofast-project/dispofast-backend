package com.dispocol.dispofast.modules.shipping.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateShipmentException extends RuntimeException {

  public DuplicateShipmentException(String message) {
    super(message);
  }
}
