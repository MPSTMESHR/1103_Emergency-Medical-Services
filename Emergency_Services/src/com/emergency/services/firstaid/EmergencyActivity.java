package com.emergency.services.firstaid;

import com.emergency.services.R.id;
import com.emergency.services.R.string;
import com.emergency.services.firstaid.widget.CountdownButton;
import com.emergency.services.firstaid.widget.CountdownButton.OnCountdownFinishedListener;

import com.emergency.services.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class EmergencyActivity extends Activity implements OnCountdownFinishedListener, OnClickListener {

    public static final String EMERGENCY_NAME = "emergencyName";
    public static final String EMERGENCY_LAYOUT_ID = "layoutId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	
	Bundle extras = getIntent().getExtras();
	if (extras != null) {
	    setTitle(extras.getString(EMERGENCY_NAME));
	    final int layoutId = extras.getInt(EMERGENCY_LAYOUT_ID);
	    setContentView(layoutId);
	}

	Button reanimationButton = (Button) findViewById(R.id.reanimationButton);
	if (reanimationButton != null) {
	    reanimationButton.setOnClickListener(this);
	}
	CountdownButton timer = (CountdownButton) findViewById(R.id.countdown);
	if (timer != null) {
	    timer.setOnCountdownFinishedListener(this);
	}
    }

    /**
     * Called when text is pressed. Reads out loud instructions for current
     * step. Please notice that this callback is set up in xml.
     * 
     * @param v
     */
    public void readOutLoud(View v) {
	String text = ((TextView) v).getText().toString();
	FirstAidApp.getTTS().speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onCountdownFinished() {
	FirstAidApp.getTTS().speak(getString(R.string.countdown_finished), TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onClick(View v) {
	if (v.getId() == R.id.reanimationButton) {
	    startActivity(new Intent(this, ReanimationActivity.class));
	}
	// TODO Auto-generated method stub
	
    }
}
