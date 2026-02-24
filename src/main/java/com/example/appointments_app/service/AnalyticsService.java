package com.example.appointments_app.service;

import com.example.appointments_app.elasticsearch.ElasticSearchService;
import com.example.appointments_app.model.ScreensDTO.InsightsDTO;
import com.example.appointments_app.model.data_aggregation.RevenueData;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.appointments_app.model.ScreensDTO.InsightsDTOBuilder.anInsightsDTO;

@Service
public class AnalyticsService {
    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper om;


    public record AnalyticsConfig(String range, String interval) {}


    public AnalyticsService(ElasticSearchService elasticSearchService,
                            ObjectMapper om){
        this.elasticSearchService = elasticSearchService;
        this.om = om;
    }

    /***
     *
     * @param userSelection - The user selection of range
     * @return - range that we can use inside the elastic search request
     */
    private AnalyticsConfig getUserSelection(String userSelection){
        return switch (userSelection) {
            case "5_DAYS" -> new AnalyticsService.AnalyticsConfig("now-5d/d", "1d");
            case "7_DAYS" -> new AnalyticsService.AnalyticsConfig("now-7d/d", "1d");
            case "30_DAYS" -> new AnalyticsService.AnalyticsConfig("now-30d/d", "1d");
            case "6_MONTHS" -> new AnalyticsService.AnalyticsConfig("now-6M/w", "1w");
            case "YEAR" -> new AnalyticsService.AnalyticsConfig("now-1y/M", "1M");
            case "ALL_TIME" -> new AnalyticsService.AnalyticsConfig("0", "1M"); // Epoch 0
            default -> throw new RuntimeException("Invalid interval: " + userSelection);
        };
    }

    /***
     *
     * @param businessId - The business ID
     * @param userSelection - The range that the user selected
     * @return - List of the revenue data between the range
     */
    public List<RevenueData> getRevenueAnalytics(Long businessId, String userSelection) {
        AnalyticsService.AnalyticsConfig dateData = getUserSelection(userSelection);

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

        String response = elasticSearchService.aggregate("appointments_history", finalPipeline);

        return fetchRevenueAggregation(response);
    }

    /***
     *
     * @param data - THe data from the aggregation result
     * @return - List of RevenueData objet that contains all the information about the revenue from the data variable
     */
    private List<RevenueData> fetchRevenueAggregation(String data){
        List<RevenueData> revenueDataList = new ArrayList<>();
        JsonNode root = om.readTree(data);

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

    /***
     *
     * @param businessId - The ID of the business that we want to aggregate
     * @param userSelection - The range that the user selected
     * @return - InsightsDTO with all the insights data
     */
    public InsightsDTO getInsightsPageData(Long businessId, String userSelection){
        AnalyticsConfig analyticsConfig = getUserSelection(userSelection);
        List<RevenueData> revenueDataList = getRevenueAnalytics(businessId, userSelection);

        String pipeline = """
            {
                "size": 0,
                "query": {
                    "bool": {
                        "filter": [
                            {"term": {"businessId": %d}},
                            {"range": {"timeCreated": {"gte": "%s"}}}
                        ]
                    }
                },
                "aggs": {
                    "count_new_customers": {
                        "filter": { "term": { "firstTimeCustomer": true } }
                    }
                }
            }
            """;

        String finalPipeline = String.format(pipeline, businessId, analyticsConfig.range);

        String response = elasticSearchService.aggregate("appointments_history", finalPipeline);

        JsonNode root = om.readTree(response);

        long total_bookings = root.path("hits").path("total").path("value").asLong();
        int total_new_customers = root.path("aggregations").path("count_new_customers").path("doc_count").asInt();

        return anInsightsDTO().withRevenueDataList(revenueDataList)
                .withBookings(total_bookings)
                .withNew_customers(total_new_customers)
                .withRating(4.9)
                .build();
    }

}
