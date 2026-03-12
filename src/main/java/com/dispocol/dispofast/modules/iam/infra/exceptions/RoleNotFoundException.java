package com.dispocol.dispofast.modules.iam.infra.exceptions;

public class RoleNotFoundException extends RuntimeException {
  public RoleNotFoundException(String message) {
    super(message);
  }
}
