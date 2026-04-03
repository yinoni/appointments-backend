package com.example.appointments_app.controller;

import com.example.appointments_app.model.AWS.S3SignedURLResponse;
import com.example.appointments_app.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/aws")
public class AWSController {
    private final S3Service s3Service;


    public AWSController(S3Service s3Service){
        this.s3Service = s3Service;
    }

    public enum S3Method {
        GET, PUT
    }


    @GetMapping("/get-presigned-url")
    public ResponseEntity<?> getUploadURL(@RequestParam String fileName, @RequestParam(defaultValue = "PUT") S3Method method){
        S3SignedURLResponse s3SignedURLResponse = null;

        if(S3Method.PUT == method){
             s3SignedURLResponse = s3Service.generatePresignedUrlForUpload(fileName);
        }


        return ResponseEntity.ok(s3SignedURLResponse);
    }
}
