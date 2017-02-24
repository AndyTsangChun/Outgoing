package com.outgoing.activity;

import java.util.ArrayList;
import java.util.Observable;

import com.outgoing.R;
import com.outgoing.R.layout;
import com.outgoing.R.menu;
import com.outgoing.util.DatabaseConnector;
import com.outgoing.util.PreferenceUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class Tutorial_page extends BaseActivity {
	
	private ListView list;
	private TextView lw_title;
	private String option;
	private DatabaseConnector DBC;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_page);
		list = (ListView) findViewById(R.id.listview1); 
		lw_title = (TextView) findViewById(R.id.lw_title); 
		lw_title.setText("Tutorial");
		
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
						option = ((TextView) view).getText().toString();
						if(option != null && option.length() != 0 && !option.equals("No message found")){
							Intent intent = new Intent(
									Tutorial_page.this,
									Tutorial_content_page.class);
							intent.putExtra("function", option);
							startActivity(intent);
							 
						}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
			ArrayList<String> list_array = new ArrayList<String>();
			String [] type = {"Map Page","Search","Add Marker"};
			for(String tmp : type){
				list_array.add(tmp);
			}
			if(list_array.size()<1){
				list_array.add("No data found");
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.chat_list_item, list_array);
			adapter.notifyDataSetChanged();
			list.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial_page, menu);
		return true;
	}

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		
	}
	
	public void btnHandler(View view) {
		switch (view.getId()) {
		case R.id.back_Button: {
			finish(); 
			break;
		}
		default:
			break;
		}
	}

}
