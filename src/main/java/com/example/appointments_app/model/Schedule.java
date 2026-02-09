package com.example.appointments_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "schedules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "business_date", // שם האילוץ (לבחירתך)
                        columnNames = {"business_id", "date"} // העמודות שחייבות להיות ייחודיות יחד
                )
        },
        indexes = {
                @Index(name = "idx_business_date", columnList = "business_id, date", unique = true)
        })
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @OneToMany(mappedBy = "schedule",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @Column(nullable = false)
    @Min(5)
    private Integer min_duration;

    @Column(nullable = false)
    private LocalTime start_time;

    @Column(nullable = false)
    private LocalTime end_time;

    @Column(nullable = false)
    private List<LocalTime> available_hours;


    public Schedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Integer getMin_duration() {
        return min_duration;
    }

    public void setMin_duration(Integer min_duration) {
        this.min_duration = min_duration;
    }

    public LocalTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalTime start_time) {
        this.start_time = start_time;
    }

    public LocalTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalTime end_time) {
        this.end_time = end_time;
    }

    public List<LocalTime> getAvailable_hours() {
        return available_hours;
    }

    public void setAvailable_hours(List<LocalTime> available_hours) {
        this.available_hours = available_hours;
    }



    public ScheduleDTO convertToDTO(){
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(id);
        scheduleDTO.setDate(date);
        scheduleDTO.setBusiness(business.getId());
        //scheduleDTO.setAppointments(appointments.stream().map(Appointment::convertToDTO).toList());
        scheduleDTO.setAvailable_hours(available_hours);

        return scheduleDTO;
    }


}
