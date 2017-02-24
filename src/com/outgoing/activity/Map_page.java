package com.outgoing.activity;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.outgoing.R;
import com.outgoing.http.HttpRequests;
import com.outgoing.http.ImageLoader;
import com.outgoing.model.GeneralMarker;
import com.outgoing.model.GeneralPopupWindow;
import com.outgoing.model.StatusUpdate;
import com.outgoing.util.DatabaseConnector;
import com.outgoing.util.PreferenceUtils;

public class Map_page extends BaseActivity {
	static LatLng MyLocation;
	private Marker myPosition;
	private Marker[] maps_marker;
	static double latitude;
	static double longitude;
	private Handler handler = new Handler();
	private String provider, myPhoto;
	private HorizontalScrollView bottombar;
	public static GoogleMap map;
	private String eId = "E0000";
	private DatabaseConnector DBC = new DatabaseConnector(this);
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 30 s
	private Location myLocation; // location
	private LocationManager locationManager = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss.SSSZ");
	// Other Variables
	private String uID, userName, uGender, uPhone, uEmail, uType,
			current_function = null; 
	// Activity variable
	public static Map_page instance = null;
	// sos pop
	private RelativeLayout layout_r;
	private TextView sos_TB;
	// user pop
	private EditText userName_TB, userPhone_TB, userEmail_TB;
	private RadioButton f_rb;
	// auto trace variable
	private String auto_trace;
	// sos
	private String sos_request = null;
	private ImageView alert;
	private Animation animAlpha;
	// loading
	ProgressDialog pBar;
	AnimationDrawable progressAnimation;
	boolean loading = false;
	// menu drawer
	boolean infow_status = false;
	boolean setting_status = false;
	// setting menu
	private ListView setting_listview;
	private String setting_option;
	// display
	int width, height;
	// infow option
	String target_uid = null;
	boolean target_is_user = true;
	// search user
	public static double s_lo, s_la;
	//pic download 
	public ImageLoader imageLoader;
	//MSG
	private static SocketIO socket = null;
	private boolean onPaused = false;
	IOAcknowledge ack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_page);

		bottombar = (HorizontalScrollView) findViewById(R.id.bottombar);
		bottombar.setHorizontalScrollBarEnabled(false);
		alert = (ImageView) findViewById(R.id.alert);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		uID = PreferenceUtils.getUID(this);
		userName = targetUserInfo(uID,"name");
		uType = PreferenceUtils.getUType(this);

		// sos
		animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_alert);

		// get latest location
		String RemLoc = PreferenceUtils.getRemLoc(getApplicationContext());
		if (!RemLoc.equals("")) {
			String[] tmp = RemLoc.split(";");
			latitude = Double.parseDouble(tmp[0]);
			longitude = Double.parseDouble(tmp[1]);
		}
		// get pic
		String RemPic = PreferenceUtils.getPic(getApplicationContext());
		if (!RemPic.equals("")) {
			myPhoto = RemPic;
		}
		
		// set a timer to get refresh location
		handler.removeCallbacks(getMyLocation);
		handler.postDelayed(getMyLocation, 0);

		// Move the camera instantly to NKUT with a zoom of 16.
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		refreshMyLocation();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 18));
		// map.getUiSettings().setZoomControlsEnabled(false);
		map.setPadding(0, 140, 0, 130);

		// <<infow menu start
		// Setting a custom info window adapter for the google map
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		map.setOnMarkerClickListener(new CustomOnMarkerClickListener());
		// Adding and showing marker while touching the GoogleMap
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				replaceInfoWindow();
			}
		});
		// infow menu ends>>

		// setting menu
		setting_listview = (ListView) findViewById(R.id.setting_menu_listview);
		getDisplaySize();

		//ImageView test = (ImageView) findViewById(R.id.test);
		//loadPic(test);
		//if(socket==null)
		new Thread(new ClientThread("119.246.80.6","8124")).start();
		//new Thread(new ClientThread("172.19.127.160","8124")).start();
		ack = new IOAcknowledge() {
		    @Override
		    public void ack(Object... args) {
		        if (args.length > 0) {
		            Log.d("SocketIO", "" + args[0]);
		            // -> "hello"
		        }
		    }
		};
	}

	@SuppressLint("NewApi")
	private void getDisplaySize() {
		// get display size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_page, menu);
		return true;
	}
	
	public void btnHandler(View view) {
		switch (view.getId()) {
		case R.id.ms_Normal_Button: {
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			findViewById(R.id.ms_Normal_Button).setVisibility(View.INVISIBLE);
			findViewById(R.id.ms_Satelite_Button).setVisibility(View.VISIBLE);
			break;
		}
		case R.id.ms_Satelite_Button: {
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			findViewById(R.id.ms_Satelite_Button).setVisibility(View.INVISIBLE);
			findViewById(R.id.ms_Hybrid_Button).setVisibility(View.VISIBLE);
			break;
		}
		case R.id.ms_Hybrid_Button: {
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			findViewById(R.id.ms_Hybrid_Button).setVisibility(View.INVISIBLE);
			findViewById(R.id.ms_Normal_Button).setVisibility(View.VISIBLE);
			break;
		}
		case R.id.setting_Button: {
			instance = this;

			if (!setting_status) {
				if (target_is_user) {
					moveSettingWindow();
				}
				setting_listview
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								setting_option = ((TextView) view).getText()
										.toString();
								if (setting_option != null
										&& setting_option.length() != 0
										&& setting_status) {
									if (setting_option.equals("Log Out")) {
										PreferenceUtils.setIsLoggedIn(
												getApplicationContext(), false);
										Intent intent = new Intent(
												Map_page.this, Login_page.class);
										startActivity(intent);
										if (Map_page.instance != null) {
											try {
												instance.stopHandler();
												instance.finish();
											} catch (Exception e) {
											}
										}
										finish();
									}
									if (setting_option.equals("View Event")) {
										System.out.println("eId=" + eId);
										if (eId != null && !eId.equals("E0000")) {
											if (!eId.equals("")) {
												HttpRequests request = new HttpRequests(
														Map_page.this, null,
														getApplicationContext());
												request.execute(
														"get_event_info_request",
														eId);
											} else {
												showDialog(DIALOG_NO_EVENT);
											}
										} else {
											/*
											 * Intent intent = new
											 * Intent(Setting_page.this,
											 * EventList_page.class);
											 * startActivity(intent);
											 */
											showDialog(DIALOG_NO_EVENT);
										}
									}
									if (setting_option.equals("Marker Type")) {
										System.out
												.println("choosing marker type");
										showDialog(DIALOG_MARKER_TYPE);
									}
									if (setting_option.equals("Tutorial")) {
										Intent intent = new Intent(
												Map_page.this,
												Tutorial_page.class);
										startActivity(intent);
									}
									if (setting_option.equals("Add Marker")) {
										Intent intent = new Intent(
												Map_page.this,
												AddMarker_page.class);
										intent.putExtra("eventNo", eId);
										startActivity(intent);
									}
									replaceSettingWindow();
								}
							}
						});
			} else {
				replaceSettingWindow();
			}
			break;
		}
		case R.id.refresh_Button: {
			current_function = "refresh_map";
			get_user_info();
			break;
		}
		case R.id.user_status_Button: {
			int y = findViewById(R.id.user_status_Button).getBottom() * 3 / 2;
			int x = width / 10;
			GeneralPopupWindow.userStatus_PopupWindow(x, y, this, uID, true);
			break;
		}
		case R.id.auto_trace_Button: {
			auto_Trace();
			break;
		}
		case R.id.search_Button: {
			int y = height;
			int x = width;
			replaceInfoWindow();
			GeneralPopupWindow.search_PopupWindow(x, y, this);
			break;
		}
		case R.id.msg_Button: {
			Intent intent = new Intent(Map_page.this, Msg_list_page.class);
			intent.putExtra("uID", uID);
			intent.putExtra("loginName", userName);
			this.startActivity(intent);  
			break;
		}
		case R.id.chat_Button: {
			Intent intent = new Intent(Map_page.this, Chat_page.class);
			this.startActivity(intent);
			break;
		}
		case R.id.filter_Button: {
			int y = view.getBottom() * 3 / 2;
			int x = getWindowManager().getDefaultDisplay().getWidth() / 5;
			GeneralPopupWindow.filter_PopupWindow(x, y, this);
			break;
		}
		case R.id.sos_Button: {
			int y = 0;
			int x = getWindowManager().getDefaultDisplay().getWidth() / 10;
			GeneralPopupWindow.sos_PopupWindow(x, y, this);
			break;
		}
		case R.id.h_sos_Button: {
			layout_r = GeneralPopupWindow.layout_r;
			sos_TB = ((TextView) layout_r.findViewById(R.id.sos_TB));
			String sos_status = PreferenceUtils.getSOS(this);
			if (sos_status.equals("OFF") || sos_status.equals("")
					|| sos_status == null) {
				PreferenceUtils.setSOS(this, "ON");
				sos_TB.setText("ON");
				HttpRequests request = new HttpRequests(Map_page.this, null,
						getApplicationContext());
				uType = "emergency";
				uID = PreferenceUtils.getUID(this);
				request.execute("update_user_location_request", uID, ""
						+ latitude, "" + longitude, uType);
				loading();
				loading = true;
				sos_request = "y";
				alert.startAnimation(animAlpha);
				alert.setVisibility(View.VISIBLE);
			} else {
				PreferenceUtils.setSOS(this, "OFF");
				sos_TB.setText("OFF");
				uType = PreferenceUtils.getUType(this);
				alert.clearAnimation();
				alert.setVisibility(View.GONE);
			}
			break;
		}
		case R.id.close_button: {
			GeneralPopupWindow.popupWindow.dismiss();
			break;
		}
		case R.id.save_button: {
			layout_r = GeneralPopupWindow.layout_r;
			userName_TB = ((EditText) layout_r.findViewById(R.id.UserName_TB));
			userPhone_TB = ((EditText) layout_r.findViewById(R.id.UserPhone_TB));
			userEmail_TB = ((EditText) layout_r.findViewById(R.id.UserEmail_TB));
			f_rb = ((RadioButton) layout_r.findViewById(R.id.female_rb));

			if (f_rb.isChecked())
				uGender = "F";
			else
				uGender = "M";
			userName = "" + userName_TB.getText();
			uPhone = "" + userPhone_TB.getText();
			uEmail = "" + userEmail_TB.getText();

			HttpRequests request = new HttpRequests(Map_page.this, null,
					getApplicationContext());
			request.execute("update_user_info_request", uID, userName, uPhone,
					uEmail, uGender);
			loading();
			loading = true;
			break;
		}
		case R.id.infow_button_trace: {
			if (infow_status) {
				System.out.println("trace=" + uID);
			}
			break;
		}
		case R.id.infow_button_chat: {
			if (infow_status) {
				String p = targetUserInfo(target_uid, "phone");
				System.out.println(target_uid + ";" + "phone");
				if (checkReturnData(p)) {
					Intent intent = new Intent(Map_page.this, Chat_page.class);
					intent.putExtra("target_phone", p);
					this.startActivity(intent);
				}
			}
			break;
		}
		case R.id.infow_button_msg: {
			if (infow_status) {
				if(!target_uid.equals(uID)){
					Intent intent = new Intent(Map_page.this,
							Msg_content_page.class);
					intent.putExtra("fromUser", target_uid);
					intent.putExtra("toUser", uID);
					this.startActivity(intent);
				}
			}
			break;
		}
		case R.id.infow_button_details: {
			if (infow_status) {
				int y = findViewById(R.id.user_status_Button).getBottom() * 3 / 2;
				int x = width / 10;
				GeneralPopupWindow.userStatus_PopupWindow(x, y, this,
						target_uid, false);
			}
			break;
		}
		case R.id.search_trace_button: {
			final LatLng s_Location = new LatLng(s_la,s_lo);
			if(s_la == 0 || s_lo == 0){
				showDialog(DIALOG_MARKER_NOT_EXISTS);
			}else{
			runOnUiThread(new Runnable(){
				public void run() {
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(s_Location,18));
			}});
			GeneralPopupWindow.popupWindow.dismiss();
			}
		} 
		case R.id.trace_me_Button: {
			refreshMyLocation();
			// Move the camera instantly to NKUT with a zoom of 16.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 18));
			break;
		}
		default:
			break;
		}
	}

	public void auto_Trace() {
		String userOption = PreferenceUtils
				.getUserOption(getApplicationContext());
		String[] tmp = userOption.split(";");
		String auto_status = "";
		if (tmp.length > 0) {
			if (tmp[0].equals("y")) {
				findViewById(R.id.auto_trace_Button).setBackgroundResource(
						R.drawable.mbutton_auto_trace);
				auto_status = "n";
				Toast.makeText(this, "Auto Trace Disabled", Toast.LENGTH_SHORT)
						.show();
			} else {
				findViewById(R.id.auto_trace_Button).setBackgroundResource(
						R.drawable.mbutton_auto_trace_pressed);
				auto_status = "y";
				Toast.makeText(this, "Auto Trace Enabled", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			auto_status = "n";
		}
		auto_trace = auto_status;
		for (int i = 1; i < tmp.length; i++) {
			auto_status += ";" + tmp[i];
		}
		PreferenceUtils.setUserOption(getApplicationContext(), auto_status);
	}

	public void stopHandler() {
		handler.removeCallbacks(getMyLocation);
	}

	public void refreshMyLocation() {
		// GPS location part
		String type = "own";
		MyLocation = new LatLng(latitude, longitude);
		GeneralMarker myMarker = new GeneralMarker(latitude, longitude, uID,
				userName, type, this, myPhoto);
		if (myPosition != null) {
			myPosition.remove();
		}
		myPosition = map.addMarker(myMarker.get_Marker());
		replaceInfoWindow();
	}

	private Runnable getMyLocation = new Runnable() {
		public void run() {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
			System.out.println("Provider="+LocationManager.GPS_PROVIDER+":"+LocationManager.NETWORK_PROVIDER);
			handler.postDelayed(this, 20000);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		onPaused = false;
		String userOption = PreferenceUtils
				.getUserOption(getApplicationContext());
		String[] tmp = userOption.split(";");
		if (tmp.length > 0) {
			if (tmp[0].equals("y")) {
				findViewById(R.id.auto_trace_Button).setBackgroundResource(
						R.drawable.mbutton_auto_trace_pressed);
				auto_trace = tmp[0];
			} else {
				findViewById(R.id.auto_trace_Button).setBackgroundResource(
						R.drawable.mbutton_auto_trace);
				auto_trace = tmp[0];
			}
		}

		get_user_info();
		refreshMyLocation();
		current_function = "inti";
		replaceInfoWindow();
		replaceSettingWindow();

		// setting menu
		ArrayList<String> setting_list_array = new ArrayList<String>();
		String[] setting_la = { "Marker Type", "View Event", "Add Marker", "Tutorial",
				"Log Out" };
		String[] setting_la2 = { "Marker Type", "Tutorial", "Log Out" };
		System.out.println(uType);
		if (!uType.equals("leader")) {
			for (String h : setting_la2) {
				setting_list_array.add(h);
			}
		} else {
			for (String h : setting_la) {
				setting_list_array.add(h);
			}
		}

		ArrayAdapter<String> setting_adapter = new ArrayAdapter<String>(this,
				R.layout.setting_list_item, setting_list_array);
		try {
			setting_listview.setAdapter(setting_adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//re-on socket if null
		if(socket == null)
			//new Thread(new ClientThread("172.19.127.160","8124")).start();
			new Thread(new ClientThread("119.246.80.6","8124")).start();
		Log.e("onResume", "onResume");
	}

	@Override
	protected void onPause() {
		replaceInfoWindow();
		super.onPause();
		onPaused = true;
		Log.e("onPause", "onPause");

		locationManager.removeUpdates(listener);
	}

	public static void disconnectSocket(){
		if(socket!= null){
			socket.disconnect();
			socket = null;
		}
	}
	
	private void get_user_info() {
		if (isNetworkConnected()) {
			HttpRequests request = new HttpRequests(this, null,
					getApplicationContext());
			request.execute("get_marker_request");
		} else {
			showDialog(DIALOG_CONNECTION_FAILURE);
			current_function = null;
		}
	}

	LocationListener listener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			System.out.println("location changed");
			if (isBetterLocation(location, myLocation)) {
				provider = location.getProvider();
				// 位置的准確性
				float accuracy = location.getAccuracy();
				// 高度信息
				double altitude = location.getAltitude();
				// 方向角
				float bearing = location.getBearing();
				// 速度 米/秒
				float speed = location.getSpeed();

				String locationTime = sdf.format(new Date(location.getTime()));
				String currentTime = null;

				if (myLocation != null) {
					currentTime = sdf.format(new Date(myLocation.getTime()));
					myLocation = location;

				} else {
					myLocation = location;
				}
				PreferenceUtils.setRemLoc(getApplicationContext(), ""
						+ latitude, "" + longitude);
				if (uID != null && !uID.equals("") && !uID.equals("null")
						&& uType != null && !uType.equals("")
						&& !uType.equals("null")) {
					HttpRequests request = new HttpRequests(Map_page.this,
							null, getApplicationContext());
					String sos_status = PreferenceUtils.getSOS(Map_page.this);
					if (sos_status.equals("ON"))
						uType = "emergency";
					request.execute("update_user_location_request", uID, ""
							+ latitude, "" + longitude, uType);
				} else {
					System.out.println("no uId or uType");
				}
				if (latitude != 0 && longitude != 0) {
					refreshMyLocation();
				}
			} else {
				System.out.println("Current location is the best");
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e("onStatusChanged", "onStatusChanged: " + provider);

		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.e("onProviderEnabled", "onProviderEnabled: " + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e("onProviderDisabled", "onProviderDisabled: " + provider);
		}
	};

	private static final int TWO_MINUTES = 1000 * 1 * 2;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			System.out.println("Current location is null");
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
 
	@Override
	public void update(Observable observable, Object data) {
		if (!(data instanceof StatusUpdate)) {
			return;
		}
		StatusUpdate statusUpdate = (StatusUpdate) data;

		try {
			switch (statusUpdate.getUpdateType()) {
			case get_user_info_request: {
				JSONArray respond = statusUpdate.getPayload();
				// adding user data to sqlite
				String uID = "", uName = "", uPhone = "", uEmail = "", uGender = "", uType = "", eId = "", temLongitude = "", temLatitude = "", uPhoto = "";
				for (int i = 0; i < respond.length(); i++) {
					for (int k = 0; k < ((JSONArray) respond.get(i)).length(); k++) {
						String r = "" + ((JSONArray) respond.get(i)).get(k);
						switch (k) {
						case 0:
							uID = r;
							break;
						case 1:
							uName = r;
							break;
						case 2:
							uPhone = r;
							break;
						case 3:
							uEmail = r;
							break;
						case 4:
							uGender = r;
							break;
						case 5:
							uType = r;
							break;
						case 6:
							uPhoto = r;
							break;
						case 7:
							temLongitude = r;
							break;
						case 8:
							temLatitude = r;
							break;
						case 9:
							eId = r;
							if (uID.equals(this.uID)){
								if(eId.equals("null")){
									this.eId = "E0000";
								} else {
									this.eId = eId;
								}
							}
							break;						
						default:
							break;
						}
					}
					DBC.open();
					Cursor abc = DBC.getOneUserInfo(uID);
					boolean found_status = false;
					if (abc.getCount() <= 0) {
						found_status = true;
					}
					if (found_status) {
						DBC.insertUserInfo(uID, uName, uPhone, uEmail, uGender,
								uType, temLatitude, temLongitude, eId, uPhoto);
						//System.out.println(uID+":"+ uName+","+ uPhone+","+ uEmail+","+ uGender+","+
								//uType+","+ temLatitude+","+ temLongitude+","+ eId+","+ uPhoto);
					} else {
						DBC.updateUserInfo(uID, uName, uPhone, uEmail, uGender,
								uType, temLatitude, temLongitude, eId, uPhoto);
						//System.out.println(uID+":"+ uName+","+ uPhone+","+ uEmail+","+ uGender+","+
							//	uType+","+ temLatitude+","+ temLongitude+","+ eId+","+ uPhoto);
					}
					// intialize variable
					uID = uName = uPhone = uEmail = uGender = uType = eId = temLongitude = temLatitude = "";
				}
				uID = PreferenceUtils.getUID(getApplicationContext());
				Cursor cUInfo = DBC.getOneUserInfo(uID);
				if (cUInfo != null && cUInfo.getCount() > 0) {
					cUInfo.moveToFirst();
					int indexUName = cUInfo.getColumnIndex("uName");
					userName = cUInfo.getString(indexUName);
				}
				// get user data
				DBC.open();
				int count = 0;
				Cursor cAllLocation = DBC.getAllUserLocation(this.eId);
				Cursor cAllMarkerLocation = DBC.getAllMarkerLocation(this.eId);
				int numberOfMarker = cAllLocation.getCount() + cAllMarkerLocation.getCount();
				
				if (maps_marker != null && maps_marker.length > 0) {
					for (int i = 0; i < maps_marker.length; i++) {
						if (maps_marker[i] != null)
							maps_marker[i].remove();
					}
				}
				// get marker data
				System.out.println("Marker geted = "+cAllMarkerLocation.getCount());
				if (cAllMarkerLocation != null
						&& cAllMarkerLocation.getCount() > 0) {
					cAllMarkerLocation.moveToFirst();
					int indexmId = cAllMarkerLocation.getColumnIndex("mId");
					int indexmName = cAllMarkerLocation
							.getColumnIndex("mName");
					int indexmType = cAllMarkerLocation.getColumnIndex("mType");
					int indexmLong = cAllMarkerLocation
							.getColumnIndex("mlongitude");
					int indexmLat = cAllMarkerLocation
							.getColumnIndex("mlatitude");
					int indexDesc = cAllMarkerLocation
							.getColumnIndex("mDesc");
					
					maps_marker = new Marker[numberOfMarker - 1];

					String filterOption = PreferenceUtils
							.getFilterOption(this);
					String[] tmp = filterOption.split(";");
					if (filterOption != null && !filterOption.equals("")) {

						String assembly_status = tmp[3], station_status = tmp[4];
						do {
							boolean filtered = true;
							String type =  cAllMarkerLocation
									.getString(indexmType); 
							if (assembly_status.equals("y")
									&& type.equals("assembly"))
								filtered = false;
							if (station_status.equals("y")
									&& type.equals("station"))
								filtered = false;
							if (!filtered) {
								String getLa = cAllMarkerLocation
										.getString(indexmLat), getLo = cAllMarkerLocation
										.getString(indexmLong);
								if (!getLa.equals("")
										&& !getLa.equals("null")
										&& getLa != null
										&& !getLo.equals("")
										&& !getLo.equals("null")
										&& getLo != null) {
									double other_la = Double
											.parseDouble(getLa), other_lo = Double
											.parseDouble(getLo);
									String other_Id = cAllMarkerLocation
											.getString(indexmId), other_Name = cAllMarkerLocation
											.getString(indexmName), mDesc = cAllMarkerLocation
											.getString(indexDesc);
									GeneralMarker new_Marker = new GeneralMarker(
											other_la, other_lo, other_Id,
											other_Name, type, mDesc, this);
									maps_marker[count] = map
											.addMarker(new_Marker
													.get_Marker());
									count++;
								}
							}
							
						} while (cAllMarkerLocation.moveToNext());
					}
				}
				if (cAllLocation != null && cAllLocation.getCount() > 0) {
					cAllLocation.moveToFirst();
					int indexUId = cAllLocation.getColumnIndex("uId");
					int indexUName = cAllLocation.getColumnIndex("uName");
					int indexLong = cAllLocation.getColumnIndex("longitude");
					int indexLat = cAllLocation.getColumnIndex("latitude");
					int indexType = cAllLocation.getColumnIndex("uType");
					int indexPic = cAllLocation.getColumnIndex("uPhoto");

					String filterOption = PreferenceUtils.getFilterOption(this);
					String[] tmp = filterOption.split(";");
					if (filterOption != null && !filterOption.equals("")) {
						String all_status = tmp[0], other_status = tmp[1], leader_status = tmp[2], assembly_status = tmp[3], station_status = tmp[4];

						do {
							boolean filtered = true;
							String type = cAllLocation.getString(indexType);
							if (all_status.equals("y"))
								filtered = false;
							if (other_status.equals("y")
									&& type.equals("member"))
								filtered = false;
							if (leader_status.equals("y")
									&& type.equals("leader"))
								filtered = false;
							if (!filtered) {
								this.uID = PreferenceUtils.getUID(this);
								String getLa = cAllLocation.getString(indexLat), getLo = cAllLocation
										.getString(indexLong), u_pic = cAllLocation.getString(indexPic);
								if (!getLa.equals("") && !getLa.equals("null")
										&& getLa != null && !getLo.equals("")
										&& !getLo.equals("null")
										&& getLo != null) {
									double other_la = Double.parseDouble(getLa), other_lo = Double
											.parseDouble(getLo);
									String other_Id = cAllLocation
											.getString(indexUId);
									String other_Name = cAllLocation
											.getString(indexUName);
									if (!cAllLocation.getString(indexUId)
											.equals(this.uID)) {
										GeneralMarker new_Marker = new GeneralMarker(
												other_la, other_lo, other_Id,
												other_Name, type, this, u_pic);
										System.out.println(other_Id+":"+"u_pic="+u_pic);
										maps_marker[count] = map
												.addMarker(new_Marker
														.get_Marker());
										count++;
									}else{
										String RemLoc = PreferenceUtils.getRemLoc(this);
										String f_la="0",f_lo="0";
										if (!RemLoc.equals("")) {
											String[] tmp2 = RemLoc.split(";");
											latitude = Double.parseDouble(tmp2[0]);
											longitude = Double.parseDouble(tmp2[1]);
										}
										if(latitude==0)
											f_la = ""+other_la;
										if(longitude==0)
											f_lo = ""+other_lo;
										PreferenceUtils.setRemLoc(getApplicationContext(), ""
												+ f_la, "" + f_lo);
									}
								}
							}
						} while (cAllLocation.moveToNext());
					}
				}
				current_function = null;
				break;
			}

			case update_user_location_request: {
				if (auto_trace != null && auto_trace.equals("y")) {
					refreshMyLocation();
				}
				if (sos_request != null && sos_request.equals("y")) {
					HttpRequests request = new HttpRequests(Map_page.this,
							null, getApplicationContext());
					request.execute("location_remark", "emergency", uID);
					sos_request = null;
				}
				break;
			}

			case connection_fail: {
				Toast.makeText(this,
						"Connection with server is not available.",
						Toast.LENGTH_SHORT).show();
				if (current_function.equals("user_status")) {
					findViewById(R.id.user_status_Button).getTop();
					int y = findViewById(R.id.user_status_Button).getBottom() * 3 / 2;
					int x = getWindowManager().getDefaultDisplay().getWidth() / 10;
					GeneralPopupWindow.userStatus_PopupWindow(x, y, this, uID,
							false);
					current_function = null;
				}
				break;
			}

			case get_marker_request: {
				HttpRequests request = new HttpRequests(this, null,
						getApplicationContext());
				request.execute("get_user_info_request");

				JSONArray respond = statusUpdate.getPayload();
				String mId = "", mName = "", mlatitude = "", mlongitude = "", mType = "", mDesc = "", eId = "";
				for (int i = 0; i < respond.length(); i++) {
					for (int k = 0; k < ((JSONArray) respond.get(i)).length(); k++) {
						String r = "" + ((JSONArray) respond.get(i)).get(k);
						switch (k) {
						case 0:
							mId = r;
							break;
						case 2:
							mName = r;
							break;
						case 3:
							mlatitude = r;
							break;
						case 4:
							mlongitude = r;
							break;
						case 5:
							mType = r;
							break;
						case 6:
							mDesc = r;
							break;
						case 7:
							eId = r;
							break;
						default:
							break;
						}
					}
					DBC.open();
					Cursor abc = DBC.getOneMarkerInfo(mId);
					boolean found_marker = false;
					if (abc.getCount() > 0) {
						found_marker = true;
					}
					if (found_marker) {
						DBC.updateMarkerInfo(mId, mName, mlatitude, mlongitude,
								mType, mDesc, eId);
						System.out.println("markerfounded");					
					} else {
						DBC.insertMarkerInfo(mId, mName, mlatitude, mlongitude,
								mType, mDesc, eId);
						System.out.println("markerNotFound");
					}
					// intialize variable
					mId = mName = mlatitude = mlongitude = mType = mDesc = eId = "";
					break;
				}
			}

			case update_user_info_request: {
				//showDialog(DIALOG_USER_UPDATED);
				break;
			}

			case get_event_info_request: {
				JSONArray respond = statusUpdate.getPayload();
				String EventNo, EventName, EventPlace, EventDate, TotalNum, EventDesc;
				EventNo = "" + ((JSONArray) respond.get(0)).get(0);
				EventName = "" + ((JSONArray) respond.get(0)).get(1);
				EventDesc = "" + ((JSONArray) respond.get(0)).get(2);
				EventDate = "" + ((JSONArray) respond.get(0)).get(3);
				TotalNum = "" + ((JSONArray) respond.get(0)).get(4);
				EventPlace = "" + ((JSONArray) respond.get(0)).get(5);

				Intent intent = new Intent(this, ViewEvent_page.class);
				intent.putExtra("EventNo", "" + EventNo);
				intent.putExtra("EventName", "" + EventName);
				intent.putExtra("EventDesc", "" + EventDesc);
				intent.putExtra("EventDate", "" + EventDate);
				intent.putExtra("TotalNum ", "" + TotalNum);
				intent.putExtra("EventPlace", "" + EventPlace);
				startActivity(intent);
			}

			default:
				break;
			}
			if (loading) {
				pBar.cancel();
				loading = false;
			}
			DBC.close();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			Log.e("Error", "error = " + e1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loading() {
		pBar = new ProgressDialog(this);
		pBar.setMessage("Loading, please wait..."); 
		pBar.setIndeterminate(true);
		pBar.setIndeterminateDrawable(getResources().getDrawable(
				R.anim.loading_animation));
		pBar.setCancelable(false);
		pBar.show();
	}

	@Override
	public void onBackPressed() {
		showDialog(DIALOG_EXIT);
	}

	@Override
	public void finish() {
		super.finish();
		instance = null;
	}

	// Animation method - infow menu
	public void moveInfoWindow() {
		infow_status = true;
		RelativeLayout infow_menu = (RelativeLayout) findViewById(R.id.infow_menu);
		infow_menu.setVisibility(View.VISIBLE);
		TranslateAnimation translateAnimation = new TranslateAnimation(-120, 0,
				0, 0);
		translateAnimation.setDuration(500);
		translateAnimation.setRepeatCount(0);
		infow_menu.setAnimation(translateAnimation);
		translateAnimation.setFillAfter(true);
		translateAnimation.startNow();
	}

	public void replaceInfoWindow() { 
		infow_status = false;
		RelativeLayout infow_menu = (RelativeLayout) findViewById(R.id.infow_menu);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, -180,
				0, 0);
		translateAnimation.setDuration(500);
		translateAnimation.setRepeatCount(0);
		infow_menu.setAnimation(translateAnimation);
		translateAnimation.setFillAfter(true);
		translateAnimation.startNow();
		infow_menu.setVisibility(View.GONE);
	}

	// Animation method - setting bar
	public void moveSettingWindow() {
		setting_status = true;
		RelativeLayout setting_menu = (RelativeLayout) findViewById(R.id.setting_menu);
		setting_menu.setVisibility(View.VISIBLE);
		TranslateAnimation translateAnimation = new TranslateAnimation(500, 0,
				0, 0);
		translateAnimation.setDuration(500);
		translateAnimation.setRepeatCount(0);
		setting_menu.setAnimation(translateAnimation);
		translateAnimation.setFillAfter(true);
		translateAnimation.startNow();
	}

	public void replaceSettingWindow() {
		setting_status = false;
		RelativeLayout setting_menu = (RelativeLayout) findViewById(R.id.setting_menu);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 600,
				0, 0);
		translateAnimation.setDuration(500);
		translateAnimation.setRepeatCount(0);
		setting_menu.setAnimation(translateAnimation);
		translateAnimation.setFillAfter(true);
		translateAnimation.startNow();
		setting_menu.setVisibility(View.INVISIBLE);
		translateAnimation
				.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationEnd(Animation arg0) {
						RelativeLayout setting_menu = (RelativeLayout) findViewById(R.id.setting_menu);
						setting_menu.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
					}

					@Override
					public void onAnimationStart(Animation arg0) {
					}
				});
	}

	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
		private View view;

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
			String iw_name = marker.getTitle(), iw_data = marker.getSnippet(), iw_id, iw_la, iw_lo, iw_desc="", iw_type,iw_pic;
			String[] iw_temp = iw_data.split(";");
			if(iw_temp.length > 4)
				iw_desc = iw_temp[4];
			iw_type = iw_temp[0];
			iw_id = iw_temp[1];
			iw_la = iw_temp[2];
			iw_lo = iw_temp[3];
			iw_pic = iw_temp[5];
			if (iw_desc.equals("non")) {
				view = getLayoutInflater().inflate(R.layout.info_window_layout,
						null);
				target_uid = iw_id;
				target_is_user = true;
				TextView name_tv = (TextView) view.findViewById(R.id.name_tv);
				TextView id_tv = (TextView) view.findViewById(R.id.id_tv);
				TextView latitude_tv = (TextView) view
						.findViewById(R.id.latitude_tv);
				TextView longitude_tv = (TextView) view
						.findViewById(R.id.longitude_tv);
				ImageView infow_user_pic = (ImageView) view.findViewById(R.id.infow_user_pic);
				System.out.println("iw_pic="+iw_pic);
				File imgFile = new  File(Environment.getExternalStorageDirectory()
			            + File.separator + "Outgoing/"+iw_pic);
				if(imgFile.exists()){
				    Bitmap founded_Bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				    infow_user_pic.setImageBitmap(founded_Bitmap);	
				}else{
					imageLoader.DisplayImage(iw_pic,infow_user_pic);
				}
				name_tv.setText(iw_name);
				id_tv.setText(iw_id);
				latitude_tv.setText(iw_la);
				longitude_tv.setText(iw_lo);
			} else {
				view = getLayoutInflater().inflate(
						R.layout.info_window_layout2, null);
				target_is_user = false;
				TextView infow_2_name_tv = (TextView) view
						.findViewById(R.id.infow_2_name_tv);
				TextView infow_2_desc_tv = (TextView) view
						.findViewById(R.id.infow_2_desc_tv);
				infow_2_name_tv.setText(iw_name);
				infow_2_desc_tv.setText(iw_desc);
			}
			return view;
		}
	}

	private class CustomOnMarkerClickListener implements OnMarkerClickListener {
		String target_type;

		@Override
		public boolean onMarkerClick(Marker marker) {
			String iw_data = marker.getSnippet();
			String[] iw_temp = iw_data.split(";");
			target_type = iw_temp[0];
			if(!target_type.equals("assembly")&&!target_type.equals("station"))
				moveInfoWindow();
			return false;
		}
	}

	private String targetUserInfo(String target_uid, String target_type) {
		String target_userName = null, target_userPhone = null, target_userEmail = null, target_eventNo = null;
		DBC = new DatabaseConnector(this);
		String uId = PreferenceUtils.getUID(this);
		Cursor cUInfo = DBC.getOneUserInfo(target_uid);
		if (cUInfo != null && cUInfo.getCount() > 0) {
			cUInfo.moveToFirst();
			int indexUName = cUInfo.getColumnIndex("uName");
			int indexUPhone = cUInfo.getColumnIndex("uPhone");
			int indexUEmail = cUInfo.getColumnIndex("uEmail");
			int indexENO = cUInfo.getColumnIndex("eId");
			int indexGender = cUInfo.getColumnIndex("uGender");
			target_userName = cUInfo.getString(indexUName);
			target_userPhone = cUInfo.getString(indexUPhone);
			target_userEmail = cUInfo.getString(indexUEmail);
			target_eventNo = cUInfo.getString(indexENO);
		}
		if (target_type.equals("phone"))
			return target_userPhone;
		else if (target_type.equals("email"))
			return target_userEmail;
		else if (target_type.equals("name"))
			return target_userName;
		else
			return null;
	}

	private boolean checkReturnData(String data) {
		if (data == null) {
			showDialog(DIALOG_NO_SUCH_DATA);
			return false;
		}
		return true;
	}
	class ClientThread implements Runnable{
		String input_ip;
		String input_port;
		public ClientThread(String input_ip,String input_port){
			super();
			this.input_ip = input_ip;
			this.input_port = input_port;
		}

		@Override
		public void run() {
			try {
				socket = new SocketIO("http://"+input_ip+":"+input_port);
			} catch (MalformedURLException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket.connect(new IOCallback() {
			    @Override
			    public void on(String event, IOAcknowledge ack, Object... args) {
			        if ("echo back".equals(event) && args.length > 0) {
			            Log.d("SocketIO", "" + args[0]);
			            // -> "hello"
			        }
			    }

			    @Override
			    public void onMessage(JSONObject json, IOAcknowledge ack) {			    	
			    	try {
						JSONArray datas = (JSONArray)json.get("args");
						String[] msg_data = (""+datas.get(0)).split(";");
						if(msg_data[0].equals("login")){
							
						}else if(msg_data[0].equals("msg") && !msg_data[1].equals(uID)){
							//if(onPaused)
								buildNotify(msg_data[3]);
							DBC.insertMsg(msg_data[0], msg_data[1], msg_data[2], msg_data[3]);
							System.out.println(msg_data[0]+":"+msg_data[1]+":"+msg_data[2]+":"+msg_data[3]);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			    @Override		
			    public void onMessage(String data, IOAcknowledge ack) {			    }
			    @Override
			    public void onError(SocketIOException socketIOException) {
			    	socketIOException.printStackTrace();
			    }
			    @Override
			    public void onDisconnect() {}
			    @Override
			    public void onConnect() {
					socket.emit("echo", ack, "login;"+userName+";null;null");
			    }
			});	
		}
		
	}
	private void buildNotify(String data){
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(this,Msg_list_page.class); 
        System.out.println("UUUUUUUUUUUUUU="+uID);
        notifyIntent.putExtra("uID", uID);
        notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent appIntent=PendingIntent.getActivity(this,0,notifyIntent,0);
        Notification notification = new Notification();
        notification.icon=R.drawable.ic_launcher;
        notification.tickerText=data;
        notification.defaults=Notification.DEFAULT_ALL;
        notification.setLatestEventInfo(this,"Outgoing",data,appIntent);
        notificationManager.notify(0,notification);
	}
}