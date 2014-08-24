package com.richdudka.arctouchchallenge;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ListActivity implements MainResultReceiver.Receiver, OnItemClickListener
{
	static final int RUNNING = 1;
	static final int FINISHED = 2;
	static final int ERROR = 3;

	String ids;
	
	MainResultReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		receiver = new MainResultReceiver(new Handler());
		receiver.setReceiver(this);
	}

	public void mainClickHandler(View v)
	{
		EditText search = (EditText) findViewById(R.id.searchBox);
		String data = "%" + search.getText().toString() + "%";

		Intent intent = new Intent(this, MainIntentService.class);
		intent.putExtra("receiver", receiver);
		intent.putExtra("requestData", data);
		intent.putExtra("requestType", "list");
		startService(intent);
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData)
	{
		switch(resultCode)
		{
		case RUNNING:
			/*
			 * TODO: Show progress wheel
			 */
			break;
		case FINISHED:
			/*
			 * Get the longNames string and send it to the list adapter.
			 */
			ids = resultData.getString("ids");
			String longNames = resultData.getString("longNames");
			String[] result = longNames.split(";");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, result);
			setListAdapter(adapter);
			break;
		case ERROR:
			break;
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id)
	{
		//super.onListItemClick(av, v, position, id);
		
		Intent intent = new Intent(this, DetailsActivity.class);
		String idNumber = ids.split(";")[position];
		intent.putExtra("id", idNumber);
		startActivity(intent);
	}
}
