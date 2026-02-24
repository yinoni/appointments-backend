package com.example.appointments_app.elasticsearch;

import com.example.appointments_app.model.data_aggregation.RevenueData;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ElasticSearchService {
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchService.class);
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ELASTIC_URL = "http://localhost:9200";


    public record AnalyticsConfig(String range, String interval) {}

    /***
     *
     * @param index - The index we want to insert
     * @param id - The id
     * @param data - The data
     * @return - The response as String
     * @throws IOException
     */
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

    /***
     *
     * @param index - The index in the elasticsearch (If we want elasticsearch to give the document ID)
     * @param data - The data to insert into the index
     * @throws IOException
     */
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

    /***
     *
     * @param index - The index in the elasticsearch
     * @param query - The query for the search
     * @return - The search result as String
     * @throws IOException
     */
    public String search(String index, String query) throws IOException {
        Request request = new Request.Builder()
                .url(ELASTIC_URL + "/" + index + "/_search?q=" + query)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /***
     *
     * @param index - The index in the elasticsearch
     * @param pipeline - The aggregation pipeline
     * @return - The aggregation result as string
     */
    public String aggregate(String index, String pipeline){

        RequestBody body = RequestBody.create(
                pipeline, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(ELASTIC_URL + "/" + index + "/_search")
                .post(body)
                .build();

        try(Response res = client.newCall(request).execute()){
            if (res.isSuccessful() && res.body() != null) {
                return res.body().string();
            }

        } catch (IOException e) {
            log.info(e.getMessage());
        }

        return "{}";
    }
}
