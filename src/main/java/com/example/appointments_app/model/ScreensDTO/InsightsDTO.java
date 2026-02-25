package com.example.appointments_app.model.ScreensDTO;

import com.example.appointments_app.model.data_aggregation.RevenueData;
import com.example.appointments_app.model.data_aggregation.ServicePerformanceDTO;

import java.util.List;

public class InsightsDTO {
    private List<RevenueData> revenueDataList;
    private long bookings;
    private int new_customers;
    private double rating;
    private List<ServicePerformanceDTO> servicesPerformance;

    public InsightsDTO() {
    }

    public InsightsDTO(List<RevenueData> revenueDataList, int bookings, double rating, int new_customers) {
        this.revenueDataList = revenueDataList;
        this.bookings = bookings;
        this.rating = rating;
        this.new_customers = new_customers;
    }

    public List<RevenueData> getRevenueDataList() {
        return revenueDataList;
    }

    public void setRevenueDataList(List<RevenueData> revenueDataList) {
        this.revenueDataList = revenueDataList;
    }

    public long getBookings() {
        return bookings;
    }

    public void setBookings(long bookings) {
        this.bookings = bookings;
    }

    public int getNew_customers() {
        return new_customers;
    }

    public void setNew_customers(int new_customers) {
        this.new_customers = new_customers;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<ServicePerformanceDTO> getServicesPerformance() {
        return servicesPerformance;
    }

    public void setServicesPerformance(List<ServicePerformanceDTO> servicesPerformance) {
        this.servicesPerformance = servicesPerformance;
    }
}
