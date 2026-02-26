package com.example.appointments_app.model.business;

import com.example.appointments_app.model.appointment.AppointmentDTO;
import com.example.appointments_app.model.data_aggregation.RevenueData;

import java.util.ArrayList;
import java.util.List;

public class BusinessSummary {
    private List<AppointmentDTO> appointmentsList;
    private List<RevenueData> revenueDataList;

    public BusinessSummary() {
        this.appointmentsList = new ArrayList<>();
        this.revenueDataList = new ArrayList<>();
    }

    public BusinessSummary(List<AppointmentDTO> appointmentsList, List<RevenueData> revenueDataList) {
        this.appointmentsList = appointmentsList;
        this.revenueDataList = revenueDataList;
    }

    public List<AppointmentDTO> getAppointmentsList() {
        return appointmentsList;
    }

    public void setAppointmentsList(List<AppointmentDTO> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    public List<RevenueData> getRevenueDataList() {
        return revenueDataList;
    }

    public void setRevenueDataList(List<RevenueData> revenueDataList) {
        this.revenueDataList = revenueDataList;
    }
}
