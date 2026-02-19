package com.example.appointments_app.elasticsearch;

import okhttp3.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class ElasticSearchService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ELASTIC_URL = "http://localhost:9200";

    // פונקציה להכנסת נתונים (Index)
    public String indexDocument(String index, String id, Object data) throws IOException {
        String json = objectMapper.writeValueAsString(data);

        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(ELASTIC_URL + "/" + index + "/_doc/" + id)
                .put(body) // בדרך כלל משתמשים ב-PUT להכנסה לפי ID
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : "No response";
        }
    }

    public void insertDocument(String index, Object data) throws IOException {
        String json = objectMapper.writeValueAsString(data);

        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(ELASTIC_URL + "/" + index + "/_doc")
                .post(body)
                .build();

        try (Response res = client.newCall(request).execute()) {
            if (!res.isSuccessful()) {
                System.err.println("Failed to insert to Elastic: " + res.code() + " " + res.message());
            } else {
                System.out.println("Document indexed successfully in " + index);
            }
        }
    }

    // פונקציה לחיפוש פשוט
    public String search(String index, String query) throws IOException {
        Request request = new Request.Builder()
                .url(ELASTIC_URL + "/" + index + "/_search?q=" + query)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
