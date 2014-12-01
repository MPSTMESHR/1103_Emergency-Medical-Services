/**
 * 
 */
package com.emergency.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.emergency.services.library.GeocodeJSONParser;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Roman
 *
 */
public class  DownloadTask extends AsyncTask<String, Integer, String>{

    String data = null;

    // Invoked by execute() method of this object
    @Override
    protected String doInBackground(String... url) {
            try{                    		
                    data = downloadUrl(url[0]);
            }catch(Exception e){
                     Log.d("Background Task",e.toString());
            }
            return data;
    }

    /**
	 * @param string
	 * @return
	 */
	private String downloadUrl(String strUrl) throws IOException{
	    String data = "";
	    InputStream iStream = null;
	    HttpURLConnection urlConnection = null;
	    try{
	            URL url = new URL(strUrl);


	            // Creating an http connection to communicate with url 
	            urlConnection = (HttpURLConnection) url.openConnection();

	            // Connecting to url 
	            urlConnection.connect();

	            // Reading data from url 
	            iStream = urlConnection.getInputStream();

	            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

	            StringBuffer sb  = new StringBuffer();

	            String line = "";
	            while( ( line = br.readLine())  != null){
	                    sb.append(line);
	            }

	            data = sb.toString();

	            br.close();

	    }catch(Exception e){
	            Log.d("Exception while downloading url", e.toString());
	    }finally{
	            iStream.close();
	            urlConnection.disconnect();
	    }

	    return data;
	    
	}

	// Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(String result){
    		
    		ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
    }

}


class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

	JSONObject jObject;
	static double lat;
	static double lng;
	
	@Override
	protected List<HashMap<String,String>> doInBackground(String... jsonData) {
	
		List<HashMap<String, String>> places = null;			
		GeocodeJSONParser parser = new GeocodeJSONParser();
    
        try{
        	jObject = new JSONObject(jsonData[0]);
        	
            /** Getting the parsed data as a an ArrayList */
            places = parser.parse(jObject);
            
        }catch(Exception e){
                Log.d("Exception",e.toString());
        }
        return places;
	}
	
	// Execute0d after the complete execution of doInBackground() method
	@Override
	public void onPostExecute(List<HashMap<String,String>> list){			

		for(int i=0;i<list.size();i++){
			
            HashMap<String, String> hmPlace = list.get(i);
			lat = Double.parseDouble(hmPlace.get("lat"));	            
            lng = Double.parseDouble(hmPlace.get("lng"));
            
            

           

        }       
		Log.d("Location","I am here" + lat + "  " + lng);
	}
}

