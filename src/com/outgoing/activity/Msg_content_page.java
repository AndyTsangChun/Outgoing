package com.outgoing.activity;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.outgoing.R;
import com.outgoing.activity.Map_page.ClientThread;
import com.outgoing.model.ChatMsgEntity;
import com.outgoing.model.ChatMsgViewAdapter;
import com.outgoing.util.DatabaseConnector;

public class Msg_content_page extends BaseActivity {

	private String toUser,fromUser;
	private Button mBtnSend; 
	private TextView username_T;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private DatabaseConnector DBC;
	//MSG
	private static SocketIO socket = null;
	private boolean onPaused = false;
	IOAcknowledge ack;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_content_page);
		// �D動activity時不自動彈出軟鍵盤
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mListView = (ListView) findViewById(R.id.msg_listview);
		username_T = (TextView) findViewById(R.id.username_T);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		DBC = new DatabaseConnector(this);
		if(getIntent().getExtras()!=null){
			toUser = getIntent().getExtras().getString("toUser");
			fromUser = getIntent().getExtras().getString("fromUser");
		}
		initData();
		username_T.setText(fromUser);
		
		//Map_page.disconnectSocket();
		//new Thread(new ClientThread("172.19.127.160","8124",this)).start();
		new Thread(new ClientThread("119.246.80.6","8124",this)).start();
	}


	public void initData() {
		DBC.open();
		Cursor cMyMessage = DBC.getMyMessage(toUser);
		if (cMyMessage != null
				&& cMyMessage.getCount() > 0) {
			cMyMessage.moveToFirst();
			int indexToUser = cMyMessage.getColumnIndex("toUser");
			int indexFromUser = cMyMessage.getColumnIndex("fromUser");
			int indexType = cMyMessage.getColumnIndex("msgType");
			int indexData = cMyMessage.getColumnIndex("data");
			int indexTime = cMyMessage.getColumnIndex("DateTime");
			do {
				boolean user_exist = false;
				String c_type =  cMyMessage.getString(indexType); 
				String c_toUser =  cMyMessage.getString(indexToUser); 
				String c_fromUser =  cMyMessage.getString(indexFromUser); 
				String c_data =  cMyMessage.getString(indexData); 
				String c_time =  cMyMessage.getString(indexTime);
				
				ChatMsgEntity entity = new ChatMsgEntity();	
				if (c_fromUser.equals(fromUser) && c_toUser.equals(toUser)) {					
					entity.setName(fromUser);
					entity.setMsgType(true);
					entity.setDate(c_time);	
					entity.setText(c_data);
					mDataArrays.add(entity);
				}else if(c_fromUser.equals(toUser) && c_toUser.equals(fromUser)) {		
					entity.setName(toUser);
					entity.setMsgType(false);
					entity.setDate(c_time);	
					entity.setText(c_data);
					mDataArrays.add(entity);
				}	
			} while (cMyMessage.moveToNext());
		}
		DBC.close();

		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
	}

	public void btnHandler(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.back_Button:
			this.finish();
			break;
		case R.id.clear_all_button:
			DBC.deleteUserMessage(toUser, fromUser);
			DBC.deleteUserMessage(fromUser, toUser);
			mDataArrays.clear();
			mAdapter.notifyDataSetChanged();
		default:
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			Calendar calendar = Calendar.getInstance();
			entity.setDate(calendar.get(Calendar.DAY_OF_MONTH)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
			entity.setName(toUser);
			entity.setMsgType(false);
			entity.setText(contString);

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			IOAcknowledge ack = new IOAcknowledge() {
			    @Override
			    public void ack(Object... args) {
			        if (args.length > 0) {
			            Log.d("SocketIO", "" + args[0]);
			            // -> "hello"
			        }
			    }
			};
			DBC.insertMsg("msg", toUser, fromUser, contString);
			socket.emit("echo", ack, "msg;"+toUser+";"+fromUser+";"+contString);
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
		}
	}

	// 按下語音錄制按鈕時
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
			return false;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.msg_content_page, menu);
		return true;
	}
	
	
	public void onDestory(){
		super.onDestroy();
		socket.disconnect();
	}
	
	public void onPause(){
		super.onPause();
		onPaused = true;
	}

	public void onResume(){
		super.onResume();
		onPaused = false;
	}
	
	public void finish(){
		super.finish();
		socket.disconnect();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub

	}
	
	public void addMsg(String new_msg){
		ChatMsgEntity entity = new ChatMsgEntity();
		Calendar calendar = Calendar.getInstance();
		entity.setDate(calendar.get(Calendar.DAY_OF_MONTH)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
		entity.setName(fromUser); 
		entity.setMsgType(true);
		entity.setText(new_msg);
		mDataArrays.add(entity);
		runOnUiThread(new Runnable(){
			public void run() {
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);
		}});
	}
	
	@Override
	public void onBackPressed() {
		finish();	
	}
	
	
	class ClientThread implements Runnable{
		String input_ip;
		String input_port;
		Msg_content_page act;
		public ClientThread(String input_ip,String input_port, Msg_content_page act){
			super();
			this.act = act;
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
						if(msg_data.length>=4){
							System.out.println("logic2"+ msg_data[0].equals("msg") +";"+ !msg_data[1].equals(toUser));
							if(msg_data[0].equals("login")){
								
							}else if(msg_data[0].equals("msg") && !msg_data[1].equals(toUser)){
								if(onPaused)
									buildNotify(msg_data[3]);
								DBC.insertMsg(msg_data[0], msg_data[1], msg_data[2], msg_data[3]);
								System.out.println("Recevied:"+msg_data[0]+":"+msg_data[1]+":"+msg_data[2]+":"+msg_data[3]);
								act.addMsg(msg_data[3]);
							}
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
			    public void onDisconnect() {
			    	System.out.println("Disconnected");
			    }
			    @Override
			    public void onConnect() {
					socket.emit("echo", ack, "login;"+toUser+";null;null");
			    }
			});	
		}
		
	}
	private void buildNotify(String data){
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(this,Msg_list_page.class); 
        notifyIntent.putExtra("uID", toUser);
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
