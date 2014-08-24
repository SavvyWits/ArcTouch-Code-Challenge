package com.richdudka.arctouchchallenge;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends ListActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void mainClickHandler(View v)
    {
    	EditText search = (EditText) findViewById(R.id.searchBox);
    	String data = "%" + search.getText().toString() + "%";
        
        Intent intent = new Intent(this, MainIntentService.class);
        intent.putExtra("requestData", data);
        startService(intent);
    }
}
