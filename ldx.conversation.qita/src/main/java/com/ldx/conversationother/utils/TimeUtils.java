package com.ldx.conversationother.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by v-doli1 on 2018/3/29.
 */

public class TimeUtils {

    public static final String YMDHMSFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DHMSFORMAT = "dd";
    public static final String HFORMAT = "HH";

    public String getTime(){
        long time=System.currentTimeMillis()/1000;
        String  str=String.valueOf(time);
        return str;
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(YMDHMSFORMAT);
        return sdf.format(new Date());
    }

    public static String getCurrentStringStamp() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(YMDHMSFORMAT);
        String dateStr = sdf.format(calendar.getTime());
        return dateStr;
    }

    public static Calendar getCalendar(String strdata){
        try {
            SimpleDateFormat sdf= new SimpleDateFormat(YMDHMSFORMAT);
            Date date =sdf.parse(strdata);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatTime(long timestamp, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(timestamp));
    }

}
