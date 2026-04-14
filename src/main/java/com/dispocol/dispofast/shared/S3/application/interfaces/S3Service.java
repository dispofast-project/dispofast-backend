package com.dispocol.dispofast.shared.S3.application.interfaces;

import java.io.InputStream;

public interface S3Service {

  void uploadFile(
      String bucketName,
      String fileName,
      InputStream inputStream,
      String contentType,
      long contentLength);

  byte[] downloadFile(String bucketName, String fileName);

  String getFileUrl(String bucketName, String fileName);

  void deleteFile(String bucketName, String fileName);
}
