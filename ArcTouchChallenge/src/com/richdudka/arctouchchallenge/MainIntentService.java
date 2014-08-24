package com.richdudka.arctouchchallenge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

public class MainIntentService extends IntentService
{
	private static final String USERNAME = "WKD4N7YMA1uiM8V";
	private static final String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";
	private static final String ROUTES_URL = "https://api.appglu.com/v1/queries/findRoutesByStopName/run";
	
	public MainIntentService()
	{
		super("MainIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		String data = intent.getStringExtra("requestData");
		JSONObject body = new JSONObject();
		JSONObject params = new JSONObject();
		try {
			params.put("stopName", data);
			body.put("params", params);
			Log.v("JSON OBJECT", body.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HttpPost post = new HttpPost(ROUTES_URL);
		try {
			post.setEntity(new StringEntity(body.toString()));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		post.addHeader("Content-Type", "application/json");
		String credentials = USERNAME + ":" + PASSWORD;  
		String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);  
		post.addHeader("Authorization", "Basic " + base64EncodedCredentials);
		post.addHeader("X-AppGlu-Environment", "staging");
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(post);
			String entity = EntityUtils.toString(response.getEntity());
			SharedPreferences.Editor prefEditor = getSharedPreferences("stopsData", 0).edit();
			prefEditor.putString("json", entity);
			prefEditor.commit();
			Log.v("JSON REST RESPONSE", entity);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}