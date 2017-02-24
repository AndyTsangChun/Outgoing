package com.outgoing.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.outgoing.R;
import com.outgoing.activity.BaseActivity;
import com.outgoing.util.PreferenceUtils;


public class GeneralMarker{
		private static LatLng location;
		private static String id,type,mDesc,name,uPhoto;
		private static double latitude,longitude;
		private static BaseActivity act;
	
		public GeneralMarker (double latitude, double longitude, String userId, String uName, String type, BaseActivity a, String uPhoto){
			this.act = a;
			this.location = new LatLng(latitude, longitude);
			this.latitude = latitude;
			this.longitude = longitude;
			this.id = userId;
			this.type = type;
			this.name = uName;
			this.mDesc = "non";
			this.uPhoto = uPhoto;
		}
		public GeneralMarker (double latitude, double longitude, String mId, String mName,String type, String mDesc, BaseActivity a){
			this.act = a;
			this.location = new LatLng(latitude, longitude);
			this.latitude = latitude;
			this.longitude = longitude;
			this.id = mId;
			this.type = type;
			this.name = mName;
			this.mDesc = mDesc;
			this.uPhoto = "non";
		}
		
		public static MarkerOptions get_Marker(){
			MarkerOptions m = null;
			int choice = -1;
			String userOption = PreferenceUtils
					.getUserOption(act);
			String[] tmp = userOption.split(";");
			if(tmp.length>1 && tmp[1]!="" && tmp[1]!=null)
				choice = Integer.parseInt(tmp[1]);
			else
				choice = 0;
			if(choice == 0 || choice == -1){
				if(type.equals("own")){			
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag_green));
				}
				if(type.equals("member")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag_blue));
				}
				if(type.equals("emergency")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag_red));
				}
				if(type.equals("leader")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag_black));
				}
				if(type.equals("station")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_station));
				}
				if(type.equals("assembly")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_assembly));
				}
			}
			if(choice == 1){
				if(type.equals("own")){			
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point_green));
				}
				if(type.equals("member")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point_blue));
				}
				if(type.equals("emergency")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point_red));
				}
				if(type.equals("leader")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point_black));
				}
				if(type.equals("station")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_station));
				}
				if(type.equals("assembly")){
					m = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_assembly));
				}
			}
			return m.position(location).title(""+name).snippet(type+";"+id+";"+latitude+";"+longitude+";"+mDesc+";"+uPhoto).draggable(false).visible(true).anchor(0.5f, 0.5f);
		}

		public static void update_Marker_Info() {
			// TODO Auto-generated method stub
			
		}
}
