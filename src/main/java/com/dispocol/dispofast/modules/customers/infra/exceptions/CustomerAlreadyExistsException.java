package com.dispocol.dispofast.modules.customers.infra.exceptions;

public class CustomerAlreadyExistsException extends RuntimeException {

  public CustomerAlreadyExistsException(String message) {
    super(message);
  }
}
