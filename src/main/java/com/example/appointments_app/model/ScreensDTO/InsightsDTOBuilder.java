package com.example.appointments_app.model.ScreensDTO;

import com.example.appointments_app.model.data_aggregation.RevenueData;
import com.example.appointments_app.model.data_aggregation.ServicePerformanceDTO;

import java.util.List;

public final class InsightsDTOBuilder {
    private List<RevenueData> revenueDataList;
    private long bookings;
    private int new_customers;
    private double rating;
    private List<ServicePerformanceDTO> servicesPerformance;

    private InsightsDTOBuilder() {
    }

    public static InsightsDTOBuilder anInsightsDTO() {
        return new InsightsDTOBuilder();
    }

    public InsightsDTOBuilder withRevenueDataList(List<RevenueData> revenueDataList) {
        this.revenueDataList = revenueDataList;
        return this;
    }

    public InsightsDTOBuilder withBookings(long bookings) {
        this.bookings = bookings;
        return this;
    }

    public InsightsDTOBuilder withNew_customers(int new_customers) {
        this.new_customers = new_customers;
        return this;
    }

    public InsightsDTOBuilder withRating(double rating) {
        this.rating = rating;
        return this;
    }

    public InsightsDTOBuilder withServicesPerformance(List<ServicePerformanceDTO> servicesPerformance) {
        this.servicesPerformance = servicesPerformance;
        return this;
    }

    public InsightsDTO build() {
        InsightsDTO insightsDTO = new InsightsDTO();
        insightsDTO.setRevenueDataList(revenueDataList);
        insightsDTO.setBookings(bookings);
        insightsDTO.setNew_customers(new_customers);
        insightsDTO.setRating(rating);
        insightsDTO.setServicesPerformance(servicesPerformance);
        return insightsDTO;
    }
}
