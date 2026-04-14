package com.dispocol.dispofast.modules.orders.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SalesOrderAlreadyExistsException extends RuntimeException {

  public SalesOrderAlreadyExistsException(String message) {
    super(message);
  }
}
