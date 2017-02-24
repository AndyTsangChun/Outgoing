package com.outgoing.activity;

import java.util.ArrayList;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.outgoing.R;
import com.outgoing.http.HttpRequests;
import com.outgoing.model.GeneralPopupWindow;
import com.outgoing.model.StatusUpdate;
import com.outgoing.util.PreferenceUtils;

public class Setting_page extends BaseActivity {

	private ListView setting_listview;
	private String setting_option, eId, uType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_page);
		eId = getIntent().getExtras().getString("eId");
		uType = getIntent().getExtras().getString("uType");
		setting_listview = (ListView) findViewById(R.id.listview1);  
		System.out.println("EID2="+eId);
		
		setting_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
						setting_option = ((TextView) view).getText().toString();
						if(setting_option != null && setting_option.length() != 0){
							if(setting_option.equals("Log Out")) {
								PreferenceUtils.setIsLoggedIn(getApplicationContext(), false);
								Intent intent = new Intent(Setting_page.this,
										Login_page.class);
								startActivity(intent);
							    if(Map_page.instance != null) {
							        try {  
							        	Map_page.instance.stopHandler();
							        	Map_page.instance.finish(); 
							        } catch (Exception e) {}
							    }
								finish();
							}		
							if(setting_option.equals("View Event")) {
								if(eId != null || !eId.equals("")){
									HttpRequests request = new HttpRequests(Setting_page.this, null,
											getApplicationContext());
									request.execute("get_event_info_request",eId);
								}else{
									/*Intent intent = new Intent(Setting_page.this,
											EventList_page.class);
									startActivity(intent);*/
									showDialog(DIALOG_NO_EVENT);
								}
							}
							if(setting_option.equals("Marker Type")) {
								System.out.println("choosing marker type");
								showDialog(DIALOG_MARKER_TYPE);
							}
							if(setting_option.equals("Add Marker")) {
								Intent intent = new Intent(Setting_page.this,
										AddMarker_page.class);
								startActivity(intent);
								finish();
							}
						}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
			ArrayList<String> list_array = new ArrayList<String>();
			String[] tmp = {"Marker Type","View Event","Add Marker","Log Out"};
			String[] tmp2 = {"Marker Type","Log Out"};
			System.out.println(uType);
			if(!uType.equals("leader")){
				for (String h : tmp2) {
					list_array.add(h);
				}
			}else{
				for (String h : tmp) {
					list_array.add(h);
				}
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.setting_list_item, list_array);
			setting_listview.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting_page, menu);
		return true;
	}
	
	@Override
	public void update(Observable observable, Object data) {
		if (!(data instanceof StatusUpdate)) {
			return;
		}
		StatusUpdate statusUpdate = (StatusUpdate) data;
		try {
			switch (statusUpdate.getUpdateType()) {
			case get_event_info_request : {
				JSONArray respond = statusUpdate.getPayload();
				String EventNo, EventName, EventPlace, EventDate, TotalNum, EventDesc;
				EventNo = ""+((JSONArray) respond.get(0)).get(0);
				EventName = ""+((JSONArray) respond.get(0)).get(1);
				EventDesc = ""+((JSONArray) respond.get(0)).get(2);
				EventDate = ""+((JSONArray) respond.get(0)).get(3);
				TotalNum = ""+((JSONArray) respond.get(0)).get(4);
				EventPlace = ""+((JSONArray) respond.get(0)).get(5);
				
				Intent intent = new Intent(Setting_page.this,
						ViewEvent_page.class);
				intent.putExtra("EventNo", "" + EventNo);
				intent.putExtra("EventName", "" + EventName);
				intent.putExtra("EventDesc", "" + EventDesc);
				intent.putExtra("EventDate", "" + EventDate);
				intent.putExtra("TotalNum ", "" + TotalNum );
				intent.putExtra("EventPlace", "" + EventPlace);
				startActivity(intent);
			}			
			default:
				break;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
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
