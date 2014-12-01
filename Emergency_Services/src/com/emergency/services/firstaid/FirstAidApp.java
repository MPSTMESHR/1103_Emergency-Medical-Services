package com.emergency.services.firstaid;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class FirstAidApp extends Application implements OnInitListener {

    private static TextToSpeech mTTS;

    @Override
    public void onCreate() {
	super.onCreate();
	
	mTTS = new TextToSpeech(this, this);
	//TODO: check for available TTS data
    }

    public void onInit(int status) {
	// TODO Auto-generated method stub
    }

    public static TextToSpeech getTTS() {
	return mTTS;
    }
}
