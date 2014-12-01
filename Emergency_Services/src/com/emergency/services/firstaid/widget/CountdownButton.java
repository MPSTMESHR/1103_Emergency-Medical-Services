package com.emergency.services.firstaid.widget;

import com.emergency.services.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class CountdownButton extends FrameLayout implements OnClickListener {

    public interface OnCountdownFinishedListener {
	public void onCountdownFinished();
    }

    private int mCountdownDurationSec;
    private int mRemainingDurationSec;
    private boolean mTimerRunning = false;
    private Button mButton;
    private Context mContext;
    private OnCountdownFinishedListener listener;

    public void setOnCountdownFinishedListener(OnCountdownFinishedListener listener) {
	this.listener = listener;
    }

    Handler mHandler = new Handler();
    Runnable mUpdateTimeTask = new Runnable() {

	public void run() {
	    if (mRemainingDurationSec - 1 < 0) {
		mButton.setText(mContext.getString(R.string.countdown_finished));
		if (listener != null) {
		    listener.onCountdownFinished();
		}
		mHandler.removeCallbacks(mUpdateTimeTask);
		mRemainingDurationSec = mCountdownDurationSec;
		mTimerRunning = false;
		return;
	    }
	    mRemainingDurationSec--;

	    String timeLeft = getDurationString(mRemainingDurationSec);
	    mButton.setText(String.format(mContext.getString(R.string.time_left), timeLeft));

	    mHandler.postDelayed(mUpdateTimeTask, 1000);
	}
    };

    public CountdownButton(Context context, AttributeSet attrs) {
	super(context, attrs);
	mContext = context;
	init(attrs);

	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	inflater.inflate(R.layout.view_countdown_timer, this, true);

	mButton = (Button) findViewById(R.id.cdb_countdownButton);
	mButton.setOnClickListener(this);
	mButton.setText(String.format(context.getString(R.string.start_timer), getDurationString(mCountdownDurationSec)));
    }

    private String getDurationString(int durationSec) {
	int mins = durationSec / 60;
	int secs = durationSec % 60;
	return String.format("%02d:%02d", mins, secs);
    }

    private void init(AttributeSet attrs) {
	TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownTimer);

	mCountdownDurationSec = a.getInt(R.styleable.CountDownTimer_durationSec, 600);
	mRemainingDurationSec = mCountdownDurationSec;
	a.recycle();
    }

    public void onClick(View v) {
	final int id = v.getId();
	if (id == R.id.cdb_countdownButton) {
	    mTimerRunning = !mTimerRunning;
	    if (mTimerRunning) {
		mHandler.postDelayed(mUpdateTimeTask, 1000);
	    } else {
		mHandler.removeCallbacks(mUpdateTimeTask);
	    }
	}

    }
}
