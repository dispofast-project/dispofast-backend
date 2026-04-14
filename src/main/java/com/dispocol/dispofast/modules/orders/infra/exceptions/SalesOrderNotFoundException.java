package com.dispocol.dispofast.modules.orders.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SalesOrderNotFoundException extends RuntimeException {

  public SalesOrderNotFoundException(String message) {
    super(message);
  }
}
