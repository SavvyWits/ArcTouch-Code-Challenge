/*
 * Copyright (C) 2014 Rich Dudka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.richdudka.arctouchchallenge;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends ListActivity implements MainResultReceiver.Receiver
{
	static final int RUNNING = 1;
	static final int FINISHED = 2;
	static final int ERROR = 3;

	String ids;
	String stopNames;

	MainResultReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		receiver = new MainResultReceiver(new Handler());
		receiver.setReceiver(this);

		Intent intent = getIntent();
		// This runs if the DetailsActivity back button sent the intent
		// by checking if it has the right extra
		if (intent.hasExtra("ids"))
		{
			TextView routesTitle = (TextView) findViewById(R.id.routesTitle);
			routesTitle.setVisibility(View.VISIBLE);
			ids = intent.getStringExtra("ids");
			stopNames = intent.getStringExtra("longNames");
			String[] result = stopNames.split(";");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, result);
			setListAdapter(adapter);

			// No need to show the keyboard since the list will be populated
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);		
		}
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

	public void mainClickHandler(View v)
	{
		// Get the search term and surround it with wild cards
		EditText search = (EditText) findViewById(R.id.searchBox);
		String data = "%" + search.getText().toString() + "%";

		// Start the intent service
		Intent intent = new Intent(this, MainIntentService.class);
		intent.putExtra("receiver", receiver);
		intent.putExtra("requestData", data);
		intent.putExtra("requestType", "list");
		startService(intent);

		// We no longer need the keyboard
		InputMethodManager inputManager = (InputMethodManager)
				getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData)
	{
		switch(resultCode)
		{
		case RUNNING:
			// Show a progress while retrieving the search results
			ProgressBar pb = (ProgressBar) findViewById(R.id.progress);
			pb.setVisibility(View.VISIBLE);
			break;
		case FINISHED:
			// Get the ids string and save it for future use
			// Get the longNames string and send it to the list adapter.
			TextView routesTitle = (TextView) findViewById(R.id.routesTitle);
			routesTitle.setVisibility(View.VISIBLE);
			ids = resultData.getString("ids");
			stopNames = resultData.getString("longNames");
			String[] result = stopNames.split(";");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, result);
			setListAdapter(adapter);
			break;
		case ERROR:
			// TODO: Error handling i.e. dialog
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		// DetailsActivity needs the clicked position details
		// and the full ids and stopNames strings in case its
		// Back button is pressed
		Intent intent = new Intent(this, DetailsActivity.class);
		String stopName = stopNames.split(";")[position];
		String idNumber = ids.split(";")[position];
		intent.putExtra("stopName", stopName);
		intent.putExtra("id", idNumber);
		intent.putExtra("ids", ids);
		intent.putExtra("longNames", stopNames);
		startActivity(intent);
	}
}
