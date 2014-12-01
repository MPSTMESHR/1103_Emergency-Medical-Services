package com.emergency.services.rss;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import com.emergency.services.R;
import com.emergency.services.R.layout;

public class RSSimple extends ListActivity
{
	ArrayAdapter<String> aa;
	File file;
	RSSimpleFeedList feeds;
	ListActivity activity = this;

	private void loadFeeds() {
		feeds.getFeeds();

		ArrayList<String> feed_list = new ArrayList<String>();
		feed_list.add("Add new entry...");

		String[] feed_array = feeds.array();
		for (int i = 0; i < feed_array.length; i++) {
			feed_list.add(feed_array[i]);
		}
		
		String[] tmp_array = new String[feed_list.size()];
		feed_list.toArray(tmp_array);
		aa = new ArrayAdapter<String>(this, R.layout.list_item_rss, tmp_array);
		setListAdapter(aa);

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		feeds = new RSSimpleFeedList(activity);
		loadFeeds();

		final Intent intent = new Intent(this, RSSimpleFeed.class);
		final Intent intent_feed_add = new Intent(this, RSSimpleFeedAdd.class);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item_url = (String) parent.getItemAtPosition(position);
				Log.i("RSSimple", item_url);
				if (id == 0)
					startActivity(intent_feed_add);
				else {
					intent.putExtra("url", item_url);
					startActivity(intent);
				}
			}
		});
	}

	/** Called when the activity is about to become visible. */
	@Override
	protected void onStart() {
		super.onStart();

		loadFeeds();
	}

}
