	package com.outgoing.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.outgoing.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * here we are going to use an AsyncTask to download the image 
 *      in background while showing the download progress
 * */

public class downloadPhoto extends AsyncTask<Void, Integer, Void> {

	private static String name;
	private ImageView img;
	private static Bitmap bmp;
	private TextView percent;
	private static Activity act;
	private static FileOutputStream fos;

	/*--- constructor ---*/
	public downloadPhoto(String name, ImageView img, Activity act) {
		/*--- we need to pass some objects we are going to work with ---*/
		this.name = name;
		this.img = img;
		this.act = act;
	}

	/*--- we need this interface for keeping the reference to our Bitmap from the MainActivity. 
	 *  Otherwise, bmp would be null in our MainActivity*/
	public interface ImageLoaderListener {

		void onImageDownloaded(Bitmap bmp);

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			bmp = saveImageToSD(name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

		/*--- show download progress on main UI thread---*/
		percent.setText(values[0] + "%");

		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		act.runOnUiThread(new Runnable(){
			public void run() {
				img.setImageBitmap(bmp);
		}});
		super.onPostExecute(result);
	}

	private static Bitmap saveImageToSD(String name) throws IOException {
		Bitmap dl_Bitmap;
		boolean not_found = false;
		File folder = new File(Environment.getExternalStorageDirectory() + "/Outgoing");
		if (!folder.exists()) {
		    folder.mkdir();
		}
		File imgFile = new  File(Environment.getExternalStorageDirectory()
	            + File.separator + "Outgoing/"+name);
		if(imgFile.exists()){
		    Bitmap founded_Bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    dl_Bitmap = founded_Bitmap;			
		    //System.out.println("local pic loaded");
		}else{
			URL url = new URL("http://tracksystem.zapto.org/OutingTrack/OutsideConnect/img/"+name);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        dl_Bitmap = BitmapFactory.decodeStream(input);
	        if(dl_Bitmap != null){
			    /*--- this method will save your downloaded image to SD card ---*/
		
			    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			    /*--- you can select your preferred CompressFormat and quality. 
			     * I'm going to use JPEG and 100% quality ---*/
			    dl_Bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			    /*--- create a new file on SD card ---*/
			    File file = new File(Environment.getExternalStorageDirectory()
			            + File.separator + "Outgoing/"+name);
			    try {
			        file.createNewFile();
			    } catch (IOException e) {
			        e.printStackTrace();
			        not_found = true;
			    }
			    /*--- create a new FileOutputStream and write bytes to file ---*/
			    try {
			        fos = new FileOutputStream(file);
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();
			        not_found = true;
			    }
			    try {
			        fos.write(bytes.toByteArray());
			        fos.close();
			    } catch (IOException e) {
			    	not_found = true;
			        e.printStackTrace();
			    }
	        }else{
	        	not_found = true;
	        }
		}
		if(not_found){
        	dl_Bitmap = BitmapFactory.decodeResource(act.getResources(),
                    R.drawable.img_select);
        }
		return dl_Bitmap;
	}
}
