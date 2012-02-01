package com.example.thor.threeby3;

import java.util.Arrays;
import android.app.Activity;
import android.graphics.LightingColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ThreeBy3Activity extends Activity implements SensorEventListener {
	public int player = R.string.you;
	public String playername = "X", startingplayer = "X";
	public int rows = 3, cols = 3, moves, xscore, oscore, tscore;
	public int pointcount[] = new int[8]; //R1,R2,R3,C1,C2,C3,DD,DU
	public boolean gameover = false;
	private SensorManager sensMgr;
	private Sensor accelerometer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
		sensMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        setContentView(R.layout.main);
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Player " + playername + "'s turn");
		TextView tvs = (TextView) findViewById(R.id.textViewScore);
		tvs.setText("Score:   X:0   O:0   Tie:0");
    }
    
    protected void onResume() {
    	super.onResume();
    	sensMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    protected void onPause() {
    	super.onPause();
    	sensMgr.unregisterListener(this);
    }

	public void claimSquare(View view) {
		if (gameover == true) {
			startOver();
		}
		else {
	    	Button button = (Button)view;
	    	if(button.getText().equals("")) {
	    		int pvalue;
	    		if (playername == "X") {
	    			pvalue = -1;
	    			button.getBackground().setColorFilter(new LightingColorFilter(0xFFEEEEEE, 0xFFFF0000));
	    	    	button.setText("X");
	    		}
	    		else {
	    			pvalue = 1;
	    			button.getBackground().setColorFilter(new LightingColorFilter(0xFFEEEEEE, 0xFF0000FF));
	    			button.setText("O");
	    		}
	    		switch(view.getId()) {
	    	      case R.id.TopLeft:
	    	    	  pointcount[0] += pvalue; //R1
	    	    	  pointcount[3] += pvalue; //C1
	    	    	  pointcount[6] += pvalue; //DD
	    	          break;
	    	      case R.id.TopMiddle: 
	    	    	  pointcount[0] += pvalue; //R1
	    	    	  pointcount[4] += pvalue; //C2
	    	          break;    	                            
	    	      case R.id.TopRight:
	    	    	  pointcount[0] += pvalue; //R1
	    	    	  pointcount[5] += pvalue; //C3
	    	    	  pointcount[7] += pvalue; //DU
	    	          break;
	    	      case R.id.MiddleLeft: 
	    	    	  pointcount[1] += pvalue; //R2
	    	    	  pointcount[3] += pvalue; //C1
	    	          break;
	    	      case R.id.MiddleMiddle:
	    	    	  pointcount[1] += pvalue; //R2
	    	    	  pointcount[4] += pvalue; //C2
	    	    	  pointcount[6] += pvalue; //DD
	    	    	  pointcount[7] += pvalue; //DU
	    	          break;
	    	      case R.id.MiddleRight: 
	    	    	  pointcount[1] += pvalue; //R2
	    	    	  pointcount[5] += pvalue; //C3
	    	          break;    	                            
	    	      case R.id.BottomLeft:
	    	    	  pointcount[2] += pvalue; //R3
	    	    	  pointcount[3] += pvalue; //C1
	    	    	  pointcount[7] += pvalue; //DU
	    	          break;
	    	      case R.id.BottomMiddle: 
	    	    	  pointcount[2] += pvalue; //R3
	    	    	  pointcount[4] += pvalue; //C2
	    	          break; 
	    	      case R.id.BottomRight: 
	    	    	  pointcount[2] += pvalue; //R3
	    	    	  pointcount[5] += pvalue; //C3
	    	    	  pointcount[6] += pvalue; //DD
	    	          break; 
	    	      default:
	    	          break;
	    	     }
				moves++;
				TextView tv = (TextView) findViewById(R.id.textView1);
				TextView tvs = (TextView) findViewById(R.id.textViewScore);
	    		if (checkForWinCondition()) {
	    			buttonGlow(pvalue * 3);
	    			if (playername == "X")
	    				xscore += 1;
	    			else
	    				oscore += 1;
	    			tv.setText("Player " + playername + " WINS!");
	    			tvs.setText("Click Any Square To Start New Game");
	    			gameover = true;
	    		}
	    		else {
	    			if (moves == rows * cols) {
	    				tv.setText("Game Over: Tie");
	    				tscore += 1;
	    				tvs.setText("Click Any Square To Start New Game");
	    				gameover = true;
	    			}
	    			else {
	    				player = (player == R.string.you) ? R.string.me : R.string.you;
	    				playername = (playername == "X") ? "O" : "X";
	    				tv.setText("Player " + playername + "'s turn");
	    			}
	    		}	
	    	}
    	}
    }
    
    public boolean checkForWinCondition() {
    	if (moves < (2 * rows - 1)) return false;    		
    	int check[] = (int[])pointcount.clone();
    	Arrays.sort(check);
    	if (check[0] == -3 || check[7] == 3)
    		return true;    		
    	return false;
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.replay, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.startOver:
    		startOver();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

	private void startOver() {
		Button button = (Button) findViewById(R.id.TopLeft);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.TopMiddle);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.TopRight);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.MiddleLeft);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.MiddleMiddle);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.MiddleRight);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.BottomLeft);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.BottomMiddle);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.BottomRight);
		button.setText("");
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		gameover = false;
		moves = 0;
		Arrays.fill(pointcount, 0);
		startingplayer = (startingplayer == "X") ? "O" : "X";
		playername = startingplayer;
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Player " + playername + "'s turn");
		TextView tvs = (TextView) findViewById(R.id.textViewScore);
		tvs.setText("Score:   X:" + xscore + "   O:" + oscore + "   Tie:" + tscore);
		this.player = R.string.you;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		return;		
	}

	public void onSensorChanged(SensorEvent event) {
		if(event.values[0] > 5) {
		  startOver();
		}
		return;
	}
	
	public void buttonGlow(int winner) {
		int glowcolor;
		if (winner == 3)
			glowcolor = 0xFF0000FF;
		else
			glowcolor = 0xFFFF0000;
		Button button;
		for (int p = 0; p < 8; p++) {
			if (pointcount[p] == winner) {
				switch(p) {
	    	      	case 0:
	    	      		button = (Button) findViewById(R.id.TopLeft);
	    	      		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
	    	      		button = (Button) findViewById(R.id.TopMiddle);
	    	      		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
	    	      		button = (Button) findViewById(R.id.TopRight);
	    	      		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
	    	      		break;
	    	      	case 1: 
		    	    	button = (Button) findViewById(R.id.MiddleLeft);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.MiddleMiddle);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.MiddleRight);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		break;    	                            
	    	      	case 2:
	    	      		button = (Button) findViewById(R.id.BottomLeft);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.BottomMiddle);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.BottomRight);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
	    	      		break;
	    	      	case 3: 
	    	      		button = (Button) findViewById(R.id.TopLeft);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.MiddleLeft);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.BottomLeft);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		break;
	    	      	case 4:
	    	      		button = (Button) findViewById(R.id.TopMiddle);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.MiddleMiddle);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.BottomMiddle);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		break;
	    	      	case 5: 
	    	      		button = (Button) findViewById(R.id.TopRight);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.MiddleRight);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.BottomRight);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		break;    	                            
	    	      	case 6:
	    	      		button = (Button) findViewById(R.id.TopLeft);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.MiddleMiddle);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.BottomRight);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		break;
	    	      	case 7: 
	    	      		button = (Button) findViewById(R.id.TopRight);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.MiddleMiddle);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		button = (Button) findViewById(R.id.BottomLeft);
		    	  		button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
		    	  		break; 
	    	      default:
	    	          break;
	    	     }
			}
		}
	}
}