package com.richdudka.arctouchchallenge;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DetailsActivity extends ListActivity implements MainResultReceiver.Receiver
{
	static final int RUNNING = 1;
	static final int FINISHED = 2;
	static final int ERROR = 3;
	
	String ids;
	String stopNames;
	
	MainResultReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		receiver = new MainResultReceiver(new Handler());
		receiver.setReceiver(this);
		
		// Retrieve the extras from the intent
		Intent incomingIntent = getIntent();
		String stopName = incomingIntent.getStringExtra("stopName");
		TextView detailsStopName = (TextView) findViewById(R.id.detailsStopName);
		detailsStopName.setText(stopName);
		String data = incomingIntent.getStringExtra("id");
		ids = incomingIntent.getStringExtra("ids");
		stopNames = incomingIntent.getStringExtra("longNames");
		
		// Start the intent service
		Intent outgoingIntent = new Intent(this, DetailsIntentService.class);
		outgoingIntent.putExtra("receiver", receiver);
		outgoingIntent.putExtra("requestData", data);
		outgoingIntent.putExtra("requestType", "details");
		startService(outgoingIntent);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		receiver.setReceiver(null);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		receiver.setReceiver(this);
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData)
	{
		switch(resultCode)
		{
		case RUNNING:
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
	
	public void mainClickHandler(View v)
	{
		// MainActivity needs these strings in order to properly populate its list
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("ids", ids);
		intent.putExtra("longNames", stopNames);
		startActivity(intent);
	}
}
