package com.dispocol.dispofast.shared.S3.application.impl;

import java.io.InputStream;

import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;

public class S3ServiceImpl implements S3Service {

    @Override
    public void uploadFile(String bucketName, String fileName, InputStream inputStream, String contentType, long contentLength) {
        // Implement the logic to upload a file to S3
    }

    @Override
    public String getFileUrl(String bucketName, String fileName) {
        // Implement the logic to get the URL of a file stored in S3
        return null;
    }

    @Override
    public void deleteFile(String bucketName, String fileName) {
        // Implement the logic to delete a file from S3
    }
    

}
