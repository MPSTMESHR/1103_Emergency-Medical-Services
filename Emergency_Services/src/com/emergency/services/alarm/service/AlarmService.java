package com.emergency.services.alarm.service;

import com.emergency.services.alarm.impl.AlarmImpl;
import com.emergency.services.db.DBInfo.AlarmTable;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class AlarmService extends Service {

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // TODO update changed alarms only to improve performance
            AlarmImpl.getInstance().updateAllAlarms();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getContentResolver().registerContentObserver(AlarmTable.CONTENT_URI,
                false, mObserver);
        AlarmImpl.getInstance().setContext(getApplication());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmImpl.getInstance().updateAllAlarms();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }
}
