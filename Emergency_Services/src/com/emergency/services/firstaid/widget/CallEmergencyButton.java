package com.emergency.services.firstaid.widget;

import com.emergency.services.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CallEmergencyButton extends Button implements OnClickListener {

    Context mContext;

    public CallEmergencyButton(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	mContext = context;
	init();
    }

    public CallEmergencyButton(Context context, AttributeSet attrs) {
	super(context, attrs);
	mContext = context;
	init();
    }

    private void init() {
	setOnClickListener(this);
	setText(mContext.getString(R.string.call) + " " + mContext.getString(R.string.emergency_phone_number));
    }

    public void onClick(View v) {
	// TODO: make call

	Intent intent = new Intent(Intent.ACTION_CALL);

	intent.setData(Uri.parse("tel:" + mContext.getString(R.string.emergency_phone_number)));
	mContext.startActivity(intent);
    }
}
