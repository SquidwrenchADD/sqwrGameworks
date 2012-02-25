package org.squidwrench.gameworks.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Splash extends Activity{

	//show splash image and play jingle
	MediaPlayer splashMusic;
	@Override
	protected void onCreate(Bundle TTTTime) {
		super.onCreate(TTTTime);
		setContentView(R.layout.splash);
		splashMusic = MediaPlayer.create(Splash.this, R.raw.splashsound);
		splashMusic.start();
		Thread timer = new Thread(){
			@Override
			public void run(){
				try{
					sleep(3000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openStartingPoint = new Intent("org.squidwrench.gameworks.tictactoe.TICTACTOE");
					startActivity(openStartingPoint);
				}
			}
		};
		timer.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		splashMusic.release();
		finish();
	}
}
