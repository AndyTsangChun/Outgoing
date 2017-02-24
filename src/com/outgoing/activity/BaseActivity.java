package com.outgoing.activity;

import java.util.Observer;

import com.outgoing.R;
import com.outgoing.util.PreferenceUtils;
import com.outgoing.activity.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public abstract class BaseActivity extends Activity implements Observer {

	public static final int DIALOG_EXIT = 0;
	public static final int DIALOG_MARKER_TYPE = 1;
	public static final int DIALOG_LOGIN_FAIL = 2;
	public static final int DIALOG_CONNECTION_FAILURE = 3;
	public static final int DIALOG_NO_EVENT = 4;
	public static final int DIALOG_USER_UPDATED = 5;
	public static final int DIALOG_SERVICE_NOT_AVAILABLE = 6;
	public static final int DIALOG_NO_SUCH_DATA = 7;
	public static final int DIALOG_MARKER_ADDED = 8;
	public static final int DIALOG_MARKER_NOT_EXISTS = 9;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String message;
			switch (id) {
			case DIALOG_MARKER_TYPE:
				final String[] type = {  "Flag", "Spot" };
				String userOption = PreferenceUtils
						.getUserOption(getApplicationContext());
				String[] tmp = userOption.split(";");
				int choice = -1;
				if(tmp.length>1 && tmp[1]!="" && tmp[1]!=null)
					choice = Integer.parseInt(tmp[1]);
				return builder
						.setTitle("Choose Marker Type")
						.setSingleChoiceItems(type, choice,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getApplicationContext(), type[item],
                                Toast.LENGTH_SHORT).show();
                        markerType(item);
                    }
                }).create();

			case DIALOG_EXIT:
				return builder
						.setTitle("System Message")
						.setMessage("Do you really wish to leave?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										finish();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// do nothing
									}
								}).create();
			case DIALOG_LOGIN_FAIL:
				message = "Invalid user name or password! Please try again!";
				break;
			case DIALOG_CONNECTION_FAILURE:
				message = "Connection Error! Please try again later!";
				break;				
			case DIALOG_NO_EVENT:
				message = "No event!";
				break;
			case DIALOG_USER_UPDATED:
				message = "Information has been updated.";
				break;
			case DIALOG_SERVICE_NOT_AVAILABLE:
				message = "Service not available!";
				break;
			case DIALOG_NO_SUCH_DATA:
				message = "No such data!";
				break;
			case DIALOG_MARKER_ADDED:
				message = "Marker Added!";
				break;
			case DIALOG_MARKER_NOT_EXISTS:
				message = "Marker Not Exists";
				break;
			default:
				message = "Empty";
				break;
			}
			return builder
					.setTitle("System Message")
					.setMessage(
							message)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	/*
	 * @Override public void onBackPressed() { // do nothing. }
	 */
	
	public void markerType(int item){
		String userOption = PreferenceUtils
				.getUserOption(getApplicationContext());
		System.out.println(userOption);
		String[] tmp = userOption.split(";");
		userOption = tmp[0]+";"+item;
		for(int i = 2 ; i < tmp.length ; i++){
				userOption += ";"+tmp[i];
		}
		
		PreferenceUtils
			.setUserOption(getApplicationContext(),userOption);
	}
	
}
