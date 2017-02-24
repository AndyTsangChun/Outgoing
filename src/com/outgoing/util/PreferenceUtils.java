package com.outgoing.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class PreferenceUtils {

	/**
	 * Shared Perferences name
	 */
	private static final String PREFS_NAME = "outgoing_settings";

	// Twitter
	private static final String PREF_UID = "UID";
	private static final String PREF_LOGINNAME = "LOGINNAME";
	private static final String PREF_PWD = "PASSWORD";
	private static final String PREF_LOGGEDIN = "is_logged_in";
	private static final String PREF_TYPE = "type";
	private static final String PREF_PIC = "pic";
	private static final String PREF_REMUSER = "REMUSER";
	private static final String PREF_REMLOC = "REMLOC";
	private static final String PREF_USEROPTION = "UserOption";
	private static final String PREF_FILTEROPTION = "FilterOption";
	private static final String PREF_SOS = "sos";
	private static final String PREF_EDIT_MARKER = "edit_marker";
	private static final String PREF_NOTI_OPTION = "noti_option";

	public static void setUID(Context context, String userId) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_UID, userId);
		editor.commit();
	}

	public static String getUID(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_UID, "");
	}
	
	public static void setLoginName(Context context, String loginName) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_LOGINNAME, loginName);
		editor.commit();
	}

	public static String getLoginName(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_LOGINNAME, "");
	}

	public static void setPassword(Context context, String password) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_PWD, password);
		editor.commit();
	}

	public static String getPassword(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_PWD, "");
	}
	
	public static void setPic(Context context, String pic) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_PIC, pic);
		editor.commit();
	}

	public static String getPic(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_PIC, "");
	}

	public static void setIsLoggedIn(Context context, boolean login) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREF_LOGGEDIN, login);
		editor.commit();
	}

	public static boolean getIsLoggedIn(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(PREF_LOGGEDIN, false);
	}

	public static void setRemUser(Context context, String loginName, String password) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_REMUSER, loginName + ";" + password);
		editor.commit();
	}

	public static String getRemUser(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_REMUSER, "");
	}
	
	public static void setUType(Context context, String type) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_TYPE, type);
		editor.commit();
	}

	public static String getUType(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_TYPE, "");
	}
	
	public static void setRemLoc(Context context, String latitude, String longitude) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_REMLOC, latitude + ";" + longitude);
		editor.commit();
	}

	public static String getRemLoc(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_REMLOC, "");
	}
	
	public static void setUserOption(Context context, String option) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_USEROPTION, option);
		editor.commit();
	}

	public static String getUserOption(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_USEROPTION, "");
	}
	
	public static void setFilterOption(Context context, String option) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_FILTEROPTION, option);
		editor.commit();
	}

	public static String getFilterOption(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_FILTEROPTION, "");
	}
	
	public static void setSOS(Context context, String status) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_SOS, status);
		editor.commit();
	}

	public static String getSOS(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_SOS, "");
	}
	public static void setEditMarker(Context context, String edit_text) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_EDIT_MARKER, edit_text);
		editor.commit();
	}

	public static String getEditMarker(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString(PREF_EDIT_MARKER, "");
	}
}
