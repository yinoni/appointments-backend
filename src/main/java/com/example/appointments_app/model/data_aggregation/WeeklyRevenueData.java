package com.example.appointments_app.model.data_aggregation;

public class WeeklyRevenueData {
    private String date;
    private Double sum;

    public WeeklyRevenueData() {
    }

    public WeeklyRevenueData(String date, Double sum) {
        this.date = date;
        this.sum = sum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
