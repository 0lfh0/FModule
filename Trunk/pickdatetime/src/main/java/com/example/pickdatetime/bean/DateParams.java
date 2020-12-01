package com.example.pickdatetime.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by fhf11991 on 2017/8/29.
 */

public class DateParams {
    public int[] types;
    public Date currentDate;
    public Date startDate;
    public Date endDate;

    public DateParams(@Type int... style) {
        this.types = style;
    }

    @IntDef({TYPE_YEAR, TYPE_MONTH, TYPE_DAY, TYPE_HOUR, TYPE_MINUTE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    public final static int TYPE_YEAR = 1;    // 2 x 0
    public final static int TYPE_MONTH = 2;   // 2 x 1
    public final static int TYPE_DAY = 4;     // 2 x 2
    public final static int TYPE_HOUR = 8;    // 2 x 3
    public final static int TYPE_MINUTE = 16; // 2 x 4

    public static String getFormat(int[] types) {
        if(types == null) {
            return null;
        }

        int total = 0;
        for(int type : types) {
            total = total + type;
        }

        StringBuffer format = new StringBuffer();

        // year
        boolean hasYear = (total & TYPE_YEAR) == TYPE_YEAR;
        if(hasYear) {
            format.append("yyyy");
        }

        // month
        boolean hasMonth = (total & TYPE_MONTH) == TYPE_MONTH;
        if(hasMonth) {
            format.append(hasYear ? "-" : "");
            format.append("MM");
        }

        // day
        boolean hasDay = (total & TYPE_DAY) == TYPE_DAY;
        if(hasDay) {
            format.append(hasMonth ? "-" : "");
            format.append("dd");
        }

        // hour
        boolean hasHour = (total & TYPE_HOUR) == TYPE_HOUR;
        if(hasHour) {
            format.append(" HH");
        }

        // minute
        if((total & TYPE_MINUTE) == TYPE_MINUTE) {
            format.append(hasHour ? ":" : "");
            format.append("mm");
        }

        return format.toString();
    }

}
