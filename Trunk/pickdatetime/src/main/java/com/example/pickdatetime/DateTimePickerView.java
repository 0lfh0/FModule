package com.example.pickdatetime;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickdatetime.adapter.datetime.DatePickAdapter;
import com.example.pickdatetime.adapter.datetime.DayAdapter;
import com.example.pickdatetime.adapter.datetime.HourAdapter;
import com.example.pickdatetime.adapter.datetime.MinuteAdapter;
import com.example.pickdatetime.adapter.datetime.MonthAdapter;
import com.example.pickdatetime.adapter.datetime.YearAdapter;
import com.example.pickdatetime.bean.DateParams;
import com.example.pickdatetime.bean.DatePick;
import com.example.pickdatetime.view.OnWheelChangedListener;
import com.example.pickdatetime.view.WheelView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fhf11991 on 2017/8/29.
 */
public class DateTimePickerView extends LinearLayout {

    private static final String TAG = "WheelPicker";

    final DatePick mDatePick = new DatePick();
    private OnChangeListener mOnChangeListener;
    private WheelView mDayView;
    private DatePickAdapter mDayAdapter;

    public DateTimePickerView(Context context) {
        super(context);
    }

    public DateTimePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    public void show(@NonNull DateParams params) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(params.currentDate);
        mDatePick.setData(calendar);

        if(params.types == null) {
            return;
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        for(int type : params.types) {

            WheelView wheelView = new WheelView(getContext());
            final DatePickAdapter datePickAdapter;

            switch (type) {
                case DateParams.TYPE_YEAR:
                    wheelView.setCyclic(false);
                    wheelView.setAdapter(datePickAdapter = new YearAdapter(params, mDatePick));
                    wheelView.setCurrentItem(datePickAdapter.getCurrentIndex());
                    layoutParams.weight = 3;
                    wheelView.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            mDatePick.year = datePickAdapter.getValue(newValue);
                            mDayAdapter.refreshValues();
                            mDayView.setCurrentItem(mDayAdapter.getCurrentIndex());
                            DateTimePickerView.this.onChanged();
                        }
                    });
                    break;

                case DateParams.TYPE_MONTH:
                    wheelView.setCyclic(true);
                    wheelView.setAdapter(datePickAdapter = new MonthAdapter(params, mDatePick));
                    wheelView.setCurrentItem(datePickAdapter.getCurrentIndex());
                    layoutParams.weight = 2;
                    wheelView.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            mDatePick.month = datePickAdapter.getValue(newValue);
                            mDayAdapter.refreshValues();
                            mDayView.setCurrentItem(mDayAdapter.getCurrentIndex());
                            DateTimePickerView.this.onChanged();
                        }
                    });
                    break;

                case DateParams.TYPE_DAY:
                    mDayView = wheelView;
                    datePickAdapter = new DayAdapter(params, mDatePick);
                    mDayAdapter = datePickAdapter;

                    wheelView.setCyclic(true);
                    wheelView.setAdapter(datePickAdapter);
                    wheelView.setCurrentItem(datePickAdapter.getCurrentIndex());
                    layoutParams.weight = 2;
                    wheelView.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            mDatePick.day = datePickAdapter.getValue(newValue);
                            DateTimePickerView.this.onChanged();
                        }
                    });
                    break;

                case DateParams.TYPE_HOUR:
                    wheelView.setCyclic(true);
                    wheelView.setAdapter(datePickAdapter = new HourAdapter(params, mDatePick));
                    wheelView.setCurrentItem(datePickAdapter.getCurrentIndex());
                    wheelView.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            mDatePick.hour = datePickAdapter.getValue(newValue);
                            DateTimePickerView.this.onChanged();
                        }
                    });
                    layoutParams.weight = 2;
                    break;

                case DateParams.TYPE_MINUTE:
                    wheelView.setCyclic(true);
                    wheelView.setAdapter(datePickAdapter = new MinuteAdapter(params, mDatePick));
                    wheelView.setCurrentItem(datePickAdapter.getCurrentIndex());
                    wheelView.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            mDatePick.minute = datePickAdapter.getValue(newValue);
                            DateTimePickerView.this.onChanged();
                        }
                    });
                    layoutParams.weight = 2;
                    break;
            }

            addView(wheelView, layoutParams);

            if(type == DateParams.TYPE_HOUR) {
                layoutParams.weight = 0;
                TextView textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                TextPaint paint = textView.getPaint();
                paint.setFakeBoldText(true);
                textView.setText(":");
                textView.setTextColor(0xff444444);
                addView(textView, layoutParams);
            }
        }
    }

    private void onChanged() {
        if(mOnChangeListener != null) {
            mOnChangeListener.onChanged(getSelectDate());
        }
    }

    public Date getSelectDate() {
        int year = mDatePick.year;
        int moth = mDatePick.month;
        int day = mDatePick.day;
        int hour = mDatePick.hour;
        int minute = mDatePick.minute;

        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, moth - 1, 1);
//        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        if(day > maxDay) { // the max day of every month is not the same !!!
//            day = 1;
//            mDatePick.day = 1;
//            mDayView.setCurrentItem(mDayAdapter.getCurrentIndex());
//        }
        calendar.set(year, moth - 1, day, hour, minute);
        return calendar.getTime();
    }
}
