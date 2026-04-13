package com.dispocol.dispofast.shared.S3.infra;

public class UploadFileFailedException extends RuntimeException {
  public UploadFileFailedException(String message) {
    super(message);
  }   
}
