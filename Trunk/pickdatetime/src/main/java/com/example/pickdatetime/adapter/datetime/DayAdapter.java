package com.example.pickdatetime.adapter.datetime;

import androidx.annotation.NonNull;

import com.example.pickdatetime.bean.DateParams;
import com.example.pickdatetime.bean.DatePick;

import java.util.Calendar;

/**
 * Created by fhf11991 on 2017/8/29.
 */

public class DayAdapter extends DatePickAdapter {

    public DayAdapter(@NonNull DateParams dateParams, @NonNull DatePick datePick) {
        super(dateParams, datePick);
    }

    @Override
    public int getCurrentIndex() {
        return mData.indexOf(mDatePick.day);
    }

    @Override
    public void refreshValues() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mDatePick.year, mDatePick.month - 1, 1);

        int day = mDatePick.day;
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        mDatePick.day = Math.min(day, maxDay);

        setData(getArray(maxDay));
    }
}
