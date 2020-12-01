package com.example.pickdatetime.bean;

import java.util.Calendar;

/**
 * Created by fhf11991 on 2017/8/29.
 */

public class DatePick {

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    public void setData(Calendar calendar) {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }
}
