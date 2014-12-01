package com.emergency.services.rss;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.emergency.services.R;
import com.emergency.services.R.id;
import com.emergency.services.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class RSSimpleFeedAdd extends Activity
{
	Activity activity = this;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedadd);
		Intent intent = new Intent(this, RSSimple.class);

		Button add_button = (Button) findViewById(R.id.add);
		add_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				RSSimpleFeedList feed_list = new RSSimpleFeedList(activity);
				feed_list.getFeeds();
				TextView tv = (TextView) findViewById(R.id.new_feed);
				String feed = tv.getText().toString();
				feed_list.addFeed(tv.getText().toString());
				activity.finish();
			}
		});
	}
}
