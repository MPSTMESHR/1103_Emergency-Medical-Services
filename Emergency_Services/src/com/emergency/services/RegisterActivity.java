
package com.emergency.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import com.emergency.services.ParserTask;
import com.emergency.services.library.DatabaseHandler;
import com.emergency.services.library.PlacesAutoCompleteAdapter;
import com.emergency.services.library.SpinnerActivity;
import com.emergency.services.library.UserFunctions;
import com.emergency.services.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.TextView;

public class RegisterActivity extends Activity  implements OnItemClickListener{
	private static final List<HashMap<String, String>> List = null;
	Button btnRegister;
	Button btnLinkToLogin;
	Button btnConf;
	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword;
	EditText inputPhone;
	TextView registerErrorMsg;
	private Spinner spinner;
	
	
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	@SuppressWarnings("unused")
	private static String KEY_ERROR = "error";
	@SuppressWarnings("unused")
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	static String location;
	static double location_lat;
	static double location_lng;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		//Autocomplete text view google api for getting placeses
	    final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
	    autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
	    autoCompView.setOnItemClickListener(this);


	    
        
        spinner = (Spinner) findViewById(R.id.select_blood_group);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.blood_group, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        

        // Spinner item selection Listener  
        addListenerOnSpinnerItemSelection();


        
	    // Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		inputPhone = (EditText) findViewById(R.id.phone_number);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		btnConf =(Button) findViewById(R.id.btnConf);


		btnConf.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {

				//lat long code :google geocoding api cal
				
				location = autoCompView.getText().toString();
				
				
				if(location==null || location.equals("")){
					Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String url = "https://maps.googleapis.com/maps/api/geocode/json?";					
			
				try {
					// encoding special characters like space in the user input place
					location = URLEncoder.encode(location, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				String address = "address=" + location;
				
				String sensor = "sensor=false";
				
				String api_key = "AIzaSyDgaXllvzAmrnPaSIoXdWibruto3I9sLtc";
				
				// url , from where the geocoding data is fetched
				url = url + address + "&" + sensor + "&" + api_key;
				
				
				// Instantiating DownloadTask to get places from Google Geocoding service
				// in a non-ui thread

				DownloadTask downloadTask = new DownloadTask();
				
				// Start downloading the geocoding places
				downloadTask.execute(url);
				
				
				location_lat = ParserTask.lat;
				location_lng = ParserTask.lng;
				
				Log.d("MyApp1","I am here "+ location_lat + location_lng );
				
	}
});
		
		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				
			// check for login response	
				
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				String lat = String.valueOf(location_lat);
				String lng = String.valueOf(location_lng);
				String phone = inputPhone.getText().toString();
				String blood = String.valueOf(spinner.getSelectedItem());
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.registerUser(name, email, password, lat, lng, phone, blood);
				
				
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						registerErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully registred
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
							// Launch Dashboard Screen
							Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(dashboard);
							// Close Registration Screen
							finish();
						}else{
							// Error in registration
							registerErrorMsg.setText("Error occured in registration");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		
		
		
		
		
		
		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(i);
				// Close Registration View
				finish();
			}
		});
	}

/* (non-Javadoc)
 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
 */	    
	
/**
	 * 
	 */
	private void addListenerOnSpinnerItemSelection() {
		// TODO Auto-generated method stub
		
        spinner.setOnItemSelectedListener(new SpinnerActivity());
		
	}

public void onItemClick(AdapterView<?> adapterView, View view, int position, long id ) {
     String str = (String) adapterView.getItemAtPosition(position);
     Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
   //  Log.d("MyApp1","I am here "+ location_lat + location_lng );
     
     String bloodgroup = String.valueOf(spinner.getSelectedItem());
     Toast.makeText(RegisterActivity.this,
             "On Button Click : " + 
             "\n" + bloodgroup ,
             Toast.LENGTH_LONG).show();

}


}
