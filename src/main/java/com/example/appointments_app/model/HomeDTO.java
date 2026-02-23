package com.example.appointments_app.model;

import com.example.appointments_app.model.data_aggregation.WeeklyRevenueData;

import java.util.List;

public class HomeDTO {
    private List<BusinessDTO> businesses;
    private List<AppointmentDTO> today_appointments;
    private List<WeeklyRevenueData> revenueDataList;

    public HomeDTO(List<BusinessDTO> businesses,
                   List<AppointmentDTO> today_appointments,
                   List<WeeklyRevenueData> revenueDataList) {
        this.businesses = businesses;
        this.today_appointments = today_appointments;
        this.revenueDataList = revenueDataList;
    }

    public List<BusinessDTO> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<BusinessDTO> businesses) {
        this.businesses = businesses;
    }

    public List<AppointmentDTO> getToday_appointments() {
        return today_appointments;
    }

    public void setToday_appointments(List<AppointmentDTO> today_appointments) {
        this.today_appointments = today_appointments;
    }

    public List<WeeklyRevenueData> getRevenueDataList() {
        return revenueDataList;
    }

    public void setRevenueDataList(List<WeeklyRevenueData> revenueDataList) {
        this.revenueDataList = revenueDataList;
    }
}
