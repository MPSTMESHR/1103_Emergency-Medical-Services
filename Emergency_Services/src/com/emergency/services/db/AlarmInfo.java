package com.emergency.services.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AlarmInfo {
    /** for alarm times */
    public static final int ONCE = 0;
    public static final int TWICE = 1;
    public static final int THRICE = 2;

    /** for alarm interval */
    public static final int FIVE_MINUTE = 0;
    public static final int TEN_MINUTE = 1;
    public static final int FIFTEEN_MINUTE = 2;
    public static final int HALF_AN_HOUR = 3;

    /** for alarm repeatability */
    public static final int NO_REPEAT = 0;
    public static final int WEEKDAY_REPEAT = 1;
    public static final int SATURDAY_REPEAT = 2;
    public static final int SUNDAY_REPEAT = 4;
    public static final int EVERYDAY_REPEAT = 7;

    public static final int DEAD_ALARM = -1;

    public long id;
    public String name;
    public boolean isEnable;
    public int hour;
    public int minute;
    public int times = ONCE;
    public int interval = FIVE_MINUTE;
    public int repeatability = NO_REPEAT;
    public boolean vibrate;
    public String ringtone;
    public long nextAlarmDate;

    public long getDateAndTime(boolean isReAlarm) {
        Calendar calendar = null;
        if (isReAlarm) {
            calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTimeInMillis(System.currentTimeMillis());
        } else {
            calendar = getAlarmDate();
            if (calendar == null) {
                return DEAD_ALARM;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(calendar.getTime());
        try {
            calendar.setTime(new SimpleDateFormat("yyyyMMdd").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long time = calendar.getTimeInMillis();
        time += (hour * 60 + minute) * 60 * 1000;
        return time;
    }

    public Calendar getAlarmDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (needAlarmToday()) {
            return calendar;
        }

        if (repeatability == NO_REPEAT) {
            return null;
        } else {
            // plus one day every time till it matches the repeatability
            int daysOfWeek = 7;
            while (daysOfWeek-- >= 0) {
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                boolean contains = RepeatabilityHelper.matches(repeatability, week);

                if (contains) {
                    break;
                }
            }
            return calendar;
        }
    }

    private boolean needAlarmToday() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currMin = calendar.get(Calendar.MINUTE) + (currHour * 60);
        int min = minute + (hour * 60);
        return currMin < min ? true : false;
    }
}
