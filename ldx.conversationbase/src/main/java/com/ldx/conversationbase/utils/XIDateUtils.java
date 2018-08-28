package com.ldx.conversationbase.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by v-doli1 on 2017/9/13.
 */

public class XIDateUtils {
    public static final String FORMAT_0 = "yyyy-MM-dd";

    public static final String FORMAT_1 = "HH:mm:ss";

    public static final String FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String getDateStr(Date date, String format) {
        SimpleDateFormat simpleDateFormat;
        return (simpleDateFormat = new SimpleDateFormat(format)).format(date);
    }


    public static String getDateStr(long longDate, String format) {
        SimpleDateFormat simpleDateFormat;
        return (simpleDateFormat = new SimpleDateFormat(format)).format(longDate);
    }

    public static String getNowString(String format) {
        return getDateStr(System.currentTimeMillis(), format);
    }

    public static String getNormalTime(long value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String time = format.format(new Date(value)) ;
        return time;
    }

    public static String getNormalHMTime(long value) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm") ;
        String time = format.format(new Date(value)) ;
        return time;
    }

    public static String getNormalYMDTime(long value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd") ;
        String time = format.format(new Date(value)) ;
        return time;
    }

    public static String getSpecitalUtcString(long timeStamp) {

        SimpleDateFormat formaterDate = new SimpleDateFormat("yyyy-MM-dd");
        formaterDate.setTimeZone(TimeZone.getTimeZone("GMT"));
       //  long localTime = timeStamp - TimeZone.getTimeZone("GMT+08").getRawOffset();
        long localTime = timeStamp ;
        Date curDate = new Date(localTime);
        String strDate = formaterDate.format(curDate);
        return strDate ;
    }

    public static String getSpecitalUtcSString(long timeStamp) {

        SimpleDateFormat formaterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formaterTime = new SimpleDateFormat("HH:mm:ss");

        long localTime = timeStamp - TimeZone.getDefault().getRawOffset();
        Date curDate = new Date(localTime);
        String strDate = formaterDate.format(curDate);
        String strTime = formaterTime.format(curDate);
        return strDate + strTime;
    }

    public static String getUtcString(long timeStamp) {

        SimpleDateFormat formaterDate = new SimpleDateFormat("yyyy-MM-dd");
        long localTime = timeStamp - TimeZone.getDefault().getRawOffset();
        Date curDate = new Date(localTime);
        String strDate = formaterDate.format(curDate);
        return strDate ;
    }

    public static String getUtcSString(long timeStamp) {

        SimpleDateFormat formaterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formaterTime = new SimpleDateFormat("HH:mm:ss");

        long localTime = timeStamp - TimeZone.getDefault().getRawOffset();
        Date curDate = new Date(localTime);
        String strDate = formaterDate.format(curDate);
        String strTime = formaterTime.format(curDate);
        return strDate + strTime;
    }

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
            final long interval = ms1 - ms2;
            return interval < MILLIS_IN_DAY
                    && interval > -1L * MILLIS_IN_DAY
                    && toDay(ms1) == toDay(ms2);
        }
        private static long toDay(long millis) {
            return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
        }

    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    public static long getLocalTimestamp() {
        Date curDate=new Date(System.currentTimeMillis());
        long timestamp=curDate.getTime();
        return timestamp;
    }


    public static long getCurrentTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();
        return timestamp;
    }

    public static long getCurrentDate() {
        long timeStamp = getCurrentTimeStamp();
        timeStamp=timeStamp + TimeZone.getDefault().getRawOffset();
        timeStamp = timeStamp / 1000;
        //convert to an interger day number
        timeStamp = timeStamp / (3600 * 24);
        return timeStamp;
    }

    private static String getWeek(long timeStamp) {
        int mydate = 0;
        String week = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(timeStamp));
        mydate = cd.get(Calendar.DAY_OF_WEEK);
        if (mydate == 1) {
            week = "周日";
        } else if (mydate == 2) {
            week = "周一";
        } else if (mydate == 3) {
            week = "周二";
        } else if (mydate == 4) {
            week = "周三";
        } else if (mydate == 5) {
            week = "周四";
        } else if (mydate == 6) {
            week = "周五";
        } else if (mydate == 7) {
            week = "周六";
        }
        return week;
    }

    public static String times(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM月dd日  #  HH:mm");
        return sdr.format(new Date(timeStamp)).replaceAll("#",
                getWeek(timeStamp));

    }

    public static long getTimeStampFromUtcNow(String nowStr) {
        Date curDate = new Date(System.currentTimeMillis());
        Pattern p = Pattern.compile("([0-9]*)-([0-9]*)-([0-9]*)T([0-9]*):([0-9]*):([0-9]*).[0-9]*Z");
        Matcher m = p.matcher(nowStr);
        if (m.find()) {
            String year = m.group(1);
            String month = m.group(2);
            String day = m.group(3);
            String hour = m.group(4);
            String min = m.group(5);
            String sec = m.group(6);
            curDate = new Date(Integer.parseInt(year) - 1900,
                    Integer.parseInt(month) - 1,
                    Integer.parseInt(day),
                    Integer.parseInt(hour),
                    Integer.parseInt(min),
                    Integer.parseInt(sec));
        }
        return curDate.getTime() + TimeZone.getDefault().getRawOffset();
    }

    public static long getDate(String str) {
        SimpleDateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = formater.parse(str);

            return getDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static long getDateStamp(String str, String format, int offset) {
        SimpleDateFormat formater = new SimpleDateFormat(format, Locale.ENGLISH);
        //formater.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date date = formater.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, offset);
            date = calendar.getTime();
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static long getDateFromString(String str){
        if(str == null || str.isEmpty()){
            return 0L;
        }
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = formater.parse(str);
            return getDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static long getDate(Date date) {
        long timeStamp = date.getTime();
        timeStamp = timeStamp + TimeZone.getDefault().getRawOffset();
        timeStamp = timeStamp / 1000;
        //convert to an interger day number
        timeStamp = timeStamp / (3600 * 24);
        return timeStamp;
    }

    /**
     * get today 00:00 timeStamp
     * @return
     */
    public static long getToday00TimeStamp(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * default timezone with spectical timezone
     * @param timeZoneId
     * @return
     */
    private static int getDiffTimeZoneRawOffset(String timeZoneId) {
        return TimeZone.getDefault().getRawOffset()
                - TimeZone.getTimeZone(timeZoneId).getRawOffset();
    }

    /**
     *
     * @return default timezone ,UTC
     */
    private static int getDefaultTimeZoneRawOffset() {
        return TimeZone.getDefault().getRawOffset();
    }

}
