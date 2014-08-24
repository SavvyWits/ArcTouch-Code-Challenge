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

public class MainIntentService extends IntentService
{
	static final String USERNAME = "WKD4N7YMA1uiM8V";
	static final String PASSWORD = "DtdTtzMLQlA0hk2C1Yi5pLyVIlAQ68";
	static final String ROUTES_URL = "https://api.appglu.com/v1/queries/findRoutesByStopName/run";
	static final String STOPS_URL = "https://api.appglu.com/v1/queries/findStopsByRouteId/run";
	static final String DEPARTURES_URL = "https://api.appglu.com/v1/queries/findDeparturesByRouteId/run";

	static final int STATUS_RUNNING = 1;
	static final int STATUS_FINISHED = 2;
	static final int STATUS_ERROR = 3;

	int id;

	HttpPost post;

	public MainIntentService()
	{
		super("MainIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		ResultReceiver receiver = extras.getParcelable("receiver");
		String type = extras.getString("requestType");
		String data = extras.getString("requestData");

		if (type.equalsIgnoreCase("details"))
			id = Integer.parseInt(data);

		receiver.send(STATUS_RUNNING, Bundle.EMPTY);

		JSONObject body = new JSONObject();
		JSONObject params = new JSONObject();
		try {
			if (type.equalsIgnoreCase("list"))
				params.put("stopName", data);
			else
				params.put("routeId", id);
			body.put("params", params);
			Log.v("JSON OBJECT", body.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (type.equalsIgnoreCase("list"))
			post = new HttpPost(ROUTES_URL);
		else
			post = new HttpPost(STOPS_URL);
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
			String json = EntityUtils.toString(response.getEntity());
			if (!validateJSON(json)) {
				receiver.send(STATUS_ERROR, Bundle.EMPTY);
			} else {
				Bundle bundle = new Bundle();
				JSONObject entity = new JSONObject(json);
				if (type.equalsIgnoreCase("list"))
				{
					String ids = "";
					String shortNames = "";
					String longNames = "";
					JSONArray jArray = entity.getJSONArray("rows");
					for (int i=0; i<jArray.length(); i++)
					{
						try {
							JSONObject object = jArray.getJSONObject(i);
							ids = ids + object.getInt("id") + ";";
							shortNames = shortNames + object.getLong("shortName") + ";";
							longNames = longNames + object.getString("longName") + ";";
							bundle.putString("ids", ids);
							bundle.putString("shortNames", shortNames);
							bundle.putString("longNames", longNames);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				receiver.send(STATUS_FINISHED, bundle);
			}
			Log.v("JSON REST RESPONSE", json);
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

	static boolean validateJSON(String string) {
		return string != null && ("null".equals(string)
				|| (string.startsWith("[") && string.endsWith("]"))
				|| (string.startsWith("{") && string.endsWith("}")));
	}
}