package com.outgoing.activity;

import java.util.Observable;

import com.outgoing.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Chat_page extends BaseActivity {
	private String phone_no="";
	private TextView phone_panel;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_page);		
		phone_panel = (TextView)findViewById(R.id.phone_panel);
		if(getIntent().getExtras()!=null)
			phone_no = getIntent().getExtras().getString("target_phone");
		phone_panel.setText(phone_no);
	}

	
	public void btnHandler(View view) {
		switch (view.getId()) {
		case R.id.button1:{
			phone_no+="1";
			break;
		}
		case R.id.button2:{
			phone_no+="2";
			break;
		}
		case R.id.button3:{
			phone_no+="3";
			break;
		}
		case R.id.button4:{
			phone_no+="4";
			break;
		}
		case R.id.button5:{
			phone_no+="5";
			break;
		}
		case R.id.button6:{
			phone_no+="6";
			break;
		}
		case R.id.button7:{
			phone_no+="7";
			break;
		}
		case R.id.button8:{
			phone_no+="8";
			break;
		}
		case R.id.button9:{
			phone_no+="9";
			break;
		}
		case R.id.button0:{
			phone_no+="0";
			break;
		}
		case R.id.button10:{
			phone_no+="*";
			break;
		}
		case R.id.button12:{
			phone_no+="#";
			break;
		}
		case R.id.buttonX:{
			if(phone_no.length()>0)
				phone_no = phone_no.substring(0, phone_no.length() - 1);
			break;
		}
		case R.id.back_Button: {
			finish();
			break;
		}
		case R.id.call_button: {
			String uri = "tel:"+phone_no.trim();
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
			startActivity(intent);
			finish();
		}
		default:
			break;
		}
		phone_panel.setText(phone_no);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_page, menu);
		return true;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

}
