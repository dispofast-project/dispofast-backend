package com.dispocol.dispofast.modules.customers.infra.exceptions;

public class CustomerNotFoundException extends RuntimeException {

  public CustomerNotFoundException(String message) {
    super(message);
  }
}
