package com.example.appointments_app.model.ScreensDTO;

import com.example.appointments_app.model.appointment.AppointmentDTO;
import com.example.appointments_app.model.business.BusinessDTO;
import com.example.appointments_app.model.business.BusinessSummary;
import com.example.appointments_app.model.data_aggregation.RevenueData;

import java.util.List;

public class HomeDTO {
    private List<BusinessDTO> businesses;
    private BusinessSummary businessSummary;

    public HomeDTO(List<BusinessDTO> businesses,
                   BusinessSummary businessSummary) {
        this.businesses = businesses;
        this.businessSummary = businessSummary;
    }

    public List<BusinessDTO> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<BusinessDTO> businesses) {
        this.businesses = businesses;
    }

    public BusinessSummary getBusinessSummary() {
        return businessSummary;
    }

    public void setBusinessSummary(BusinessSummary businessSummary) {
        this.businessSummary = businessSummary;
    }
}
