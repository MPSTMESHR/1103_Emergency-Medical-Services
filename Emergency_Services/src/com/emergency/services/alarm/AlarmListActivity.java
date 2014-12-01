package com.emergency.services.alarm;

import java.util.List;

import com.emergency.services.R;
import com.emergency.services.db.AlarmInfo;
import com.emergency.services.db.DBHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class AlarmListActivity extends Activity implements OnItemClickListener,
        OnItemLongClickListener {

    private DBHelper mDBHelper;
    private ListView mListView;
    private AlarmListAdapter mAdapter;
    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        mDBHelper = new DBHelper(getApplication());
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new AlarmListAdapter(this, mDBHelper);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView
                .setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
                    public void onCreateContextMenu(ContextMenu menu, View v,
                            ContextMenuInfo menuInfo) {
                        menu.add(R.string.delete);
                    }
                });
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent intent = new Intent(this, AlarmEditActivity.class);
        intent.putExtra(AlarmEditActivity.ALARM_ID, mAdapter.getAlarmList()
                .get(position).id);
        startActivity(intent);
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        mPosition = position;
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(R.string.new_alarm);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = new Intent(this, AlarmEditActivity.class);
        startActivity(intent);
        return true;
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (mDBHelper.deleteAlarm(mAdapter.getAlarmList().get(mPosition).id)) {
            refreshList();
        }
        return true;
    }

    private void refreshList() {
        List<AlarmInfo> alarmList = mDBHelper.getAlarmList(false);
        mAdapter.setAlarmList(alarmList);
        mAdapter.notifyDataSetChanged();
    }

}
