package com.example.pickdatetime.adapter.datetime;


import androidx.annotation.NonNull;

import com.example.pickdatetime.bean.DateParams;
import com.example.pickdatetime.bean.DatePick;

/**
 * Created by fhf11991 on 2017/8/29.
 */

public class MinuteAdapter extends DatePickAdapter {

    public MinuteAdapter(@NonNull DateParams dateParams, @NonNull DatePick datePick) {
        super(dateParams, datePick);
    }

    @Override
    public int getCurrentIndex() {
        return mData.indexOf(mDatePick.minute);
    }

    @Override
    public void refreshValues() {
        setData(getArray(60));
    }
}
