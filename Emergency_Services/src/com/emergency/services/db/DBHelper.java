package com.emergency.services.db;

import java.util.ArrayList;
import java.util.List;

import com.emergency.services.db.DBInfo.AlarmTable;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DBHelper {
    private Context mContext;

    public DBHelper(Context context) {
        mContext = context;
    }

    public void newAlarm(String name, int hour, int minute, int times, int interval, int repeatability, boolean vibrate, String ringtone) {
        ContentValues values = new ContentValues();
        values.put(AlarmTable.NAME, name);
        values.put(AlarmTable.IS_ENABLE, 1);
        values.put(AlarmTable.HOUR, hour);
        values.put(AlarmTable.MINUTE, minute);
        values.put(AlarmTable.TIMES, times);
        values.put(AlarmTable.INTERVAL, interval);
        values.put(AlarmTable.REPEATABILTTY, repeatability);
        values.put(AlarmTable.VIBRATE, repeatability);
        values.put(AlarmTable.RINGTONE, ringtone);

        Uri uri = mContext.getContentResolver().insert(AlarmTable.CONTENT_URI, values);
        ContentUris.parseId(uri);
    }

    public boolean deleteAlarm(long id) {
        String selection = AlarmTable._ID + "=" + id;
        int ret = mContext.getContentResolver().delete(AlarmTable.CONTENT_URI, selection, null);

        if (ret > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int modifyAlarm(long id, String name, int hour, int minute, int times, int interval, int repeatability, boolean vibrate, String ringtone) {
        ContentValues values = new ContentValues();
        values.put(AlarmTable.NAME, name);
        values.put(AlarmTable.HOUR, hour);
        values.put(AlarmTable.MINUTE, minute);
        values.put(AlarmTable.TIMES, times);
        values.put(AlarmTable.INTERVAL, interval);
        values.put(AlarmTable.REPEATABILTTY, repeatability);
        values.put(AlarmTable.VIBRATE, repeatability);
        values.put(AlarmTable.RINGTONE, ringtone);

        String selection = AlarmTable._ID + "=" + id;
        return mContext.getContentResolver().update(AlarmTable.CONTENT_URI, values, selection, null);
    }

    public int setAlarmEnable(long id, boolean enable) {
        ContentValues values = new ContentValues();
        values.put(AlarmTable.IS_ENABLE, enable ? 1 : 0);
        String selection = AlarmTable._ID + "=" + id;
        return mContext.getContentResolver().update(AlarmTable.CONTENT_URI, values, selection, null);
    }

    public AlarmInfo getAlarm(long id) {
        AlarmInfo info = null;
        String selection = AlarmTable._ID + "=" + id;
        Cursor cursor = mContext.getContentResolver().query(AlarmTable.CONTENT_URI, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                info = fillAlarmInfo(cursor);
            }
            cursor.close();
        }
        return info;
    }

    public List<AlarmInfo> getAlarmList(boolean onlyEnable) {
        String selection = null;
        if (onlyEnable) {
            selection = AlarmTable.IS_ENABLE + "=1";
        }

        List<AlarmInfo> alarmList = new ArrayList<AlarmInfo>();
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(AlarmTable.CONTENT_URI, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    AlarmInfo info = fillAlarmInfo(cursor);
                    alarmList.add(info);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return alarmList;
    }

    public AlarmInfo getNextAlarm() {
        List<AlarmInfo> alarmList = getAlarmList(true);
        long curr = System.currentTimeMillis();
        long diff = Long.MAX_VALUE;
        AlarmInfo nextAlarm = null;
        for (AlarmInfo info : alarmList) {
            if (info.nextAlarmDate == AlarmInfo.DEAD_ALARM) {
                continue;
            }

            long tmp = info.nextAlarmDate - curr;
            if (tmp < diff) {
                diff = tmp;
                nextAlarm = info;
            }
        }
        return nextAlarm;
    }

    private AlarmInfo fillAlarmInfo(Cursor cursor) {
        AlarmInfo info = new AlarmInfo();
        info.id = cursor.getLong(cursor.getColumnIndex(AlarmTable._ID));
        info.isEnable = (cursor.getInt(cursor.getColumnIndex(AlarmTable.IS_ENABLE)) == 1) ? true : false;
        info.name = cursor.getString(cursor.getColumnIndex(AlarmTable.NAME));
        info.hour = cursor.getInt(cursor.getColumnIndex(AlarmTable.HOUR));
        info.minute = cursor.getInt(cursor.getColumnIndex(AlarmTable.MINUTE));
        info.times = cursor.getInt(cursor.getColumnIndex(AlarmTable.TIMES));
        info.interval = cursor.getInt(cursor.getColumnIndex(AlarmTable.INTERVAL));
        info.repeatability = cursor.getInt(cursor.getColumnIndex(AlarmTable.REPEATABILTTY));
        info.vibrate = (cursor.getInt(cursor.getColumnIndex(AlarmTable.VIBRATE)) == 1) ? true : false;
        info.ringtone = cursor.getString(cursor.getColumnIndex(AlarmTable.RINGTONE));
        info.nextAlarmDate = info.getDateAndTime(false);
        return info;
    }
}
