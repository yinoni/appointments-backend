package com.example.appointments_app.elasticsearch;

import com.example.appointments_app.model.data_aggregation.RevenueData;
import okhttp3.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ELASTIC_URL = "http://localhost:9200";


    public record AnalyticsConfig(String range, String interval) {}

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

    public List<RevenueData> getRevenueAnalytics(Long businessId, String userSelection){
        AnalyticsConfig dateData = switch (userSelection) {
            case "5_DAYS" -> new AnalyticsConfig("now-5d/d", "1d");
            case "7_DAYS" -> new AnalyticsConfig("now-7d/d", "1d");
            case "30_DAYS" -> new AnalyticsConfig("now-30d/d", "1d");
            case "6_MONTHS" -> new AnalyticsConfig("now-6M/w", "1w");
            case "YEAR" -> new AnalyticsConfig("now-1y/M", "1M");
            case "ALL_TIME" -> new AnalyticsConfig("0", "1M"); // Epoch 0
            default -> throw new RuntimeException("Invalid interval: " + userSelection);
        };

        List<RevenueData> revenueDataList = new ArrayList<>();

        String pipeline = """
            {
              "size": 0, 
              "query": {
                "bool": {
                  "filter": [
                    { "term": { "businessId": %d } },
                    { "range": { "timeCreated": { "gte": "%s", "lte": "now/d" } } }
                  ]
                }
              },
              "aggs": {
                "revenue_over_time": {
                  "date_histogram": {
                    "field": "timeCreated",
                    "fixed_interval": "%s",
                    "format": "yyyy-MM-dd",
                    "extended_bounds": {
                      "min": "%s",
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

        String finalPipeline = String.format(pipeline, businessId, dateData.range, dateData.interval, dateData.range);

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
                revenueDataList = this.fetchRevenueAggregation(resBody);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return revenueDataList;
    }

    private List<RevenueData> fetchRevenueAggregation(String data){
        List<RevenueData> revenueDataList = new ArrayList<>();
        JsonNode root = objectMapper.readTree(data);

        JsonNode buckets = root.path("aggregations")
                .path("revenue_over_time")
                .path("buckets");

        if (buckets.isArray()) {
            for (JsonNode bucket : buckets) {
                String date = bucket.path("key_as_string").asText();
                double amount = bucket.path("daily_revenue").path("value").asDouble();

                revenueDataList.add(new RevenueData(date, amount));
            }
        }

        return revenueDataList;
    }


}
