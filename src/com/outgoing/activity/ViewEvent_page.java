package com.outgoing.activity;

import java.util.Observable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.outgoing.R;
import com.outgoing.R.layout;
import com.outgoing.R.menu;
import com.outgoing.model.GeneralPopupWindow;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ViewEvent_page extends BaseActivity {
	private TextView EventNo_TB, EventName_TB, EventPlace_TB, EventDate_TB, TotalNum_TB, EventDesc_TB;
	private String EventNo, EventName, EventPlace, EventDate, TotalNum, EventDesc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_event_page);
		EventNo_TB = (TextView) findViewById(R.id.EventNo_TB);  
		EventName_TB = (TextView) findViewById(R.id.EventName_TB);  
		EventPlace_TB = (TextView) findViewById(R.id.EventPlace_TB);  
		EventDate_TB = (TextView) findViewById(R.id.EventDate_TB);  
		TotalNum_TB = (TextView) findViewById(R.id.TotalNum_TB);  
		EventDesc_TB = (TextView) findViewById(R.id.EventDesc_TB);  
		
		EventNo = getIntent().getExtras().getString("EventNo");
		EventName = getIntent().getExtras().getString("EventName");
		EventPlace = getIntent().getExtras().getString("EventDesc");
		EventDate = getIntent().getExtras().getString("EventDate");
		TotalNum = getIntent().getExtras().getString("TotalNum ");
		EventDesc = getIntent().getExtras().getString("EventPlace");
		
		EventNo_TB.setText(EventNo);
		EventName_TB.setText(EventName);
		EventPlace_TB.setText(EventPlace);
		EventDate_TB.setText(EventDate);
		TotalNum_TB.setText(TotalNum);
		EventDesc_TB.setText(EventDesc);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_event_page, menu);
		return true;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub		
	}

}
