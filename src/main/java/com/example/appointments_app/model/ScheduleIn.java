package com.example.appointments_app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.appointments_app.model.ScheduleBuilder.aSchedule;

public class ScheduleIn {

    @JsonFormat(pattern = "yyyy-MM-dd")

    private LocalDate date;

    @Min(1)
    @Max(22)
    private Integer startTime;

    @Min(2)
    @Max(23)
    private Integer endTime;

    @Min(1)
    @Max(60)
    private Integer min_duration;

    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) return true;
        return endTime > startTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getMin_duration() {
        return min_duration;
    }

    public void setMin_duration(Integer min_duration) {
        this.min_duration = min_duration;
    }

    public Schedule toSchedule(){
        LocalTime startTime = LocalTime.of(this.startTime, 0);
        LocalTime endTime = LocalTime.of(this.endTime, 0);
        List<LocalTime> available_hours = getAvailable_hours();
        return aSchedule()
                .withDate(this.date)
                .withStart_time(startTime)
                .withEnd_time(endTime)
                .withMin_duration(this.min_duration)
                .withAvailable_hours(available_hours)
                .build();
    }

    public List<LocalTime> getAvailable_hours() {
        List<LocalTime> available_hours = new ArrayList<>();

        for(int i = this.startTime; i < this.endTime; i++){
            for(int j = 0; j < 60; j+= this.min_duration){
                available_hours.add(LocalTime.of(i, j));
            }
        }
        return available_hours;
    }

}
