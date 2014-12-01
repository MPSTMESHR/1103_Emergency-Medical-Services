package com.emergency.services.rss;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;
import android.content.Context;

public class RSSimpleFeedList {

	ArrayList<String> feeds;
	final String file = "RSSimpleFeeds.txt";
	Context context;

	public RSSimpleFeedList(Context context) {
		feeds = new ArrayList<String>();
		this.context = context;
	}

	public String[] array() {
		String[] ar = new String[feeds.size()];
		feeds.toArray(ar);
		return (ar);
	}

	public void getFeeds() {
		feeds.clear();

		try {
			InputStream is = context.openFileInput(file);
			byte[] buf = new byte[is.available()];
			is.read(buf);
			String[] lines = new String(buf).split("\\n");

			for (int i = 0; i < lines.length; i++) {
				if (lines[i].length() > 0)
					feeds.add(lines[i]);
			} 

			is.close();
		} catch (FileNotFoundException e) {
			Log.w("InternalStorage", "Can't find " + file, e);
		} catch (IOException e) {
			Log.w("InternalStorage", "Error writing " + file, e);
		}
	}

	public void saveFeeds() {
		try {
			OutputStream os = context.openFileOutput(file, Context.MODE_PRIVATE);

			String feed_list = new String("");
			for (int i = 0; i < feeds.size(); i++)
				feed_list += feeds.get(i) + "\n";
			if (feed_list.length() > 0) {
				byte[] buf = feed_list.getBytes();
				os.write(buf);
			}
			os.close();
		} catch (FileNotFoundException e) {
			Log.w("RSSimple/ExternalStorage", "Can't find " + file, e);
		} catch (IOException e) {
			Log.w("RSSimple/ExternalStorage", "Error writing " + file, e);
		}
	}

	public void removeFeed(String feedURL) {
		feeds.remove(feedURL);
		saveFeeds();
	}

	public void removeAllFeeds() {
		feeds.clear();
		saveFeeds();
	}

	public void addFeed(String feedURL) {
		try {
			URL url = new URL(feedURL);;
			feeds.add(feedURL);
			saveFeeds();
		} catch (MalformedURLException e) {
			Log.e("RSSimple", feedURL + " is not a valid URL");
		}
	}
}
