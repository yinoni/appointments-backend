package com.example.appointments_app.model.ScreensDTO;

import com.example.appointments_app.model.AppointmentDTO;
import com.example.appointments_app.model.BusinessDTO;
import com.example.appointments_app.model.data_aggregation.RevenueData;

import java.util.List;

public class HomeDTO {
    private List<BusinessDTO> businesses;
    private List<AppointmentDTO> today_appointments;
    private List<RevenueData> revenueDataList;

    public HomeDTO(List<BusinessDTO> businesses,
                   List<AppointmentDTO> today_appointments,
                   List<RevenueData> revenueDataList) {
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

    public List<RevenueData> getRevenueDataList() {
        return revenueDataList;
    }

    public void setRevenueDataList(List<RevenueData> revenueDataList) {
        this.revenueDataList = revenueDataList;
    }
}
