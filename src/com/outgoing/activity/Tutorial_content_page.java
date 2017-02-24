package com.outgoing.activity;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import com.outgoing.R;
import com.outgoing.R.layout;
import com.outgoing.R.menu;
import com.outgoing.http.HttpRequests;
import com.outgoing.util.PreferenceUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v4.app.NavUtils;

public class Tutorial_content_page extends BaseActivity {
	private ImageView tut_iv;
	private String function;
	private AnimationDrawable animationDrawable;  
	private Handler handler = new Handler();	
	private Button tut_replay_button,tut_back_button;
	private boolean button_showed = false;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_content_page);
		
		tut_replay_button = (Button) findViewById(R.id.tut_replay_button);
		tut_back_button = (Button) findViewById(R.id.tut_back_button);
		
		tut_iv = (ImageView) findViewById(R.id.tut_iv);
		if(getIntent().getExtras()!=null){
			function = getIntent().getExtras().getString("function");
		}
		runAnim(); 
	}

	public void runAnim(){
		if(function.equals("Map Page")){
			tut_iv.setImageResource(R.drawable.tut1_animation);
			
		}
		if(function.equals("Search")){
			tut_iv.setImageResource(R.drawable.tut2_animation);  
		} 
		if(function.equals("Add Marker")){
			tut_iv.setImageResource(R.drawable.tut3_animation);  
		}
        animationDrawable = (AnimationDrawable) tut_iv.getDrawable(); 
        if(animationDrawable.isRunning()){
        	animationDrawable.stop();
        }  
        animationDrawable.start(); 

        int total_frame = animationDrawable.getNumberOfFrames();
        int total_time = 0;
        for(int i=0 ; i < total_frame ; i++){
        	total_time += animationDrawable.getDuration(i);
        }
        
        // set a timer to show button      
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                Tutorial_content_page.this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        setButton();
                    }
                });}};
        timer.schedule(timerTask, total_time);
	}
	
	public TimerTask setButton(){
		System.out.println("button_showed="+button_showed);
		if(button_showed){
			tut_replay_button.setVisibility(View.GONE);
			tut_back_button.setVisibility(View.GONE);
			button_showed = false;
		}else{
			tut_replay_button.setVisibility(View.VISIBLE);
			tut_back_button.setVisibility(View.VISIBLE);
			button_showed = true;
	        if(animationDrawable.isRunning()){
	        	animationDrawable.stop();
	        }
		}
		return null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial_content_page, menu);
		return true;
	}

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		
	}
	
	public void btnHandler(View view) {
		switch (view.getId()) {
		case R.id.tut_replay_button: {
			runAnim();
			setButton(); 
			break;
		}
		case R.id.tut_back_button: {
			finish();
			break;
		}
		default:
			break;
		}
	}

}
