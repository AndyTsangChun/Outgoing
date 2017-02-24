package com.outgoing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.outgoing.R;
import com.outgoing.activity.*;
import com.outgoing.http.ImageLoader;
import com.outgoing.util.DatabaseConnector;
import com.outgoing.util.PreferenceUtils;

public class GeneralPopupWindow extends Activity {
	// pop up window variable
	public static PopupWindow popupWindow;
	private static LinearLayout layout_l;
	public static RelativeLayout layout_r;
	public static Button save_Button;
	private static BaseActivity act;
	// filter
	private static CheckBox all, other, leader, assembly, station;
	private static String all_status, other_status, leader_status,
			assembly_status, station_status;
	// user status
	private static TextView eventNo, userNo;
	private static EditText userName, userPhone, userEmail;
	private static ImageView userPhoto;
	private static RadioButton m_rb, f_rb;
	private static DatabaseConnector DBC;
	// sos
	private static String sos_status;
	private static TextView sos_TB;
	// search
	private static ExpandableListAdapter listAdapter;
	private static ExpandableListView expListView;
	private static List<String> listDataHeader;
	private static HashMap<String, List<String>> listDataChild;
	private static String search_String = "";
	private static Button search_Button;
	private static TextView search_et;
	// edit
	private static EditText em_name_et, em_desc_et;
	//pic download 
	public static ImageLoader imageLoader;
	//private static downloadPhoto mDownloader;

	public static void userStatus_PopupWindow(int x, int y, BaseActivity a,
			String uID, boolean isEditable) {
		layout_r = (RelativeLayout) LayoutInflater.from(a).inflate(
				R.layout.user_status_pop, null);

		act = a;
		popupWindow = new PopupWindow(a);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(a.getWindowManager().getDefaultDisplay()
				.getWidth() / 5 * 4);
		popupWindow.setHeight(a.getWindowManager().getDefaultDisplay()
				.getHeight() / 7 * 5);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout_r);
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(a.findViewById(R.id.header), Gravity.LEFT
				| Gravity.BOTTOM, x, y);
		eventNo = (TextView) layout_r.findViewById(R.id.EventNo_TB);
		userNo = (TextView) layout_r.findViewById(R.id.UserNo_TB);
		userName = (EditText) layout_r.findViewById(R.id.UserName_TB);
		userPhone = (EditText) layout_r.findViewById(R.id.UserPhone_TB);
		userEmail = (EditText) layout_r.findViewById(R.id.UserEmail_TB);
		userPhoto = (ImageView) layout_r.findViewById(R.id.UserPhoto);
		m_rb = (RadioButton) layout_r.findViewById(R.id.male_rb);
		f_rb = (RadioButton) layout_r.findViewById(R.id.female_rb);

