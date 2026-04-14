package com.dispocol.dispofast.modules.iam.infra.exceptions;

public class PermissionNotFoundException extends RuntimeException {
  public PermissionNotFoundException(String message) {
    super(message);
  }
}
