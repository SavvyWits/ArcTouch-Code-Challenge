package com.richdudka.arctouchchallenge;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DetailsActivity extends ListActivity implements MainResultReceiver.Receiver
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
		String stopName = incomingIntent.getStringExtra("stopName");
		TextView detailsStopName = (TextView) findViewById(R.id.detailsStopName);
		detailsStopName.setText(stopName);
		String data = incomingIntent.getStringExtra("id");
		
		Intent outgoingIntent = new Intent(this, DetailsIntentService.class);
		outgoingIntent.putExtra("receiver", receiver);
		outgoingIntent.putExtra("requestData", data);
		outgoingIntent.putExtra("requestType", "details");
		startService(outgoingIntent);
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
			String details = resultData.getString("details");
			String[] result = details.split(";");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, result);
			setListAdapter(adapter);
			break;
		case ERROR:
			break;
		}
	}
}
