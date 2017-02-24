package com.outgoing.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.outgoing.model.ObservableObject;
import com.outgoing.model.StatusUpdate;
import com.outgoing.model.StatusUpdate.Updates;;



public class HttpRequests extends AsyncTask<String, Void, String> {

	//private static final String BASE_URL = "http://www1.se.cuhk.edu.hk/~mobilep/mobile/function_remark.php";
	private static final String BASE_URL ="http://tracksystem.zapto.org/OutingTrack/OutsideConnect/mobile/moblie_call.php";
	//private static final String BASE_URL ="http://192.168.137.104/webpage/mobile/moblie_call.php";
	private static final String charset = "UTF-8";

	private String function = null;

	private ObservableObject observerObject = null;

	private Context context;

	private Observer observer;
	
	public HttpRequests(Observer observer, ProgressDialog dialog,
			Context context) {
		super();
		this.context = context;
		this.observer = observer;
		observerObject = new ObservableObject();
		observerObject.addObserver(observer);
	}

	@Override
	protected String doInBackground(String... params) {
		HttpURLConnection urlc = null;
		OutputStreamWriter out = null;
		DataOutputStream dataout = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		function = params[0];
		try {
			String query = processParams(params);
			System.out.println(query);
			URL url = new URL(BASE_URL);
			urlc = (HttpURLConnection) url.openConnection();
			urlc.setRequestMethod("POST");
			urlc.setConnectTimeout(10000);
	        urlc.setReadTimeout(10000);
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			urlc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			dataout = new DataOutputStream(urlc.getOutputStream());
			// perform POST operation
			dataout.write(query.getBytes(charset));
			int responseCode = urlc.getResponseCode();
			in = new BufferedReader(
					new InputStreamReader(urlc.getInputStream()), 8096);

			String response = null;
			// write html to System.out for debug
			while ((response = in.readLine()) != null) {
				result.append(response);
			}
			in.close();
			return result.toString();
		} catch (ProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onPostExecute(String response) {
		StatusUpdate update = null;
		try {
			System.out.println(this.function+" :response="+response);
			if (response != null && this.function != null && response !="") {
				JSONArray respond = new JSONArray(response);
				this.observerObject.setChanged();
				if (this.function.equals("login_request")) {
					update = new StatusUpdate(Updates.login_request, respond);
				}
				if (this.function.equals("get_user_info_request")) {
					update = new StatusUpdate(Updates.get_user_info_request, respond);
				}
				if (this.function.equals("update_user_location_request")) {
					update = new StatusUpdate(Updates.update_user_location_request, respond);
				}
				if (this.function.equals("update_user_info_request")) {
					update = new StatusUpdate(Updates.update_user_info_request, respond);
				}
				if (this.function.equals("get_event_info_request")) {
					update = new StatusUpdate(Updates.get_event_info_request, respond);
				}if (this.function.equals("location_remark")) {
					update = new StatusUpdate(Updates.location_remark, respond);
				}if (this.function.equals("add_marker_request")) {
					update = new StatusUpdate(Updates.add_marker_request, respond);
				}if (this.function.equals("get_marker_request")) {
					update = new StatusUpdate(Updates.get_marker_request, respond);
				}
				observerObject.notifyObservers(update);
			}else{
				this.observerObject.setChanged();
				update = new StatusUpdate(Updates.connection_fail, null);
				observerObject.notifyObservers(update);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private String processParams(String... params)
			throws UnsupportedEncodingException {
		String query = null;
		if (params[0].equals("check_version_request")) {
			query = String.format("function=%s&os=android&userID=%s",
					URLEncoder.encode("check_version_request", charset),
					URLEncoder.encode(params[1], charset),
					URLEncoder.encode(params[2], charset));
		} else if (params[0].equals("login_request")) {
			query = String.format("function=%s&loginName=%s&password=%s",
					URLEncoder.encode("login_request", charset),
					URLEncoder.encode(params[1], charset),
					URLEncoder.encode(params[2], charset));
		} else if (params[0].equals("get_user_info_request")) {
			query = String.format("function=%s",
					URLEncoder.encode("get_user_info_request", charset));
		} else if (params[0].equals("update_user_location_request")) {
			query = String.format("function=%s&memberNo=%s&latitude=%s&longitude=%s&type=%s",
					URLEncoder.encode("update_user_location_request", charset),
					URLEncoder.encode(params[1], charset),
					URLEncoder.encode(params[2], charset),
					URLEncoder.encode(params[3], charset),
					URLEncoder.encode(params[4], charset));
		}else if (params[0].equals("update_user_info_request")) {
			query = String.format("function=%s&memberNo=%s&memberName=%s&memberPhoneNo=%s&emailAddr=%s&memberGender=%s",
					URLEncoder.encode("update_user_info_request", charset),
					URLEncoder.encode(params[1], charset),
					URLEncoder.encode(params[2], charset),
					URLEncoder.encode(params[3], charset),
					URLEncoder.encode(params[4], charset),
					URLEncoder.encode(params[5], charset));
		} else if (params[0].equals("get_event_info_request")) {
			query = String.format("function=%s&eventNo=%s",
					URLEncoder.encode("get_event_info_request", charset),
					URLEncoder.encode(params[1], charset));
		} else if (params[0].equals("location_remark")) {
			query = String.format("function=%s&reMark=%s&memberNo=%s",
					URLEncoder.encode("location_remark", charset),
					URLEncoder.encode(params[1], charset),
					URLEncoder.encode(params[2], charset));
		}else if (params[0].equals("add_marker_request")) {
			query = String.format("function=%s&name=%s&type=%s&desc=%s&longitude=%s&latitude=%s&eventNo=%s",
					URLEncoder.encode("add_marker_request", charset),
					URLEncoder.encode(params[1], charset),
					URLEncoder.encode(params[2], charset),
					URLEncoder.encode(params[3], charset),
					URLEncoder.encode(params[4], charset),
					URLEncoder.encode(params[5], charset),
					URLEncoder.encode(params[6], charset));
		}else if (params[0].equals("get_marker_request")) {
			query = String.format("function=%s",
					URLEncoder.encode("get_marker_request", charset));
		}
		return query;
	}

	private String dealNull(String param) {
		if (param == null) {
			return "";
		}
		return param;
	}
}
