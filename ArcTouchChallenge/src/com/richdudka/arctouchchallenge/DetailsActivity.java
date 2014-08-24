package com.richdudka.arctouchchallenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class DetailsActivity extends Activity implements MainResultReceiver.Receiver
{
	static final int RUNNING = 1;
	static final int FINISHED = 2;
	static final int ERROR = 3;
	
	MainResultReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		receiver = new MainResultReceiver(new Handler());
		receiver.setReceiver(this);
		
		Intent incomingIntent = getIntent();
		String data = incomingIntent.getStringExtra("id");
		
		Log.v("DETAILS_ID", data);
		
		Intent outgoingIntent = new Intent(this, MainIntentService.class);
		outgoingIntent.putExtra("receiver", receiver);
		outgoingIntent.putExtra("requestData", data);
		outgoingIntent.putExtra("requestType", "details");
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch(resultCode)
		{
		case RUNNING:
			/*
			 * TODO: Show progress wheel
			 */
			break;
		case FINISHED:
			break;
		case ERROR:
			break;
		}
	}
}
