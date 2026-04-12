package com.example.appointments_app.service;

import com.example.appointments_app.exception.BaseException;
import com.example.appointments_app.exception.FileTypeException;
import com.example.appointments_app.model.AWS.S3SignedURLResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${aws.bucketName}")
    private String bucketName;
    private final String S3_BUCKET_PATH = "assets/pics/";

    public S3Service(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ""; // אין סיומת
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public S3SignedURLResponse generatePresignedUrlForUpload(String fileName) {

        String fileType = getFileExtension(fileName);

        fileType = switch (fileType) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> throw new FileTypeException("Invalid file type");
        };
        String uniqueID = UUID.randomUUID().toString().substring(0, 8);
        String finalFileName = uniqueID + "_" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(S3_BUCKET_PATH + finalFileName)
                .contentType(fileType)
                .build();

        PresignedPutObjectRequest uploadRequest = s3Presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest));

        return new S3SignedURLResponse(uploadRequest.url().toString(), finalFileName);
    }

}
