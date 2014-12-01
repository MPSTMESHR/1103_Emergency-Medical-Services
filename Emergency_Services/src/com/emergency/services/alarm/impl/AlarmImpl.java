package com.emergency.services.alarm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.emergency.services.alarm.NotifyActivity;
import com.emergency.services.db.AlarmInfo;
import com.emergency.services.db.DBHelper;

public class AlarmImpl {
    private static final int PLUS_TIME_STEP = 60 * 1000;

    private static AlarmImpl mSelf = null;
    private DBHelper mDBhelper = null;
    private Context mContext = null;
    private AlarmManager mAlarmMgr = null;
    private HashMap<Long, PendingIntent> mIntentMap = null;
    private List<AlarmInfo> mAlarmList = null;

    public synchronized static AlarmImpl getInstance() {
        if (mSelf == null) {
            mSelf = new AlarmImpl();
        }
        return mSelf;
    }

    private AlarmImpl() {
        mIntentMap = new HashMap<Long, PendingIntent>();
        mAlarmList = new ArrayList<AlarmInfo>();
    }

    public void setContext(Context context) {
        mContext = context;
        mDBhelper = new DBHelper(context);
        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void updatePendingIntent(long id, PendingIntent intent) {
        mIntentMap.remove(id);
        mIntentMap.put(id, intent);
    }

    public void updateAlarm(long id) {
        cancelAlarm(id);

        AlarmInfo info = getInfoById(id);
        if (info.nextAlarmDate != AlarmInfo.DEAD_ALARM) {
            PendingIntent intent = getPendingIntent(info, 0);
            mIntentMap.put(info.id, intent);
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, info.nextAlarmDate, intent);
        }
    }

    public void cancelAlarm(long id) {
        mAlarmMgr.cancel(mIntentMap.get(id));
        mIntentMap.remove(id);
    }

    public void resetAlarm(AlarmInfo info, int alarmTime) {
        long date = info.getDateAndTime(true);
        if (date == AlarmInfo.DEAD_ALARM) {
            return;
        }

        PendingIntent alarmIntent = AlarmImpl.getInstance().getPendingIntent(info, alarmTime + 1);
        AlarmImpl.getInstance().updatePendingIntent(info.id, alarmIntent);

        long plusTime = 0;
        switch (info.interval) {
        case AlarmInfo.FIVE_MINUTE:
            plusTime = 5 * PLUS_TIME_STEP;
            break;
        case AlarmInfo.TEN_MINUTE:
            plusTime = 10 * PLUS_TIME_STEP;
            break;
        case AlarmInfo.FIFTEEN_MINUTE:
            plusTime = 15 * PLUS_TIME_STEP;
            break;
        case AlarmInfo.HALF_AN_HOUR:
            plusTime = 30 * PLUS_TIME_STEP;
            break;
        default:
            break;
        }

        long time = date + plusTime * (alarmTime + 1);
        mAlarmMgr.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
    }

    public PendingIntent getPendingIntent(AlarmInfo info, int alarmTime) {
        Intent intent = new Intent(mContext, NotifyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Uri data = Uri.parse("alarm/" + info.id);
        intent.setData(data);
        intent.putExtra(NotifyActivity.ALARM_ID, info.id);
        intent.putExtra(NotifyActivity.ALARM_TIME, alarmTime);
        return PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public AlarmInfo getInfoById(long id) {
        for (int i = 0; i < mAlarmList.size(); i++) {
            if (mAlarmList.get(i).id == id) {
                AlarmInfo info = mAlarmList.get(i);
                info.nextAlarmDate = info.getDateAndTime(false);
                return info;
            }
        }
        return null;
    }

    public void updateAllAlarms() {
        mAlarmList = mDBhelper.getAlarmList(true);
        Iterator iter = mIntentMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            PendingIntent val = (PendingIntent) entry.getValue();
            mAlarmMgr.cancel(val);
        }
        mIntentMap.clear();

        for (int i = 0; i < mAlarmList.size(); i++) {
            AlarmInfo info = mAlarmList.get(i);
            info.nextAlarmDate = info.getDateAndTime(false);
            if (info.nextAlarmDate != AlarmInfo.DEAD_ALARM) {
                PendingIntent intent = getPendingIntent(info, 0);
                mIntentMap.put(info.id, intent);
                mAlarmMgr.set(AlarmManager.RTC_WAKEUP, info.nextAlarmDate, intent);
            }
        }
    }

    public void wakeScreen() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        WakeLock lock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                "jk");
        lock.acquire(5000);
    }

}
