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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Base64;
import android.util.Log;

public class DetailsIntentService extends IntentService
{
	static final String USERNAME = "WKD4N7YMA1uiM8V";
	static final String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";
	static final String STOPS_URL = "https://api.appglu.com/v1/queries/findStopsByRouteId/run";
	static final String DEPARTURES_URL = "https://api.appglu.com/v1/queries/findDeparturesByRouteId/run";

	static final int STATUS_RUNNING = 1;
	static final int STATUS_FINISHED = 2;
	static final int STATUS_ERROR = 3;

	public DetailsIntentService()
	{
		super("DetailsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		ResultReceiver receiver = extras.getParcelable("receiver");
		String data = extras.getString("requestData");
		int id = Integer.parseInt(data);

		receiver.send(STATUS_RUNNING, Bundle.EMPTY);

		JSONObject body = new JSONObject();
		JSONObject params = new JSONObject();
		try {
			params.put("routeId", id);
			body.put("params", params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Bundle bundle = new Bundle();

		HttpPost stopsPost = new HttpPost(STOPS_URL);
		try {
			stopsPost.setEntity(new StringEntity(body.toString()));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		stopsPost.addHeader("Content-Type", "application/json");
		String credentials = USERNAME + ":" + PASSWORD;  
		String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);  
		stopsPost.addHeader("Authorization", "Basic " + base64EncodedCredentials);
		stopsPost.addHeader("X-AppGlu-Environment", "staging");
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(stopsPost);
			String json = EntityUtils.toString(response.getEntity());
			Log.v("STOPS", json);
			if (!validateJSON(json)) {
				receiver.send(STATUS_ERROR, Bundle.EMPTY);
			} else {
				JSONObject entity = new JSONObject(json);
				String stopNames = "";
				JSONArray jArray = entity.getJSONArray("rows");
				for (int i=0; i<jArray.length(); i++)
				{
					try {
						JSONObject object = jArray.getJSONObject(i);
						stopNames = stopNames + object.getString("name") + ";";
						bundle.putString("stopNames", stopNames);
						Log.v("STOP NAMES", stopNames);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpPost detailsPost = new HttpPost(DEPARTURES_URL);
		try {
			detailsPost.setEntity(new StringEntity(body.toString()));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		detailsPost.addHeader("Content-Type", "application/json");
		credentials = USERNAME + ":" + PASSWORD;  
		base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);  
		detailsPost.addHeader("Authorization", "Basic " + base64EncodedCredentials);
		detailsPost.addHeader("X-AppGlu-Environment", "staging");
		client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(detailsPost);
			String json = EntityUtils.toString(response.getEntity());
			Log.v("DEPARTURES", json);
			if (!validateJSON(json)) {
				receiver.send(STATUS_ERROR, Bundle.EMPTY);
			} else {
				JSONObject entity = new JSONObject(json);
				String weekdayTimes = "";
				String saturdayTimes = "";
				String sundayTimes = "";
				JSONArray jArray = entity.getJSONArray("rows");
				for (int i=0; i<jArray.length(); i++)
				{
					try {
						JSONObject object = jArray.getJSONObject(i);
						if (object.getString("calendar").equalsIgnoreCase("weekday"))
							weekdayTimes = weekdayTimes + object.getString("time") + " ";
						else if (object.getString("calendar").equalsIgnoreCase("saturday"))
							saturdayTimes = saturdayTimes + object.getString("time") + " ";
						else
							sundayTimes = sundayTimes + object.getString("time");
						bundle.putString("weekdayTimes", weekdayTimes);
						bundle.putString("saturdayTimes", saturdayTimes);
						bundle.putString("sundayTimes", sundayTimes);
						Log.v("WEEKDAY TIMES", weekdayTimes);
						Log.v("SATURDAY TIMES", saturdayTimes);
						Log.v("SUNDAY TIMES", sundayTimes);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				receiver.send(STATUS_FINISHED, bundle);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

boolean validateJSON(String string) {
	return string != null && ("null".equals(string)
			|| (string.startsWith("[") && string.endsWith("]"))
			|| (string.startsWith("{") && string.endsWith("}")));
}
}