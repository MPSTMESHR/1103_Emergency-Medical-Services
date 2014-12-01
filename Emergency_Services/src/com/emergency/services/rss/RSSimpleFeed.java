package com.emergency.services.rss;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.InputSource;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.emergency.services.R;
import com.emergency.services.R.id;
import com.emergency.services.R.layout;
import com.emergency.services.R.menu;
import com.emergency.services.rssparser.RSSEntry;
import com.emergency.services.rssparser.RSSParser;

import java.util.ArrayList;

public class RSSimpleFeed extends ListActivity
{
	private RSSParser parser;
	private URL url;
	private URL default_url;
	private String data;
	ArrayAdapter<RSSEntry> aa;
	ArrayList<RSSEntry> feed_entries;
	ListActivity activity = this;

	public RSSimpleFeed() throws Exception {
		parser = new RSSParser();
		url = default_url = new URL("http://www.health.com/health/diet-fitness/feed");
		data = "No entries found";
	}

	private ArrayList<RSSEntry> parseFeed() throws Exception {
		Log.d("RSS is",parser.toString());
		return (parser.parse(new InputSource(url.openStream())));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.feed_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;

		switch (item.getItemId()) {
		case R.id.remove_feed:
			remove_feed(this.getTitle().toString());
			break;
		case R.id.remove_all_feeds:
			remove_all_feeds();
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}

		return (result);
	}

	private void remove_feed(String feed_url) {
		RSSimpleFeedList feed_list = new RSSimpleFeedList(activity);
		feed_list.getFeeds();
		feed_list.removeFeed(feed_url);
		this.finish();
	}

	private void remove_all_feeds() {
		RSSimpleFeedList feed_list = new RSSimpleFeedList(activity);
		feed_list.getFeeds();
		feed_list.removeAllFeeds();
		this.finish();
	}

    /** Called when the activity is first created. */
 
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

	final Intent intent = getIntent();

	try {
		url = new URL(intent.getStringExtra("url"));
	} catch (MalformedURLException e) {
		Log.e("RSSimplee", "Exception: " + e.getMessage());
		url = default_url;
	}

	this.setTitle(url.toString());

	ListView lv = getListView();
	lv.setTextFilterEnabled(true);
	lv.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			String item_url = ((RSSEntry) parent.getItemAtPosition(position)).getLink();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(item_url));
			startActivity(intent);
		}
	});

	aa = new ArrayAdapter<RSSEntry>(this, R.layout.list_item_rss);
	setListAdapter(aa);

	ArrayList<RSSEntry> result = new ArrayList<RSSEntry>();

	try {
		result = parseFeed();
		Log.e("RSSimplea", "Result: " + result);
		
	} catch (Exception e) {
		
		Log.e("RSSimplea", "Exception: " + e.getMessage());
	}

	for (int i = 0; i < result.size(); i++)
		aa.add(result.get(i));
    }
}
