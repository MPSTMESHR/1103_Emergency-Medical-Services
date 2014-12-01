package com.emergency.services;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.telephony.SmsManager;
import com.emergency.services.library.GPSTracker;

public class SosActivity extends Activity {
	
	Button btnShowLocation;
	Button btnSetting;
	String message;
	// GPSTracker class
	GPSTracker gps;
	String phoneNumber1 = "5556";
	String phoneNumber2 = "5556";
	EditTextPreference pN;
    boolean flag = false;

    boolean flag2 = false;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos);
        
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String phone1 = sharedPref.getString(SosSettingsActivity.KEY_PREF_PHONE_1, "");
        String phone2 = sharedPref.getString(SosSettingsActivity.KEY_PREF_PHONE_2, "");
        String user_message = sharedPref.getString(SosSettingsActivity.KEY_PREF_MESSAGE, "");
        
//        Log.e("roman",phone1);
//        Log.e("roman",phone2);
        
        
        phoneNumber1=phone1;
        phoneNumber2=phone2;
        final String user_msg= user_message;
        
 //       btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnSetting = (Button) findViewById(R.id.sosSetting);
 //       pN = (EditTextPreference) findViewById(R.id.phoneNumber);
        
        // show location button click event
//        btnShowLocation.setOnClickListener(new View.OnClickListener() {
//			
//			public void onClick(View arg0) {		
//				// create class object
//		        gps = new GPSTracker(SosActivity.this);
//
//				// check if GPS enabled		
//		        if(gps.canGetLocation()){
//		        	
//		        	double latitude = gps.getLatitude();
//		        	double longitude = gps.getLongitude();
//		        	
//		        	// \n is for new line
//		        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//		        	message= user_msg + "\nMy Location is - \nLat: " + latitude + "\nLong: " + longitude + "\n\nhttp://maps.google.com/?q="+latitude+","+longitude;
//		        	String uri = "http://maps.google.com/?q="+latitude+","+longitude;
//		        	sendSMS();
//		        	}else{
//		        	// can't get location
//		        	// GPS or Network is not enabled
//		        	// Ask user to enable GPS/network in settings
//		        	gps.showSettingsAlert();
//		        	
//		        }
//				
//			}
//		});
//        
        ImageView imgFavorite = (ImageView) findViewById(R.id.sos_icon);
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 gps = new GPSTracker(SosActivity.this);

 				// check if GPS enabled		
 		        if(gps.canGetLocation()){
 		        	
 		        	double latitude = gps.getLatitude();
 		        	double longitude = gps.getLongitude();
 		        	
 		        	// \n is for new line
 		        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
 		        	message=user_msg + "\nMy Location is - \nLat: " + latitude + "\nLong: " + longitude + "\n\nhttp://maps.google.com/?q="+latitude+","+longitude;
 		        	String uri = "http://maps.google.com/?q="+latitude+","+longitude;
 		        	sendSMS();
 		        	}else{
 		        	// can't get location
 		        	// GPS or Network is not enabled
 		        	// Ask user to enable GPS/network in settings
 		        	gps.showSettingsAlert();
 		        }
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View view) {
				Intent sos = new Intent(getApplicationContext(),
						SosSettingsActivity.class);
				startActivity(sos);
			}
 		});
        

     }
    public void sendSMS() 
    {
       
         SmsManager smsManager = SmsManager.getDefault();
         smsManager.sendTextMessage(phoneNumber1, null, message, null, null);
         smsManager.sendTextMessage(phoneNumber2, null, message, null, null);
         
     }
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//	     switch(keyCode){
//	       case KeyEvent.KEYCODE_VOLUME_UP:
//	         event.startTracking();
//	        // Toast.makeText(this,"Volumen Up pressed", Toast.LENGTH_SHORT).show();
//	         return true;
//	       case KeyEvent.KEYCODE_VOLUME_DOWN:
//	    	   event.startTracking();
//	         //Toast.makeText(this,"Volumen Down pressed", Toast.LENGTH_SHORT).show();
//	         return true;
//	     }
//	     return onKeyDown(keyCode, event);
//	    }
//	     
//    	@Override
//	    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//    	sendSMS();
//	     Toast.makeText(this, "Pressed for a long time =) ", Toast.LENGTH_SHORT).show();
//	     return true;
//	    }

 }
