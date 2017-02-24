package com.outgoing.model;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.outgoing.R;
import com.outgoing.activity.BaseActivity;
import com.outgoing.activity.Chat_page;
import com.outgoing.activity.Map_page;
import com.outgoing.activity.Msg_content_page;
import com.outgoing.http.ImageLoader;
import com.outgoing.util.PreferenceUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private String myUID;
	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<String>> _listDataChild;
	private static Map_page act;
	//
	private ImageView search_icon_m, search_icon_f, search_icon_leader,
			search_icon_member, search_icon_admin,search_user_pic,search_icon_id;
	//pic download 
	public ImageLoader imageLoader;
	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData, Map_page act) {
		this.myUID = PreferenceUtils.getUID(act);
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this.act = act;
		imageLoader=new ImageLoader(act.getApplicationContext());
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.e_list_item, null);

		}
		String headerTitle = (String) getGroup(groupPosition);
		if (!headerTitle.equals("No user found")) {
			String uPhone, uEmail, Longitude, Latitude, uENO, uID;
			TextView latitude_tv, longitude_tv, uPhone_tv, uEmail_tv, eventNo_tv;
			Button search_button_msg, search_button_phone, search_button_trace;
			// set child view
			latitude_tv = (TextView) convertView
					.findViewById(R.id.search_latitude_tv);
			longitude_tv = (TextView) convertView
					.findViewById(R.id.search_longitude_tv);
			uPhone_tv = (TextView) convertView
					.findViewById(R.id.search_phone_tv);
			uEmail_tv = (TextView) convertView
					.findViewById(R.id.search_email_tv);
			eventNo_tv = (TextView) convertView
					.findViewById(R.id.search_event_tv);
			search_button_msg = (Button) convertView
					.findViewById(R.id.search_msg_button);
			search_button_phone = (Button) convertView
					.findViewById(R.id.search_phone_button);
			search_button_trace = (Button) convertView
					.findViewById(R.id.search_trace_button);

			// get item
			uID = (String) getChild(groupPosition, 0);
			uPhone = (String) getChild(groupPosition, 1);
			uEmail = (String) getChild(groupPosition, 2);
			Longitude = (String) getChild(groupPosition, 5);
			Latitude = (String) getChild(groupPosition, 6);
			uENO = (String) getChild(groupPosition, 7);
			latitude_tv.setText(Latitude);
			longitude_tv.setText(Longitude);
			uPhone_tv.setText(uPhone);
			uEmail_tv.setText(uEmail);
			eventNo_tv.setText(uENO);
			if( !Latitude.equals("null"))
				Map_page.s_la = Double.parseDouble(Latitude);
			else
				Map_page.s_la = 0.0;
			if( !Longitude.equals("null"))
				Map_page.s_lo = Double.parseDouble(Longitude);
			else
				Map_page.s_lo = 0.0;
			myUID = PreferenceUtils.getUID(act);
			search_button_msg.setOnClickListener(new msg_ClickListener(
					uID,myUID, act));
			search_button_phone.setOnClickListener(new phone_ClickListener(
					uPhone, act));
			//search_button_trace.setOnClickListener(new trace_ClickListener(act));
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int c = this._listDataChild
				.get(this._listDataHeader.get(groupPosition)).size();
		if (c > 1)
			c = 1;
		return c;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.e_list_group, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.search_user_name);
		// set parent view
		search_icon_m = (ImageView) convertView
				.findViewById(R.id.search_icon_m);
		search_icon_f = (ImageView) convertView
				.findViewById(R.id.search_icon_f);
		search_icon_leader = (ImageView) convertView
				.findViewById(R.id.search_icon_leader);
		search_icon_member = (ImageView) convertView
				.findViewById(R.id.search_icon_member);
		search_icon_admin = (ImageView) convertView
				.findViewById(R.id.search_icon_admin);
		search_user_pic = (ImageView) convertView
				.findViewById(R.id.search_user_pic);
		search_icon_id = (ImageView) convertView
				.findViewById(R.id.search_icon_id);

		if (!headerTitle.equals("No user found")) {
			String uID, uGender, uType, uPic;
			TextView uID_tv;
			uID_tv = (TextView) convertView.findViewById(R.id.search_user_id);
			// get item
			uID = (String) getChild(groupPosition, 0);
			uGender = (String) getChild(groupPosition, 3);
			uType = (String) getChild(groupPosition, 4);
			uPic = (String) getChild(groupPosition, 8);
			uID_tv.setText(uID);
			imageLoader.DisplayImage(uPic, search_user_pic);
			if (uType.equals("leader")) {
				search_icon_leader.setVisibility(View.VISIBLE);
				search_icon_member.setVisibility(View.INVISIBLE);
				search_icon_admin.setVisibility(View.INVISIBLE);
			} else if (uType.equals("member")) {
				search_icon_leader.setVisibility(View.INVISIBLE);
				search_icon_member.setVisibility(View.VISIBLE);
				search_icon_admin.setVisibility(View.INVISIBLE);
			} else if (uType.equals("leader")) {
				search_icon_leader.setVisibility(View.INVISIBLE);
				search_icon_member.setVisibility(View.INVISIBLE);
				search_icon_admin.setVisibility(View.VISIBLE);
			}
			if (uGender.equals("F")) {
				search_icon_m.setVisibility(View.INVISIBLE);
				search_icon_f.setVisibility(View.VISIBLE);
			} else {
				search_icon_m.setVisibility(View.VISIBLE);
				search_icon_f.setVisibility(View.INVISIBLE);
			}
		} else {
			search_icon_leader.setVisibility(View.INVISIBLE);
			search_icon_member.setVisibility(View.INVISIBLE);
			search_icon_admin.setVisibility(View.INVISIBLE);
			search_icon_m.setVisibility(View.INVISIBLE);
			search_icon_f.setVisibility(View.INVISIBLE);
			search_user_pic.setVisibility(View.INVISIBLE);
			search_icon_id.setVisibility(View.INVISIBLE);
		}
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public class phone_ClickListener implements View.OnClickListener {
		private String uPhone;
		private BaseActivity act;

		public phone_ClickListener(String uPhone, BaseActivity act) {
			this.uPhone = uPhone;
			this.act = act;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(act, Chat_page.class);
			intent.putExtra("target_phone", uPhone);
			act.startActivity(intent);
		}
	}
	public class msg_ClickListener implements View.OnClickListener {
		private String fromUID,toUID;
		private BaseActivity act;

		public msg_ClickListener(String fromUID, String toUID, BaseActivity act) {
			this.fromUID = fromUID;
			this.toUID = toUID;
			this.act = act;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(act, Msg_content_page.class);
			intent.putExtra("fromUser", fromUID);
			intent.putExtra("toUser", toUID);
			act.startActivity(intent);
		}
	}
}