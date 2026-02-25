package com.example.appointments_app.model.data_aggregation;

public class ServicePerformanceDTO {

    private String serviceName;

    private Integer bookings;

    private Double revenue;

    public ServicePerformanceDTO(String serviceName, Integer bookings, Double revenue) {
        this.serviceName = serviceName;
        this.bookings = bookings;
        this.revenue = revenue;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getBookings() {
        return bookings;
    }

    public void setBookings(Integer bookings) {
        this.bookings = bookings;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
