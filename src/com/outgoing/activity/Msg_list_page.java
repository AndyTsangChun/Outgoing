package com.outgoing.activity;

import io.socket.SocketIO;

import java.util.ArrayList;
import java.util.Observable;

import com.google.android.gms.maps.model.Marker;
import com.outgoing.R;
import com.outgoing.R.layout;
import com.outgoing.R.menu;
import com.outgoing.http.HttpRequests;
import com.outgoing.model.GeneralMarker;
import com.outgoing.util.DatabaseConnector;
import com.outgoing.util.PreferenceUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Msg_list_page extends BaseActivity {

	private ListView list;
	private TextView lw_title;
	private String option;
	private String my_uID;//my_loginName;
	private DatabaseConnector DBC;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_page);
		list = (ListView) findViewById(R.id.listview1);  
		lw_title = (TextView) findViewById(R.id.lw_title);  
		lw_title.setText("Outgoing Message");
		if(getIntent().getExtras()!=null){
			my_uID = getIntent().getExtras().getString("uID");
			//my_loginName = getIntent().getExtras().getString("loginName");
		}else{
			my_uID = PreferenceUtils.getUID(this);
		}
		DBC = new DatabaseConnector(this);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
						option = ((TextView) view).getText().toString();
						if(option != null && option.length() != 0 && !option.equals("No message found")){
							Intent intent = new Intent(Msg_list_page.this,
									Msg_content_page.class);
							String userName = ((TextView) view).getText().toString();
							intent.putExtra("toUser", my_uID);
							intent.putExtra("fromUser", option);
							Msg_list_page.this.startActivity(intent);	
						}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
			ArrayList<String> list_array = new ArrayList<String>();
			DBC.open();
			my_uID = PreferenceUtils.getUID(this);
			Cursor cMyMessage = DBC.getMyMessage(my_uID);
			if (cMyMessage != null
					&& cMyMessage.getCount() > 0) {
				cMyMessage.moveToLast();
				int indexFromUser = cMyMessage.getColumnIndex("fromUser");
				int indexToUser = cMyMessage.getColumnIndex("toUser");
				int indexType = cMyMessage.getColumnIndex("msgType");
				int indexData = cMyMessage.getColumnIndex("data");
				int indexTime = cMyMessage.getColumnIndex("DateTime");
				do {
					boolean user_exist = false;
					String type =  cMyMessage.getString(indexType); 
					String fromUser =  cMyMessage.getString(indexFromUser); 
					String toUser = cMyMessage.getString(indexToUser); 
					String data =  cMyMessage.getString(indexData); 
					String time =  cMyMessage.getString(indexTime); 	
					if(!fromUser.equals(my_uID)){
						for(int i = 0 ; i<list_array.size();i++){
							if(list_array.get(i).equals(fromUser)){
								user_exist = true;
							}
						}
						if(!user_exist){
							list_array.add(fromUser);
						}
					}
				} while (cMyMessage.moveToPrevious());
			}
			if(list_array.size()<1){
				list_array.add("No message found");
			}
			DBC.close();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.chat_list_item, list_array);
			adapter.notifyDataSetChanged();
			list.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.msg_list_page, menu);
		return true;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public void btnHandler(View view) {
		switch (view.getId()) {
		case R.id.back_Button: {
			finish();
			break;
		}
		}
	}
}
