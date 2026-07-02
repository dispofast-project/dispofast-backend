package com.dispocol.dispofast.modules.shipping.infra.exceptions;

import com.dispocol.dispofast.shared.error.ResourceNotFoundException;

public class DriverNotFoundException extends ResourceNotFoundException {
  public DriverNotFoundException(String message) {
    super(message);
  }
}
