package com.dispocol.dispofast.modules.shipping.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ShipmentNotEditableException extends RuntimeException {

  public ShipmentNotEditableException(String message) {
    super(message);
  }
}
