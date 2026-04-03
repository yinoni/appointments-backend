package com.example.appointments_app.model.AWS;

public class S3SignedURLResponse {

    private String url;
    private String fileName;

    public S3SignedURLResponse() {
    }

    public S3SignedURLResponse(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
