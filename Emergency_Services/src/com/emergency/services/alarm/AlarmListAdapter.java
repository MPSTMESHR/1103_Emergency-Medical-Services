package com.emergency.services.alarm;

import java.util.ArrayList;
import java.util.List;

//import com.emergency.services.alarm.R;
import com.emergency.services.R;
import com.emergency.services.db.AlarmInfo;
import com.emergency.services.db.DBHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmListAdapter extends BaseAdapter {

    private Context mContext;
    private DBHelper mDBHelper;
    private List<AlarmInfo> mAlarmList;

    public AlarmListAdapter(Context context, DBHelper dbHelper) {
        mContext = context;
        mDBHelper = dbHelper;
        mAlarmList = new ArrayList<AlarmInfo>();
    }

    /**
     * @param alarmList
     *            the mAlarmList to set
     */
    public void setAlarmList(List<AlarmInfo> alarmList) {
        mAlarmList = alarmList;
    }

    /**
     * @return the mAlarmList
     */
    public List<AlarmInfo> getAlarmList() {
        return mAlarmList;
    }

    public int getCount() {
        return mAlarmList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_alarm, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.switcher = (Switch) convertView
                    .findViewById(R.id.switcher);
            viewHolder.switcher.setOnClickListener(mListener);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(mAlarmList.get(position).name);
        viewHolder.switcher.setChecked(mAlarmList.get(position).isEnable);
        viewHolder.switcher.setTag(position);
        return convertView;
    }

    static final class ViewHolder {
        TextView name;
        Switch switcher;
    }

    OnClickListener mListener = new OnClickListener() {
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj != null) {
                int position = (Integer) obj;
                Switch switcher = (Switch) v;
                int ret = mDBHelper.setAlarmEnable(mAlarmList.get(position).id,
                        switcher.isChecked());
                if (ret < 1) {
                    Toast.makeText(mContext, R.string.saving_fail,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