		DBC = new DatabaseConnector(act);
		Cursor cUInfo = DBC.getOneUserInfo(uID);
		userNo.setText(uID);
		if (cUInfo != null && cUInfo.getCount() > 0) {
			cUInfo.moveToFirst();
			int indexUName = cUInfo.getColumnIndex("uName");
			int indexUPhone = cUInfo.getColumnIndex("uPhone");
			int indexUEmail = cUInfo.getColumnIndex("uEmail");
			int indexENO = cUInfo.getColumnIndex("eId");
			int indexGender = cUInfo.getColumnIndex("uGender");
			int indexUserPhoto = cUInfo.getColumnIndex("uPhoto");
			userName.setText(cUInfo.getString(indexUName));
			userPhone.setText(cUInfo.getString(indexUPhone));
			userEmail.setText(cUInfo.getString(indexUEmail));
			eventNo.setText(cUInfo.getString(indexENO));
			imageLoader=new ImageLoader(act.getApplicationContext());
			imageLoader.DisplayImage(cUInfo.getString(indexUserPhoto),userPhoto);
			if (cUInfo.getString(indexGender) != null
					&& cUInfo.getString(indexGender).equals("F")) {
				f_rb.setChecked(true);
			} else {
				m_rb.setChecked(true);
			}
		}
		save_Button = (Button) layout_r.findViewById(R.id.save_button);
		if (!isEditable) {
			userName.setEnabled(false);
			userPhone.setEnabled(false);
			userEmail.setEnabled(false);
			f_rb.setEnabled(false);
			m_rb.setEnabled(false);
			save_Button.setEnabled(false);
			save_Button.setVisibility(View.GONE);
		}
	}

	// filter pop up window
	public static void filter_PopupWindow(int x, int y, BaseActivity a) {
		layout_l = (LinearLayout) LayoutInflater.from(a).inflate(
				R.layout.filter_pop, null);

		act = a;
		popupWindow = new PopupWindow(a);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(a.getWindowManager().getDefaultDisplay()
				.getWidth() / 3 * 2);
		popupWindow.setHeight(a.getWindowManager().getDefaultDisplay()
				.getHeight() / 5 * 2);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout_l);
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(a.findViewById(R.id.filter_Button),
				Gravity.LEFT | Gravity.BOTTOM, x, y);
		all = (CheckBox) layout_l.findViewById(R.id.all_CB);
		other = (CheckBox) layout_l.findViewById(R.id.other_CB);
		leader = (CheckBox) layout_l.findViewById(R.id.leader_CB);
		assembly = (CheckBox) layout_l.findViewById(R.id.assembly_CB);
		station = (CheckBox) layout_l.findViewById(R.id.station_CB);
		String filterOption = PreferenceUtils.getFilterOption(a);
		String[] tmp = filterOption.split(";");
		if (filterOption != null && !filterOption.equals("")) {
			if (tmp[0].equals("y")) {
				all.setChecked(true);
				other.setChecked(true);
				leader.setChecked(true);
				assembly.setChecked(true);
				station.setChecked(true);
			}
			if (tmp[1].equals("y"))
				other.setChecked(true);
			if (tmp[2].equals("y"))
				leader.setChecked(true);
			if (tmp[3].equals("y"))
				assembly.setChecked(true);
			if (tmp[4].equals("y"))
				station.setChecked(true);
		} else {
			all_status = "n";
			other_status = "n";
			leader_status = "n";
			assembly_status = "n";
			station_status = "n";
		}
		all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				cb_action(buttonView, isChecked);
			}
		});
		other.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				cb_action(buttonView, isChecked);
			}
		});
		leader.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				cb_action(buttonView, isChecked);
			}
		});
		assembly.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				cb_action(buttonView, isChecked);
			}
		});
		station.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				cb_action(buttonView, isChecked);
			}
		});
	}

	public static void sos_PopupWindow(int x, int y, BaseActivity a) {
		layout_r = (RelativeLayout) LayoutInflater.from(a).inflate(
				R.layout.sos_pop, null);

		act = a;
		popupWindow = new PopupWindow(a);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(a.getWindowManager().getDefaultDisplay()
				.getWidth() / 5 * 4);
		popupWindow.setHeight(a.getWindowManager().getDefaultDisplay()
				.getHeight() / 10 * 3);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout_r);
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(a.findViewById(R.id.map), Gravity.LEFT
				| Gravity.CENTER, x, y);

		sos_TB = (TextView) layout_r.findViewById(R.id.sos_TB);
		sos_status = PreferenceUtils.getSOS(act);
		if (sos_status != null && !sos_status.equals(""))
			sos_TB.setText(sos_status);
		else {
			sos_TB.setText("OFF");
			PreferenceUtils.setSOS(act, "OFF");
		}
	}

	public static void cb_action(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.all_CB:
			if (buttonView.isChecked()) {
				all_status = "y";
				other.setChecked(true);
				leader.setChecked(true);
				assembly.setChecked(true);
				station.setChecked(true);
			} else {
				all_status = "n";
				other.setChecked(false);
				leader.setChecked(false);
				assembly.setChecked(false);
				station.setChecked(false);
			}
			break;
		case R.id.other_CB:
			if (buttonView.isChecked()) {
				other_status = "y";
			} else {
				other_status = "n";
			}
			break;
		case R.id.leader_CB:
			if (buttonView.isChecked()) {
				leader_status = "y";
			} else {
				leader_status = "n";
			}
			break;
		case R.id.assembly_CB:
			if (buttonView.isChecked()) {
				assembly_status = "y";
			} else {
				assembly_status = "n";
			}
			break;
		case R.id.station_CB:
			if (buttonView.isChecked()) {
				station_status = "y";
			} else {
				station_status = "n";
			}
			break;
		default:
			break;
		}
		PreferenceUtils.setFilterOption(act, all_status + ";" + other_status
				+ ";" + leader_status + ";" + assembly_status + ";"
				+ station_status);
	}

	public static void search_PopupWindow(int x, int y, final Map_page a) {
		layout_r = (RelativeLayout) LayoutInflater.from(a).inflate(
				R.layout.search_pop, null);

		act = a;
		popupWindow = new PopupWindow(a);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(x * 8 / 10);
		popupWindow.setHeight(y * 6 / 10);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout_r);
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(a.findViewById(R.id.map), Gravity.LEFT
				| Gravity.TOP, x / 10, y * 2 / 10);

		// set elv
		expListView = (ExpandableListView) layout_r
				.findViewById(R.id.member_elv);
		search_Button = (Button) layout_r
				.findViewById(R.id.memberSerach_Button);
		search_et = (EditText) layout_r.findViewById(R.id.search_et);
		// preparing list data
		refreshListData("",a);

		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				int len = listAdapter.getGroupCount();

				for (int i = 0; i < len; i++) {
					if (i != groupPosition) {
						expListView.collapseGroup(i);
					}
				}
			}

		});
		search_Button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				search_String = search_et.getText().toString();
				refreshListData(search_String,a);
			}
		});

	}

	public static void edit_PopupWindow(int x, int y, BaseActivity a) {
		layout_r = (RelativeLayout) LayoutInflater.from(a).inflate(
				R.layout.edit_marker_pop, null);

		act = a;
		popupWindow = new PopupWindow(a);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(x * 8 / 10);
		popupWindow.setHeight(y * 6 / 10);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout_r);
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(a.findViewById(R.id.map), Gravity.LEFT
				| Gravity.TOP, x / 10, y * 2 / 10);

		// set elv
		em_name_et = (EditText) layout_r.findViewById(R.id.em_name_et);
		em_desc_et = (EditText) layout_r.findViewById(R.id.em_desc_et);

		String rem_em_string = PreferenceUtils.getEditMarker(act);
		if (!rem_em_string.equals("")) {
			String[] tmp = rem_em_string.split(";");
			em_name_et.setText(tmp[0]);
			em_desc_et.setText(tmp[1]);
		}else{
			em_name_et.setText("");
			em_desc_et.setText("");
		}
		em_name_et.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			@Override
			public void afterTextChanged(Editable arg01) {
				String s =em_name_et.getText().toString()+";"+em_desc_et.getText().toString();
				PreferenceUtils.setEditMarker(act,s);
			}
		});
		em_desc_et.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			@Override
			public void afterTextChanged(Editable arg01) {
				String s =em_name_et.getText().toString()+";"+em_desc_et.getText().toString();
				PreferenceUtils.setEditMarker(act,s);
			}
		});
	}

	/*
	 * Preparing the list data
	 */
	private static void refreshListData(String search_String,Map_page a) {
		boolean no_user_found = false;
		DBC = new DatabaseConnector(a);
		DBC.open();
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		if (!search_String.equals("")) {
			Cursor searchUser = DBC.searchUser(search_String);
			if (searchUser != null && searchUser.getCount() > 0) {
				int num = searchUser.getCount();
				searchUser.moveToFirst();
				for (int i = 0,j = 0; i < num; i++,j++) {
					int indexUName = searchUser.getColumnIndex("uName");
					int indexUId = searchUser.getColumnIndex("uId");
					int indexUPhone = searchUser.getColumnIndex("uPhone");
					int indexUEmail = searchUser.getColumnIndex("uEmail");
					int indexUGender = searchUser.getColumnIndex("uGender");
					int indexUType = searchUser.getColumnIndex("uType");
					int indexLongitude = searchUser.getColumnIndex("longitude");
					int indexLatitude = searchUser.getColumnIndex("latitude");
					int indexENO = searchUser.getColumnIndex("eId");
					int indexUPic = searchUser.getColumnIndex("uPhoto");
					String uName = searchUser.getString(indexUName);
					String uID = searchUser.getString(indexUId);
					String uPhone = searchUser.getString(indexUPhone);
					String uEmail = searchUser.getString(indexUEmail);
					String uGender = searchUser.getString(indexUGender);
					String uType = searchUser.getString(indexUType);
					String Longitude = searchUser.getString(indexLongitude);
					String Latitude = searchUser.getString(indexLatitude);
					String uENO = searchUser.getString(indexENO);
					String uPic = searchUser.getString(indexUPic);
					if(!uID.equals(PreferenceUtils.getUID(act))){
						listDataHeader.add(uName);
						List<String> searchUserItem = new ArrayList<String>();
						searchUserItem.add(uID);
						searchUserItem.add(uPhone);
						searchUserItem.add(uEmail);
						searchUserItem.add(uGender);
						searchUserItem.add(uType);
						searchUserItem.add(Longitude);
						searchUserItem.add(Latitude);
						searchUserItem.add(uENO);
						searchUserItem.add(uPic);
						listDataChild.put(listDataHeader.get(j), searchUserItem);
					}else{
						j = j-1;
					}
					searchUser.moveToNext();
				}
			} else {
				no_user_found = true;
			}
		} else {
			no_user_found = true;
		}
		if (no_user_found) {
			System.out.println("No user found");
			// Adding child data
			listDataHeader.add("No user found");
			// Adding child data
			List<String> searchUserItem = new ArrayList<String>();
			// top250.add("The Shawshank Redemption");
			listDataChild.put(listDataHeader.get(0), searchUserItem); // Header,
																		// Child
																		// data
		}

		listAdapter = new ExpandableListAdapter(act, listDataHeader,
				listDataChild, a);
		// setting list adapter
		expListView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
	}

	public void btnHandler(View view) {
		switch (view.getId()) {

		default:
			break;
		}
	}
}
