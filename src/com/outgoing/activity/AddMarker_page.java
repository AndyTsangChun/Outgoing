package com.outgoing.activity;

import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.outgoing.R;
import com.outgoing.R.layout;
import com.outgoing.R.menu;
import com.outgoing.http.HttpRequests;
import com.outgoing.model.GeneralMarker;
import com.outgoing.model.GeneralPopupWindow;
import com.outgoing.model.StatusUpdate;
import com.outgoing.util.PreferenceUtils;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AddMarker_page extends BaseActivity {
	private GoogleMap map;
	static double latitude;
	static double longitude;
	private static String em_lo,em_la,em_name,em_desc;
	private Marker Marker;
	static LatLng MyLocation;
	private String marker_type = "asseMmbly";
	private MarkerOptions markerOpt;
	private String uId="U111",eventNo="";
	//display
	int width,height;
	// loading
	ProgressDialog pBar;
	AnimationDrawable progressAnimation;
	boolean loading = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.addmarker_page);
		findViewById(R.id.assPoint_Button).setEnabled(false);
		findViewById(R.id.station_Button).setEnabled(true);
		String RemLoc = PreferenceUtils.getRemLoc(getApplicationContext());
		if (!RemLoc.equals("")) {
			String[] tmp = RemLoc.split(";");
			latitude = Double.parseDouble(tmp[0]);
			longitude = Double.parseDouble(tmp[1]);
		}
		eventNo = getIntent().getExtras().getString("eventNo");
		PreferenceUtils.setEditMarker(this,"");
		MyLocation = new LatLng(latitude, longitude);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		//
		markerOpt = new MarkerOptions();
		markerOpt.position(new LatLng(latitude, longitude)).title("Add").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_assembly));
		markerOpt.draggable(true);
		if (Marker != null) {
			Marker.remove();
		}
		
		Marker = map.addMarker(markerOpt);
		map.setOnMarkerDragListener(new OnMarkerDragListener(){
			@Override
			public void onMarkerDrag(
					com.google.android.gms.maps.model.Marker marker) {}

			@Override
			public void onMarkerDragEnd(
					com.google.android.gms.maps.model.Marker marker) {
				em_la = ""+marker.getPosition().latitude;
				em_lo = ""+marker.getPosition().longitude;
			}

			@Override
			public void onMarkerDragStart(
					com.google.android.gms.maps.model.Marker marker) {}			
		});
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 18));
		map.setPadding(0, 100, 0, 120);
		
		// Setting a custom info window adapter for the google map
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        //map.setOnMarkerClickListener(new CustomOnMarkerClickListener());
        
        // Adding and showing marker while touching the GoogleMap
        map.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
               //replaceInfoWindow(); 
            }
        });
        getDisplaySize();
	}

	public void onResume(){
		super.onResume();
		String rem_em_string = PreferenceUtils.getEditMarker(this);
		if (!rem_em_string.equals("")) {
			String[] tmp = rem_em_string.split(";");
			em_name = tmp[0];
			em_desc = tmp[1];
		}
	}
	
	@SuppressLint("NewApi")
	private void getDisplaySize(){
		//get display size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}
	
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
		 
	        private View view;
	        private BaseActivity a;
	        
	        public CustomInfoWindowAdapter(BaseActivity a){
	        	super();
	        	this.a = a;
	        }

			@Override
			public View getInfoContents(Marker marker) {
	            return null;
			}

			@Override
			public View getInfoWindow(Marker marker) {
				// TODO Auto-generated method stub
				view = getLayoutInflater().inflate(R.layout.info_window_layout2, null);
				TextView info_window_name = (TextView) view.findViewById(R.id.infow_2_name_tv);
				TextView info_window_desc = (TextView) view.findViewById(R.id.infow_2_desc_tv);
				String rem_em_string = PreferenceUtils.getEditMarker(a);
				if (!rem_em_string.equals("")) {
					String[] tmp = rem_em_string.split(";");
					em_name = tmp[0];
					em_desc = tmp[1];
				}
				System.out.println(em_name+";"+em_desc);
				info_window_name.setText(em_name);
				info_window_desc.setText(em_desc);
				return view;
			}
	 }
	
	public void btnHandler(View view) {
		switch (view.getId()) {
		case R.id.cancel_Button: {
			finish();
			break;
		}
		case R.id.assPoint_Button: {
			findViewById(R.id.assPoint_Button).setEnabled(false);
			findViewById(R.id.station_Button).setEnabled(true);
			marker_type = "assembly"; 
			if (Marker != null) {
				Marker.remove();
			}
			markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_assembly));
			Marker = map.addMarker(markerOpt);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 18));
			break;
		}
		case R.id.station_Button: {
			findViewById(R.id.assPoint_Button).setEnabled(true);
			findViewById(R.id.station_Button).setEnabled(false);
			marker_type = "station";
			if (Marker != null) {
				Marker.remove();
			}
			markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_station));
			Marker = map.addMarker(markerOpt);		
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 18));
			break;
		}
		case R.id.em_save_Button: {
			em_la = ""+Marker.getPosition().latitude;
			em_lo = ""+Marker.getPosition().longitude;
			em_name = em_desc ="";
			String rem_em_string = PreferenceUtils.getEditMarker(this);
			if (!rem_em_string.equals("")) {
				String[] tmp = rem_em_string.split(";");
				em_name = tmp[0];
				em_desc = tmp[1];
			}
			HttpRequests request = new HttpRequests(this, null,
					getApplicationContext());
			request.execute("add_marker_request",""+em_name,""+marker_type,""+em_desc,""+em_lo,""+em_la,""+eventNo);
			break;
		}
		case R.id.addmaker_edit_Button: {
			int y = height;
			int x = width;
			GeneralPopupWindow.edit_PopupWindow(x, y, this);
			break;
		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.addmarker_page, menu);
		return true;
	}

	public void loading() {
		pBar = new ProgressDialog(this);
		loading = true;
		pBar.setMessage("Loading, please wait...");
		pBar.setIndeterminate(true);
		pBar.setIndeterminateDrawable(getResources().getDrawable(
				R.anim.loading_animation));
		pBar.setCancelable(false);
		pBar.show();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		if (!(data instanceof StatusUpdate)) {
			return;
		}
		StatusUpdate statusUpdate = (StatusUpdate) data;
		if (loading) {
			pBar.cancel();
			loading = false;
		}try {
			switch (statusUpdate.getUpdateType()) {
			case add_marker_request : {
				JSONArray respond = statusUpdate.getPayload();
				String reply_status = "" + respond.get(0);
				if(reply_status.equals("1")){
					showDialog(DIALOG_MARKER_ADDED);
					System.out.println("Added");
				}else{
					showDialog(DIALOG_CONNECTION_FAILURE);
					System.out.println("Not Added");
				}
				this.finish();
			}	
				
			default:
				break;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			Log.e("Error", "error = " + e1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
