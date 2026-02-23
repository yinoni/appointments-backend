package com.example.appointments_app.util;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HoursHelpClass {


    public List<Long> getOffsets(List<LocalTime> hours, int day_divide){
        List<Long> offsets = new ArrayList<>();

        for (LocalTime hour : hours) {
            long minutesFromMidnight = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, hour);
            long current_offset = minutesFromMidnight / day_divide;
            offsets.add(current_offset);
        }

        return offsets;
    }
}
