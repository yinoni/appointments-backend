package com.example.appointments_app.service;

import com.example.appointments_app.elasticsearch.ElasticSearchService;
import com.example.appointments_app.model.ScreensDTO.InsightsDTO;
import com.example.appointments_app.model.data_aggregation.RevenueData;
import com.example.appointments_app.model.data_aggregation.ServicePerformanceDTO;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.appointments_app.model.ScreensDTO.InsightsDTOBuilder.anInsightsDTO;

@Service
public class AnalyticsService {
    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper om;


    public record AnalyticsConfig(String range, String interval, String intervalType, String prevRange) {}


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
            case "5D" -> new AnalyticsService.AnalyticsConfig("now-5d/d", "1d", "fixed_interval", "now-10d/d");
            case "7D" -> new AnalyticsService.AnalyticsConfig("now-7d/d", "1d", "fixed_interval", "now-14d/d");
            case "1M" -> new AnalyticsService.AnalyticsConfig("now-30d/d", "1d", "fixed_interval", "now-60d/d");
            case "6M" -> new AnalyticsService.AnalyticsConfig("now-6M/w", "1w", "calendar_interval", "now-12M/w");
            case "1Y" -> new AnalyticsService.AnalyticsConfig("now-1y/M", "1M", "calendar_interval", "now-2y/M");
            case "ALL_TIME" -> new AnalyticsService.AnalyticsConfig("2020-01-01", "1M", "calendar_interval", "1970-01-01"); // Epoch 0
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
                     "calendar_interval": "%s",
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
                .path("graph_data")
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

    public Double getGrowthPercent(double previous, double current){
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0; // אם עלינו מ-0 למשהו, זו צמיחה של 100%
        }
        double growth = ((current - previous) / previous) * 100;
        return Math.round(growth * 100.0) / 100.0; // עיגול ל-2 ספרות אחרי הנקודה
    }

    /***
     *
     * @param businessId - The ID of the business that we want to aggregate
     * @param userSelection - The range that the user selected
     * @return - InsightsDTO with all the insights data
     */
    public InsightsDTO getInsightsPageData(Long businessId, String userSelection){
        AnalyticsConfig dateData = getUserSelection(userSelection);

        String pipeline = """
                {
                  "size": 0,
                  "query": {
                    "bool": {
                      "filter": [ { "term": { "businessId": %d } } ]
                    }
                  },
                  "aggs": {
                    "graph_data": {
                      "date_histogram": {
                        "field": "timeCreated",
                        "%s": "%s",
                        "format": "yyyy-MM-dd",
                        "extended_bounds": { "min": "%s", "max": "now/d" }
                      },
                      "aggs": {
                        "daily_revenue": { "sum": { "field": "servicePrice" } }
                      }
                    },
                    "period_comparison": {
                      "filters": {
                        "filters": {
                          "current": { "range": { "timeCreated": { "gte": "%s", "lte": "now/d" } } },
                          "previous": { "range": { "timeCreated": { "gte": "now-60d/d", "lt": "now-30d/d" } } }
                        }
                      },
                      "aggs": {
                        "total_revenue": { "sum": { "field": "servicePrice" } },
                        "new_customers": { "filter": { "term": { "firstTimeCustomer": true } } }
                      }
                    },
                    "bookings_by_service_name": {
                       "terms": {
                         "field": "serviceName.keyword"
                       },
                       "aggs": {
                        "total_revenue": {
                            "sum": {"field": "servicePrice"}
                        }
                       }
                    }
                  }
                }
            """;

        String finalPipeline = String.format(pipeline,
                businessId,
                dateData.intervalType,
                dateData.interval,
                dateData.range,
                dateData.range,
                dateData.prevRange);

        String response = elasticSearchService.aggregate("appointments_history", finalPipeline);

        JsonNode root = om.readTree(response);

        long current_total_bookings = root.path("aggregations").path("period_comparison").path("buckets").path("current").path("doc_count").asLong(0L);
        int current_total_new_customers = root.path("aggregations").path("period_comparison").path("buckets").path("current").path("new_customers").path("doc_count").asInt(0);
        long previous_total_bookings = root.path("aggregations").path("period_comparison").path("buckets").path("previous").path("doc_count").asLong(0L);
        int previous_total_new_customers = root.path("aggregations").path("period_comparison").path("buckets").path("previous").path("new_customers").path("doc_count").asInt(0);
        double current_revenue = root.path("aggregations").path("period_comparison").path("buckets").path("current").path("total_revenue").path("value").asDouble(0D);
        double previous_revenue = root.path("aggregations").path("period_comparison").path("buckets").path("previous").path("total_revenue").path("value").asDouble(0D);
        double revenue_growth = getGrowthPercent(previous_revenue, current_revenue);
        double bookings_growth = getGrowthPercent(previous_total_bookings, current_total_bookings);
        double new_customers_growth = getGrowthPercent(previous_total_new_customers, current_total_new_customers);

        List<RevenueData> revenueDataList = fetchRevenueAggregation(response);

        List<ServicePerformanceDTO> services_performance = fetchBestServicePerformances(response);


        return anInsightsDTO().withRevenueDataList(revenueDataList)
                .withBookings(current_total_bookings)
                .withNew_customers(current_total_new_customers)
                .withRating(4.9)
                .withServicesPerformance(services_performance)
                .withRevenueGrowth(revenue_growth)
                .withBookingsGrowth(bookings_growth)
                .withNewCustomersGrowth(new_customers_growth)
                .build();
    }

    /***
     *
     * @param data - The data from the aggregation
     * @return - The best performing services for the business with id = {businessId}
     */
    public List<ServicePerformanceDTO> fetchBestServicePerformances(String data){
        List<ServicePerformanceDTO> services_performance = new ArrayList<>();

        JsonNode root = om.readTree(data);
        JsonNode buckets = root.path("aggregations").path("bookings_by_service_name").path("buckets");

        if(buckets.isArray()){
            for(JsonNode bucket : buckets){
                String serviceName = bucket.path("key").asString();
                int bookings = bucket.path("doc_count").asInt();
                double total_revenue = bucket.path("total_revenue"). path("value").asDouble();
                services_performance.add(new ServicePerformanceDTO(serviceName, bookings, total_revenue));
            }
        }

       return services_performance;
    }

}
