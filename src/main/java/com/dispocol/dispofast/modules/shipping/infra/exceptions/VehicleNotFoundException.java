package com.dispocol.dispofast.modules.shipping.infra.exceptions;

public class VehicleNotFoundException extends RuntimeException {

  public VehicleNotFoundException(String message) {
    super(message);
  }
}
