package com.outgoing.util;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector {
	private static final String DATABASE_NAME = "OutGoing";// database name
	private SQLiteDatabase database; // database object
	private DatabaseOpenHelper databaseOpenHelper; // database helper

	// public constructor for DatabaseConnector
	public DatabaseConnector(Context context) {
		// create a new DatabaseOpenHelper
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME,
				null, 1);
	} // end DatabaseConnector constructor

	// open the database connection
	public void open() throws SQLException {
		// create or open a database for reading/writing
		database = databaseOpenHelper.getWritableDatabase();
	} // end method open

	// close the database connection
	public void close() {
		if (database != null)
			database.close(); // close the database connection
	} // end method close

	// insert data to Msg
	public void insertMsg(String mType, String fromUser, String toUser, String data) {
		ContentValues newData = new ContentValues();
		Calendar calendar = Calendar.getInstance();
		String dt = calendar.get(Calendar.DAY_OF_MONTH)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
		newData.put("DateTime", dt);
		newData.put("data", data);
		newData.put("msgType", mType);
		newData.put("fromUser", fromUser);
		newData.put("toUser", toUser);

		open(); // open the database
		database.insert("message", null, newData);
		close(); // close the database
	}
	
	// return msg
	public Cursor getAllMessage() {
		return database.query("message", new String[] { "DateTime", "fromUser", "toUser", "msgType", "data" }, null,
				null, null, null, null);
	}
	// return msg
		public Cursor getMyMessage(String toUser) {
			return database.query("message", new String[] { "DateTime", "fromUser", "toUser", "msgType", "data" }, "toUser = '"+ toUser+"' OR fromUser = '"+ toUser+"'",
					null, null, null, null);
	}

	public void deleteUserMessage(String user1,String user2) {
		open(); // open the database
		database.delete("message", "fromUser ='" + user1+"' AND toUser ='" +user2+"'", null);		
		close(); // close the database
	} // end method deleteContact
		

	public void deleteMessage(String id) {
		open(); // open the database
		database.delete("message", "_id=" + id, null);
		close(); // close the database
	} // end method deleteContact
	
	//insert userInfo
	public void insertUserInfo(String uId, String uName, String uPhone,String uEmail, String uGender, String uType, String longitude, String latitude, String eId, String uPhoto) {
		ContentValues newData = new ContentValues();
		newData.put("uId", uId);
		newData.put("uName", uName);
		newData.put("uPhone", uPhone);
		newData.put("uEmail", uEmail);
		newData.put("uGender", uGender);
		newData.put("uType", uType);
		newData.put("longitude", longitude);
		newData.put("latitude", latitude);
		newData.put("eId", eId);
		newData.put("uPhoto", uPhoto);
		
		open(); // open the database
		database.insert("userInfo", null, newData);
		close(); // close the database
	}
	
	//update userInfo
	public void updateUserInfo(String uId, String uName, String uPhone,String uEmail, String uGender, String uType, String longitude, String latitude, String eId, String uPhoto) {
		ContentValues newData = new ContentValues();
		newData.put("uName", uName);
		newData.put("uPhone", uPhone);
		newData.put("uEmail", uEmail);
		newData.put("uGender", uGender);
		newData.put("uType", uType);
		newData.put("longitude", longitude);
		newData.put("latitude", latitude);
		newData.put("eId", eId);
		newData.put("uPhoto", uPhoto);
		
		open(); // open the database
		database.update("userInfo", newData,"uId = '"+ uId+"'",null);
		close(); // close the database
	}
	
	// return all userInfo
	public Cursor getAllUserInfo() {
		return database.query("userInfo", new String[] { "uId", "uName", "uPhone","uEmail", "uGender", "uType", "longitude", "latitude", "eId", "uPhoto" }, null,
				null, null, null, null);
	}	
	// return all userInfo Location
	public Cursor getAllUserLocation(String eId) {
		return database.query("userInfo", new String[] { "uId", "longitude", "uName" ,"latitude", "uType", "uPhoto"}, "eId = '"+ eId+"'",
				null, null, null, null);
	}
	
	// return searched userInfo
	public Cursor searchUser(String search_String) {
		return database.query("userInfo", new String[] { "uId", "uName", "uPhone","uEmail", "uGender", "uType", "longitude", "latitude", "eId", "uPhoto"}, "uName LIKE '%"+ search_String+"%'",
				null, null, null, null);
	}
	
	
	// return single userInfo
	public Cursor getOneUserInfo(String uId) {
		open();
		return database.query("userInfo", new String[] { "uId", "uName", "uPhone","uEmail", "uGender", "uType", "longitude", "latitude", "eId", "uPhoto" }, "uId = '"+uId+"'",
				null, null, null, null);
	}
	
	//insert markerInfo
	public void insertMarkerInfo(String mId ,String mName ,String mlatitude ,String mlongitude ,String mType ,String mDesc ,String eId ) {
		ContentValues newData = new ContentValues();
		newData.put("mId", mId);
		newData.put("mName", mName);
		newData.put("mlatitude", mlatitude);
		newData.put("mlongitude", mlongitude);
		newData.put("mType", mType);
		newData.put("mDesc", mDesc);
		newData.put("eId", eId);
		
		open(); // open the database
		database.insert("marker", null, newData);
		close(); // close the database
	}
	
	//update markerInfo
	public void updateMarkerInfo(String mId ,String mName ,String mlatitude ,String mlongitude ,String mType ,String mDesc ,String eId ) {
		ContentValues newData = new ContentValues();
		newData.put("mId", mId);
		newData.put("mName", mName);
		newData.put("mlatitude", mlatitude);
		newData.put("mlongitude", mlongitude);
		newData.put("mType", mType);
		newData.put("mDesc", mDesc);
		newData.put("eId", eId);
		
		open(); // open the database
		database.update("marker", newData,"mId = '"+ mId+"'",null);
		close(); // close the database
	}
	
	// return all marker Location
	public Cursor getAllMarkerLocation(String eId) {
		return database.query("marker", new String[] { "mId","mName", "mlatitude", "mlongitude", "mType", "mDesc"}, "eId = '"+ eId+"'",
				null, null, null, null);
	}
	
	// return single markerInfo
	public Cursor getOneMarkerInfo(String mId) {
		open();
		return database.query("marker", new String[] { "mName", "mlatitude", "mlongitude", "mType", "mDesc", "eId" }, "mId = '"+mId+"'",
				null, null, null, null);
	}
	
	private class DatabaseOpenHelper extends SQLiteOpenHelper {
		// public constructor
		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		} // end DatabaseOpenHelper constructor

		public void onCreate(SQLiteDatabase db) {
			String createQuery = "CREATE TABLE message"
					+ "(mId TEXT,DateTime TEXT, msgType TEXT, data TEXT, fromUser TEXT, toUser TEXT);";
			String createQuery2 = "CREATE TABLE userInfo"
					+ "(uId TEXT,uName TEXT,uPhone TEXT,uEmail TEXT,uGender TEXT,uType TEXT,longitude TEXT, latitude TEXT,eId TEXT,uPhoto TEXT);";
			String createQuery3 =  "CREATE TABLE specialMarker"
					+ "(mId TEXT,pId TEXT, des TEXT,type TEXT);";
			String createQuery4 =  "CREATE TABLE event"
					+ "(eId TEXT,eName TEXT,eDesc TEXT,eDate TEXT,ePlace TEXT);";
			String createQuery5 =  "CREATE TABLE marker"
					+ "(mId TEXT,mName TEXT,mlatitude TEXT,mlongitude TEXT,mType TEXT,mDesc TEXT, eId TEXT);";
			db.execSQL(createQuery);
			db.execSQL(createQuery2);
			db.execSQL(createQuery3);
			db.execSQL(createQuery4);
			db.execSQL(createQuery5);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		} // end method onUpgrade
	} // end class DatabaseOpenHelper
} // end class DatabaseConnector