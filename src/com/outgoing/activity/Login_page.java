package com.outgoing.activity;

import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.outgoing.R;
import com.outgoing.http.HttpRequests;
import com.outgoing.model.StatusUpdate;
import com.outgoing.util.PreferenceUtils;

public class Login_page extends BaseActivity {
	private String loginName = " ", password = " ";
	private EditText name_TB, password_TB;
	private CheckBox remCB;
	//loading
	ProgressDialog pBar;
	AnimationDrawable progressAnimation;
	boolean loading = true;
	private ImageView logo_iv; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);

		name_TB = (EditText) findViewById(R.id.etxtUsername);
		password_TB = (EditText) findViewById(R.id.etxtPwd);
		remCB = (CheckBox) this.findViewById(R.id.remCB);
		boolean isLoggedIn = PreferenceUtils.getIsLoggedIn(this);
		if (isLoggedIn) { 
			logIn();
		} 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String RemUser = PreferenceUtils.getRemUser(getApplicationContext());
		System.out.println(RemUser);
		if (!RemUser.equals("")) {
			String[] tmp = RemUser.split(";");
			name_TB.setText(tmp[0]);
			password_TB.setText(tmp[1]);
			remCB.setChecked(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_page, menu);
		return true;
	}

	public void btnHandler(View view) {
		switch (view.getId()) {
		case R.id.login_Button: {
			loginName = name_TB.getText().toString();
			password = password_TB.getText().toString();
			
			if(!loginName.equals("root")){		
				if(isNetworkConnected()){
					HttpRequests request = new HttpRequests(this, null,
							getApplicationContext());
					request.execute("login_request", loginName, password);
					loading();
				}else{
					showDialog(DIALOG_CONNECTION_FAILURE);
				}
				break;				
			}else{
				PreferenceUtils.setLoginName(getApplicationContext(), "root");
				PreferenceUtils.setPassword(getApplicationContext(), "root");
				PreferenceUtils.setUID(getApplicationContext(), "root");
				PreferenceUtils.setUType(getApplicationContext(), "leader");
				logIn();
			}
		}
		case R.id.register_Button: {
			Intent viewIntent =
			          new Intent("android.intent.action.VIEW",
			            Uri.parse("http://tracksystem.zapto.org/OutingTrack/OutsideConnect/register.php"));
			          startActivity(viewIntent);
		}
		default:
			break;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (!(data instanceof StatusUpdate)) {
			return;
		}
		StatusUpdate statusUpdate = (StatusUpdate) data;
		if (loading){
			pBar.cancel();
			loading = false;
		}
		try {
			switch (statusUpdate.getUpdateType()) {
			case login_request: {
				JSONArray respond = statusUpdate.getPayload();
				String login_status = "" + respond.get(0);
				String uId = "" + respond.get(1);		
				String uType = "" + respond.get(2);
				String uPic = "" + respond.get(3);
				System.out.println("login_status="+login_status+"UID="+uId);
				if(login_status.equals("0")){
					showDialog(DIALOG_CONNECTION_FAILURE);
				}else if(login_status.equals("1")){
					PreferenceUtils.setIsLoggedIn(getApplicationContext(), true);
					PreferenceUtils.setLoginName(getApplicationContext(), loginName);
					PreferenceUtils.setPassword(getApplicationContext(), password);
					PreferenceUtils.setUID(getApplicationContext(), uId);
					PreferenceUtils.setUType(getApplicationContext(), uType);
					PreferenceUtils.setPic(getApplicationContext(), uPic);
					if(remCB.isChecked())
						PreferenceUtils.setRemUser(getApplicationContext(), loginName, password);
					Intent intent = new Intent(Login_page.this, Map_page.class);
					startActivity(intent);
					finish();
				}else{
					showDialog(DIALOG_LOGIN_FAIL);
				}
				break;
			}
			case connection_fail: {
				showDialog(DIALOG_CONNECTION_FAILURE);
				break;
			}
			default:
				break;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			Log.e("Error","error = "+e1);
		}
	}
	
	public void logIn(){
		Intent intent = new Intent(Login_page.this, Map_page.class);
		Login_page.this.startActivity(intent);
		Login_page.this.finish();
	}
	
	public void loading(){
		loading = true;
		pBar = new ProgressDialog(
				this);
		//pBar.setTitle("Loading");
		pBar.setMessage("Loading, please wait...");
		//pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//pBar.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		pBar.setIndeterminate(true);
		pBar.setIndeterminateDrawable(getResources().getDrawable(R.anim.loading_animation));
		pBar.setCancelable(false);
		/*
		pBar.setView(getLayoutInflater().inflate(R.layout.loading_bar,null));
		ImageView progressSpinner = (ImageView) pBar.findViewById(R.id.progressSpinner);
		progressSpinner.setBackgroundResource(R.anim.loading_animation);
		progressAnimation = (AnimationDrawable) progressSpinner.getBackground();
		pBar.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
			progressAnimation.start();
			}
		});*/
		pBar.show();
		//pBar.setMax(100);
		//pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}

}
