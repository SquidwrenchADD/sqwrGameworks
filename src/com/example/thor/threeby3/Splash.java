package com.example.thor.threeby3;

import android.app.Activity;
import android.content.Intent;
//import android.media.MediaPlayer;
import android.os.Bundle;

public class Splash extends Activity{

//	MediaPlayer ourSong;
	@Override
	protected void onCreate(Bundle TTTTime) {
		super.onCreate(TTTTime);
//		ourSong = MediaPlayer.create(Splash.this, R.raw.tttsplash);
//		ourSong.start();
		setContentView(R.layout.splash);
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(3000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openStartingPoint = new Intent("com.example.thor.threeby3.THREEBY3ACTIVITY");
					startActivity(openStartingPoint);
				}
			}
		};
		timer.start();
	}
	
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		ourSong.release();
//		finish();
//	}
}
