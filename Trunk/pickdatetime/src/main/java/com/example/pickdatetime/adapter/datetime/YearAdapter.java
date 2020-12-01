package com.example.pickdatetime.adapter.datetime;

import androidx.annotation.NonNull;

import com.example.pickdatetime.bean.DateParams;
import com.example.pickdatetime.bean.DatePick;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by fhf11991 on 2017/8/29.
 */

public class YearAdapter extends DatePickAdapter {

    public YearAdapter(@NonNull DateParams dateParams, @NonNull DatePick datePick) {
        super(dateParams, datePick);
    }

    @Override
    public String getItem(int position) {
        int number = mData.get(position);
        String value = number < 10 ? ("0" + number) : "" + number;
        return value + "年";
    }

    @Override
    public int getCurrentIndex() {
        return mData.indexOf(mDatePick.year);
    }

    @Override
    public void refreshValues() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDateParams.startDate);
        int startYear = calendar.get(Calendar.YEAR);

        calendar.setTime(mDateParams.endDate);
        int endYear = calendar.get(Calendar.YEAR);

        ArrayList<Integer> values = new ArrayList<>();
        for (int i = startYear; i < endYear; i++) {
            values.add(i);
        }

        setData(values);
    }
}
