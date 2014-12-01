package com.emergency.services.library;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class GetDataFromDB {
	
	static InputStream is = null;

	public String getDataFromDB(String lat,String lng,String bloodgroup) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("lat", lat));
		param.add(new BasicNameValuePair("lng", lng));
		param.add(new BasicNameValuePair("bloodgroup", bloodgroup));
		try {
			
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://emergency.byethost10.com/GetUsers.php");
						httpPost.setEntity(new UrlEncodedFormEntity(param));

						HttpResponse httpResponse = httpClient.execute(httpPost);
						HttpEntity httpEntity = httpResponse.getEntity();
						is = httpEntity.getContent();
			
			
			
			
		//	HttpPost httppost;
//			HttpClient httpclient;
//			httpclient = new DefaultHttpClient();
//			httppost = new HttpPost("http://10.0.2.2/GetUsers.php"); 
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			final String response = httpClient.execute(httpPost,
					responseHandler);
			
			return response.trim();

		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage());
			return "error";
		}
	}
}
