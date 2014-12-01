package com.emergency.services.alarm.action;

import android.content.Context;
import android.os.Vibrator;

public class VibrationAction implements AlarmAction {

    private static long[] pattern = { 100, 200, 100, 200, 100, 200, 1000 };
    private Context mContext;
    private Vibrator mVibrator = null;

    public VibrationAction(Context context) {
        mContext = context;
        init();
    }

    public void init() {
        mVibrator = (Vibrator) mContext
                .getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void start() {
        mVibrator.vibrate(pattern, 0);
    }

    public void stop() {
        mVibrator.cancel();
    }

    public void release() {
    }

}
