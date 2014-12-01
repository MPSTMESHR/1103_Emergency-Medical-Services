package com.emergency.services.alarm.action;

import android.media.MediaPlayer;

public class SoundAction implements AlarmAction {
    private MediaPlayer mMediaPlayer = null;
    private String mRingtone;

    public SoundAction(String ringtone) {
        mRingtone = ringtone;
        init();
    }

    public void init() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(true);
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mRingtone);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
