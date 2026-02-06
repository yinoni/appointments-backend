package com.example.appointments_app.util;

import com.example.appointments_app.exception.DateFormatException;

import java.time.LocalDate;

public class DateUtils {

    public static LocalDate getDateLocalDate(String date) {
        String[] splitted = date.split("-");
        int year, month, day;

        if(splitted.length != 3)
            throw new DateFormatException("The format of the date is not allowed! please use this format: YYYY-MM-DD");

        year = Integer.parseInt(splitted[0]);
        month = Integer.parseInt(splitted[1]);
        day = Integer.parseInt(splitted[2]);

        if(year < 0 || month < 0 || month > 12 || day < 0 || day > 31)
            throw new DateFormatException("The date parameters are illegal! please don't use negative numbers");

        return LocalDate.of(year, month, day);

    }
}
