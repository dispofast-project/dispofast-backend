package com.dispocol.dispofast.shared.S3.application.impl;

import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  private final S3Client s3Client;

  @Value("${app.minio.endpoint}")
  private String minioEndpoint;

  @Override
  public void uploadFile(
      String bucketName,
      String fileName,
      InputStream inputStream,
      String contentType,
      long contentLength) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(contentType)
            .contentLength(contentLength)
            .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
  }

  @Override
  public byte[] downloadFile(String bucketName, String fileName) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(fileName).build();
    return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
  }

  @Override
  public String getFileUrl(String bucketName, String fileName) {
    return minioEndpoint + "/" + bucketName + "/" + fileName;
  }

  @Override
  public void deleteFile(String bucketName, String fileName) {
    DeleteObjectRequest deleteObjectRequest =
        DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build();
    s3Client.deleteObject(deleteObjectRequest);
  }
}
