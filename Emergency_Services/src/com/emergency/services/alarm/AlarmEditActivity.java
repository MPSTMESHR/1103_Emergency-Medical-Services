package com.emergency.services.alarm;

import java.util.Calendar;
import java.util.TimeZone;

import com.emergency.services.R;
import com.emergency.services.alarm.impl.PreferencesUtil;
import com.emergency.services.db.AlarmInfo;
import com.emergency.services.db.DBHelper;
import com.emergency.services.db.RepeatabilityHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AlarmEditActivity extends Activity {
    public static final String ALARM_ID = "AlarmId";
    public static final int NO_ALARM_ID = -1;

    private DBHelper mDBHelper;
    private TimePicker mTimePicker;
    private EditText mEditName;
    private Spinner mTimes;
    private Spinner mInterval;
    private TextView mRepeatability;
    private TextView mRingtone;
    private Button mOkButton;
    private Button mCancelButton;

    private boolean[] mSelectedItems;
    private AlarmInfo mInfo = null;
    private long mAlarmId = NO_ALARM_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        mAlarmId = getIntent().getLongExtra(ALARM_ID, NO_ALARM_ID);
        mDBHelper = new DBHelper(this);
        initView();
        initSettings();
    }

    private void initView() {
        mOkButton = (Button) findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String name = mEditName.getText().toString().trim();
                String ringtone = mRingtone.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AlarmEditActivity.this, R.string.pls_enter_name, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(ringtone)) {
                    Toast.makeText(AlarmEditActivity.this, R.string.pls_enter_ringtone, Toast.LENGTH_SHORT).show();
                } else {
                    if (mAlarmId == NO_ALARM_ID) {
                        mDBHelper.newAlarm(name, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(), mTimes.getSelectedItemPosition(),
                                mInterval.getSelectedItemPosition(), mInfo.repeatability, true, mInfo.ringtone);
                    } else {
                        mDBHelper.modifyAlarm(mAlarmId, name, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(),
                                mTimes.getSelectedItemPosition(), mInterval.getSelectedItemPosition(), mInfo.repeatability, true, mInfo.ringtone);
                    }
                    finish();
                }
            }
        });

        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mEditName = (EditText) findViewById(R.id.name);
        mTimes = (Spinner) findViewById(R.id.times);
        mInterval = (Spinner) findViewById(R.id.interval);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        // mTimePicker.setIs24HourView(true);
        mRepeatability = (TextView) findViewById(R.id.repeatability);
        mRepeatability.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showRepeatabilityDialog();
            }
        });

        mRingtone = (TextView) findViewById(R.id.ringtone);
        mRingtone.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setRingtone();
            }
        });
    }

    private void initSettings() {
        if (mAlarmId == NO_ALARM_ID) {
            mInfo = new AlarmInfo();
            mInfo.repeatability = AlarmInfo.NO_REPEAT;
            mRepeatability.setText(R.string.no_repeat);

            long currIndex = getAlarmIndex();
            setAlarmIndex(currIndex + 1);
            String name = getString(R.string.alarm) + currIndex;
            mEditName.setText(name);

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        } else {
            mInfo = mDBHelper.getAlarm(mAlarmId);
            if (mInfo != null) {
                mEditName.setText(mInfo.name);
                mTimePicker.setCurrentHour(mInfo.hour);
                mTimePicker.setCurrentMinute(mInfo.minute);
                mTimes.setSelection(mInfo.times);
                mInterval.setSelection(mInfo.interval);

                mSelectedItems = RepeatabilityHelper.parseRepeatability(mInfo.repeatability);
                mRepeatability.setText(RepeatabilityHelper.genRepeatabilityString(getApplicationContext(), mSelectedItems));
                mRingtone.setText(Uri.parse(mInfo.ringtone).getLastPathSegment());
            }
        }
    }

    private void showRepeatabilityDialog() {
        OnMultiChoiceClickListener listener = new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                mSelectedItems[which] = isChecked;
                mInfo.repeatability = RepeatabilityHelper.calcRepeatability(mSelectedItems);
                mRepeatability.setText(RepeatabilityHelper.genRepeatabilityString(getApplicationContext(), mSelectedItems));
            }
        };

        if (mAlarmId == NO_ALARM_ID) {
            mSelectedItems = new boolean[] { false, false, false };
        } else {
            mSelectedItems = RepeatabilityHelper.parseRepeatability(mInfo.repeatability);
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.choose_alarm_repeatability)
                .setMultiChoiceItems(R.array.alarm_repeatability, mSelectedItems, listener).create();
        dialog.show();
    }

    private void setRingtone() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String ringtone = uriToFilePath(data.getData());
            if (ringtone != null) {
                mInfo.ringtone = ringtone;
                mRingtone.setText(Uri.parse(ringtone).getLastPathSegment());
            } else {
                Toast.makeText(this, R.string.invalid_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String uriToFilePath(Uri uri) {
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            return uri.toString();
        } else if (scheme.equals("content")) {
            Uri queryUri = uri;
            String[] projection = { MediaStore.Audio.Media.DATA };
            String selection = null;
            if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this, uri)) {
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                selection = MediaStore.Audio.Media._ID + "=" + id;
            }

            Cursor cursor = getContentResolver().query(queryUri, projection, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    return cursor.getString(index);
                }
                cursor.close();
            }
            return null;
        } else {
            return null;
        }
    }

    private long getAlarmIndex() {
        SharedPreferences preferences = getSharedPreferences(PreferencesUtil.SETTINGS, Context.MODE_PRIVATE);
        return preferences.getLong(PreferencesUtil.ALARM_INDEX, 0);
    }

    private void setAlarmIndex(long index) {
        Editor editor = PreferencesUtil.getEditor(this);
        editor.putLong(PreferencesUtil.ALARM_INDEX, index);
        editor.commit();
    }
}
