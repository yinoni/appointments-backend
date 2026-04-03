package com.example.appointments_app.model.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.appointments_app.model.schedule.ScheduleBuilder.aSchedule;

public class ScheduleIn {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Min(1)
    @Max(60)
    private Integer min_duration;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMin_duration() {
        return min_duration;
    }

    public void setMin_duration(Integer min_duration) {
        this.min_duration = min_duration;
    }

    public Schedule toSchedule(){
        return aSchedule()
                .withDate(this.date)
                .withStart_time(this.startTime)
                .withEnd_time(this.endTime)
                .withMin_duration(this.min_duration)
                .build();
    }

    /*
    public List<LocalTime> getAvailable_hours() {
        List<LocalTime> available_hours = new ArrayList<>();

        for(int i = this.startTime; i < this.endTime; i++){
            for(int j = 0; j < 60; j+= this.min_duration){
                available_hours.add(LocalTime.of(i, j));
            }
        }
        available_hours.add(LocalTime.of(this.endTime, 0));
        return available_hours;
    }
    */

}
