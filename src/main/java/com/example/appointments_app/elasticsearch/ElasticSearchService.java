package com.example.appointments_app.elasticsearch;

import com.example.appointments_app.model.data_aggregation.WeeklyRevenueData;
import okhttp3.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<WeeklyRevenueData> getWeeklyRevenue(Long businessId) throws IOException {
        String pipeline = """
        {
          "size": 0, 
          "query": {
            "bool": {
              "filter": [
                { "term": { "businessId": %d } },
                { "range": { "timeCreated": { "gte": "now-7d/d", "lte": "now/d" } } }
              ]
            }
          },
          "aggs": {
            "revenue_over_time": {
              "date_histogram": {
                "field": "timeCreated",
                "fixed_interval": "1d",
                "format": "yyyy-MM-dd",
                "extended_bounds": {
                  "min": "now-7d/d",
                  "max": "now/d"
                }
              },
              "aggs": {
                "daily_revenue": {
                  "sum": { "field": "servicePrice" }
                }
              }
            }
          }
        }
    """;
        String finalPipeline = String.format(pipeline, businessId);

        RequestBody body = RequestBody.create(
                finalPipeline, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(ELASTIC_URL + "/appointments_history" + "/_search")
                .post(body)
                .build();

        try (Response res = client.newCall(request).execute()) {
            if (!res.isSuccessful()) {
                System.err.println("Failed to insert to Elastic: " + res.code() + " " + res.message());
            } else {
                String resBody = res.body().string();
                return this.getWeeklyRevenue(resBody);
            }
        }

        return new ArrayList<>();

    }


    private List<WeeklyRevenueData> getWeeklyRevenue(String data){
        List<WeeklyRevenueData> revenueDataList = new ArrayList<>();
        JsonNode root = objectMapper.readTree(data);

        JsonNode buckets = root.path("aggregations")
                .path("revenue_over_time")
                .path("buckets");

        if (buckets.isArray()) {
            for (JsonNode bucket : buckets) {
                String date = bucket.path("key_as_string").asText();
                double amount = bucket.path("daily_revenue").path("value").asDouble();

                revenueDataList.add(new WeeklyRevenueData(date, amount));
            }
        }

        return revenueDataList;
    }


}
