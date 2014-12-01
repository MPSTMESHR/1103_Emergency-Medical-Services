package com.emergency.services.firstaid;

import com.emergency.services.R;
import com.emergency.services.R.id;
import com.emergency.services.R.layout;
import com.emergency.services.R.string;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

public class ReanimationActivity extends Activity {

    private int count = 0;
    private TextView counterTextView, instructionTextView;
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_reanimation);

	instructionTextView = (TextView) findViewById(R.id.instructions);

	FirstAidApp.getTTS().speak(getString(R.string.reanimation_1), TextToSpeech.QUEUE_FLUSH, null);

	counterTextView = (TextView) findViewById(R.id.counterText);

	final Button button = (Button) findViewById(R.id.startStopButton);
	button.setOnClickListener(new View.OnClickListener() {

	    public void onClick(View v) {
		running = !running;

		button.setText(running ? R.string.stop : R.string.start);

		if (running) {
		    Thread counter = new Thread(new Counter());
		    counter.start();
		} else {
		    counterTextView.setText("");
		}
	    }
	});
    }

    class Counter extends Thread {

	public void run() {

	    try {

		while (true) {
		    if (!running)
			break;
		    count = 0;

		    while (true) {
			if (!running)
			    break;

			try {
			    count++;
			    FirstAidApp.getTTS().speak("raz", TextToSpeech.QUEUE_FLUSH, null);

			    runOnUiThread(new Runnable() {
				public void run() {
				    counterTextView.setText(Integer.toString(count));
				}
			    });

			    Thread.sleep(600);

			    if (count == 30)
				break;

			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }

		    if (!running)
			break;

		    count = 0;

		    FirstAidApp.getTTS().speak(getString(R.string.reanimation_2), TextToSpeech.QUEUE_FLUSH, null);

		    runOnUiThread(new Runnable() {
			public void run() {
			    instructionTextView.setText(getString(R.string.reanimation_2));
			}
		    });

		    Thread.sleep(2000);

		    while (true) {
			if (!running)
			    break;

			try {
			    count++;
			    FirstAidApp.getTTS().speak(Integer.toString(count), TextToSpeech.QUEUE_FLUSH, null);

			    runOnUiThread(new Runnable() {
				public void run() {
				    counterTextView.setText(Integer.toString(count));
				}
			    });

			    Thread.sleep(1200);

			    if (count == 2)
				break;

			    Thread.sleep(1800);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }

		    FirstAidApp.getTTS().speak(getString(R.string.reanimation_3), TextToSpeech.QUEUE_FLUSH, null);

		    runOnUiThread(new Runnable() {
			public void run() {
			    instructionTextView.setText(getString(R.string.reanimation_3));
			}
		    });

		    Thread.sleep(4000);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
