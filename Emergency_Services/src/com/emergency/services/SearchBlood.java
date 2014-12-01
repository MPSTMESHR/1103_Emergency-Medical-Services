package com.emergency.services;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emergency.services.library.GPSTracker;
import com.emergency.services.library.GetDataFromDB;
import com.emergency.services.library.SpinnerActivity;
import com.emergency.services.library.Users;
import com.emergency.services.library.UserFunctions;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class SearchBlood extends Activity {

	String data = "";
	TableLayout tl;
	TableRow tr;
	TextView label;
	private Spinner spinner;
	
	Button btnsearch;
	ProgressBar mProgressBar;
	// GPSTracker class
	GPSTracker gps;
	
	double current_lat;
	double current_lng;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blood_search);
		
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		 StrictMode.setThreadPolicy(policy);
		 
		 
        
		    spinner = (Spinner) findViewById(R.id.select_blood_group);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.blood_group, android.R.layout.simple_spinner_dropdown_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);

	        // Spinner item selection Listener  
	        addListenerOnSpinnerItemSelection();
        
        btnsearch = (Button) findViewById(R.id.btnSearch);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar1);
        
        btnsearch.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				
				mProgressBar.setVisibility(View.VISIBLE);
				
				 gps = new GPSTracker(SearchBlood.this);

					// check if GPS enabled		
			        if(gps.canGetLocation()){
			        	
			        	current_lat = gps.getLatitude();
			        	current_lng = gps.getLongitude();
			        	
			        	// \n is for new line
			        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + current_lat + "\nLong: " + current_lng, Toast.LENGTH_LONG).show();	
			        }else{
			        	// can't get location
			        	// GPS or Network is not enabled
			        	// Ask user to enable GPS/network in settings
			        	gps.showSettingsAlert();
			        }
			        
			        final String lat= String.valueOf(current_lat);
			        final String lng = String.valueOf(current_lng);
			        
			        final String bloodgroup_selected = SpinnerActivity.bloodgroup;
			        System.out.println(bloodgroup_selected);
//			        UserFunctions userFunction = new UserFunctions();
//			        JSONObject json = userFunction.bloodSearch(lat,lng);

			        
			        
					tl = (TableLayout) findViewById(R.id.maintable);
					clearTable();
					final GetDataFromDB getdb = new GetDataFromDB();
					new Thread(new Runnable() {
						public void run() {
							
							
							
							
							
							data = getdb.getDataFromDB(lat,lng,bloodgroup_selected);
							System.out.println(data);
							
							// Thread sleeps for displaying spinner progress bar.You can remove this try catch block if you want.
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							runOnUiThread(new Runnable() {		
								public void run() {
									ArrayList<Users> users = parseJSON(data);
									addData(users);
									mProgressBar.setVisibility(View.GONE);
								}
							});
							
						}
					}).start();
					
					
					

			}
		});
        
	
		
	}

	/**
	 * 
	 */
	private void addListenerOnSpinnerItemSelection() {
		// TODO Auto-generated method stub
        spinner.setOnItemSelectedListener(new SpinnerActivity());
	}

	public ArrayList<Users> parseJSON(String result) {
		ArrayList<Users> users = new ArrayList<Users>();
		try {
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				Users user = new Users();
				//user.setId(json_data.getInt("id"));
				user.setName(json_data.getString("name"));
				user.setPhone(json_data.getString("phone"));
				user.setPlace(json_data.getString("distance"));
				users.add(user);
			}
		} catch (JSONException e) {
			Log.e("log_tag1", "Error parsing data " + e.toString());  
		}
		return users;
	}

	void addHeader(){
		/** Create a TableRow dynamically **/
		tr = new TableRow(this);

		/** Creating a TextView to add to the row **/
		label = new TextView(this);
		label.setText("DONOR");
		label.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		label.setPadding(5, 5, 5, 5);
		label.setBackgroundColor(Color.GRAY);
		LinearLayout Ll = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 5, 5, 5);
		//Ll.setPadding(10, 5, 5, 5);
		Ll.addView(label,params);
		tr.addView((View)Ll); // Adding textView to tablerow.
		

		
		
		
		TextView phone = new TextView(this);
		phone.setText("Phone");
		phone.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		phone.setPadding(5, 5, 5, 5);
		phone.setBackgroundColor(Color.GRAY);
		Ll = new LinearLayout(this);
		params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 5, 5, 5);
		//Ll.setPadding(10, 5, 5, 5);
		Ll.addView(phone,params);
		tr.addView((View)Ll); // Adding textview to tablerow.
		
		
		/** Creating distance Button **/
		TextView place = new TextView(this);
		place.setText("DISTANCE");
		place.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		place.setPadding(5, 5, 5, 5);
		place.setBackgroundColor(Color.GRAY);
		Ll = new LinearLayout(this);
		params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 5, 5, 5);
		//Ll.setPadding(10, 5, 5, 5);
		Ll.addView(place,params);
		tr.addView((View)Ll); // Adding textview to tablerow.

		 // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
	}
	
	@SuppressWarnings({ "rawtypes" })
	public void addData(ArrayList<Users> users) {

		addHeader();
		
		for (Iterator i = users.iterator(); i.hasNext();) {

			Users p = (Users) i.next();

			/** Create a TableRow dynamically **/
			tr = new TableRow(this);

			/** Creating a TextView to add to the row **/
			label = new TextView(this);
			label.setText(p.getName());
			label.setId(p.getId());
			label.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			label.setPadding(5, 5, 5, 5);
			//label.setBackgroundColor(Color.GRAY);
			LinearLayout Ll = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 2, 2, 2);
			//Ll.setPadding(10, 5, 5, 5);
			Ll.addView(label,params);
			tr.addView((View)Ll); // Adding textView to tablerow.
			
			

			
			
			
			/** Creating distance Button **/
			TextView phone = new TextView(this);
			phone.setText(p.getPhone());
			phone.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			phone.setPadding(5, 5, 5, 5);
			//place.setBackgroundColor(Color.GRAY);
			Ll = new LinearLayout(this);
			params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 2, 2, 2);
			//Ll.setPadding(10, 5, 5, 5);
			Ll.addView(phone,params);
			tr.addView((View)Ll);
			
			
			

			/** Creating distance Button **/
			TextView place = new TextView(this);
			place.setText(p.getPlace());
			place.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			place.setPadding(5, 5, 5, 5);
			//place.setBackgroundColor(Color.GRAY);
			Ll = new LinearLayout(this);
			params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 2, 2, 2);
			//Ll.setPadding(10, 5, 5, 5);
			Ll.addView(place,params);
			tr.addView((View)Ll); // Adding textview to tablerow.

			 // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
		}
	}
	
	
	public void clearTable(){
		
		tl.removeAllViews();
	}
}
