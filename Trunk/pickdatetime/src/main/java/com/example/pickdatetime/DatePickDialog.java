package com.example.pickdatetime;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.pickdatetime.bean.DateParams;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by fhf11991 on 2017/8/29.
 */
public class DatePickDialog extends Dialog {

    private TextView titleText;
    private TextView messageText;
    private TextView cancelText;
    private TextView sureText;
    private DateTimePickerView mDateTimePickerView;

    private Builder mBuilder;
    private final String pattern;

    private DatePickDialog(Context context, Builder builder) {
        super(context, R.style.dialog_style);
        mBuilder = builder;
        pattern = DateParams.getFormat(mBuilder.types);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbk_dialog_pick_time);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = outMetrics.widthPixels;
        getWindow().setAttributes(params);

        initView();
    }

    private void initView() {
        titleText = (TextView) findViewById(R.id.title);
        if(!TextUtils.isEmpty(mBuilder.title)) {
            titleText.setText(mBuilder.title);
        }

        messageText = (TextView) findViewById(R.id.message);

        mDateTimePickerView = (DateTimePickerView) findViewById(R.id.wheelLayout);
        mDateTimePickerView.setOnChangeListener(new OnChangeListener() {
            @Override
            public void onChanged(Date date) {
                setDate(date);
            }
        });
        DateParams dateParams = new DateParams();
        dateParams.types = mBuilder.types;
        dateParams.currentDate = mBuilder.currentDate;
        dateParams.startDate = mBuilder.startDate;
        dateParams.endDate = mBuilder.endDate;
        mDateTimePickerView.show(dateParams);

        cancelText = (TextView) findViewById(R.id.cancel);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        sureText = (TextView) findViewById(R.id.sure);
        sureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mBuilder.onSureListener != null) {
                    mBuilder.onSureListener.onSure(mDateTimePickerView.getSelectDate());
                }
            }
        });

        setDate(mBuilder.currentDate);
    }

    private void setDate(Date date) {
        String message = new SimpleDateFormat(pattern).format(date);
        messageText.setText(message);
    }

    public static class Builder {
        private int[] types;
        private String title;
        private Date currentDate;
        private Date startDate;
        private Date endDate;
        private OnSureListener onSureListener;

        public Builder setTypes(@DateParams.Type int... types) {
            this.types = types;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setCurrentDate(Date currentDate) {
            this.currentDate = currentDate;
            return this;
        }

        public Builder setStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setOnSureListener(OnSureListener onSureListener) {
            this.onSureListener = onSureListener;
            return this;
        }

        public void show(Context context) {
            DatePickDialog dialog = new DatePickDialog(context, this);
            dialog.show();
        }
    }
}
