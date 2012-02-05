package com.example.thor.threeby3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.media.AudioManager;
import android.media.MediaPlayer;
//import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ThreeBy3Activity extends Activity implements SensorEventListener {
	public int player = R.string.you;
	public String playername = "X", startingplayer = "X";
	public int rows = 3, cols = 3, moves, xscore, oscore, tscore;
	public int squarecount = rows * cols;
	public int pointcount[] = new int[8]; //R1,R2,R3,C1,C2,C3,DD,DU = ways to win
	public int squaremoves[] = new int[9]; //0 = first move, positive = O, negative = X, TL=1,TM,TR,ML,MM,MR,BL,BM,BR=9
	public boolean gameover = false, computeropponent = false;
	public List<Integer> squaresremaining = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8)); //TL,TM,TR,ML,MM,MR,BL,BM,BR
	private SensorManager sensMgr;
	private Sensor accelerometer;
    //private static SoundPool sounds;
    //private static int computerwintrashtalk;
    //private static MediaPlayer music;
    private static boolean sound = true;
    private static MediaPlayer trashTalk;
	//private static final String TAG = "MyActivity"; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        SharedPreferences settings = getSharedPreferences("PREF",0);
        sound = settings.getBoolean("sound", true);
		sensMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        setContentView(R.layout.main);
        Context context = getApplicationContext();
        trashTalk = MediaPlayer.create(context, R.raw.maybeyoushould);
	    //sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	    //computerwintrashtalk = sounds.load(context, R.raw.maybeyoushould, 1);
	    //music = MediaPlayer.create(context, R.raw.something);
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Player " + playername + "'s turn");
		TextView tvs = (TextView) findViewById(R.id.textViewScore);
		tvs.setText("Score:   X:0   O:0   Tie:0");
		CheckBox cbAI =(CheckBox)findViewById(R.id.checkBoxAI);
	    cbAI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	    		if (arg1) {
	    			computeropponent = true;
	    			if (playername.equals("O"))
	       				computerMove();	
	    		}
	    		else {
	    			computeropponent = false;
	    		}
	    	}
	    });     
    }
    
    protected void onResume() {
    	super.onResume();
    	sensMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    
    protected void onPause() {
    	super.onPause();
    	sensMgr.unregisterListener(this);
    }
    
    //When button is clicked
	public void claimSquare(View view) {
		if (gameover == true) {
			startOver();
		}
		else {
	    	Button button = (Button)view;
	    	if(button.getText().equals("")) {
	    		int pvalue;
	    		if (playername.equals("X")) {
	    			pvalue = -1;
	    			button.getBackground().setColorFilter(new LightingColorFilter(0xFFEEEEEE, 0xFFFF0000));
	    	    	button.setText("X");
	    		}
	    		else {
	    			pvalue = 1;
	    			if (computeropponent == true)
	    				button.setTextColor(0xFFA4C639);
	    			button.getBackground().setColorFilter(new LightingColorFilter(0xFFEEEEEE, 0xFF0000FF));
	    			button.setText("O");
	    		}
	    		switch(view.getId()) {
	    	      case R.id.TopLeft:
	    	    	  pointcount[0] += pvalue; //R1
	    	    	  pointcount[3] += pvalue; //C1
	    	    	  pointcount[6] += pvalue; //DD
	    	    	  squaresremaining.remove(new Integer(0));
	    	    	  squaremoves[moves] = pvalue;
	    	          break;
	    	      case R.id.TopMiddle: 
	    	    	  pointcount[0] += pvalue; //R1
	    	    	  pointcount[4] += pvalue; //C2
	    	    	  squaresremaining.remove(new Integer(1));
	    	    	  squaremoves[moves] = pvalue * 2;
	    	          break;    	                            
	    	      case R.id.TopRight:
	    	    	  pointcount[0] += pvalue; //R1
	    	    	  pointcount[5] += pvalue; //C3
	    	    	  pointcount[7] += pvalue; //DU
	    	    	  squaresremaining.remove(new Integer(2));
	    	    	  squaremoves[moves] = pvalue * 3;
	    	          break;
	    	      case R.id.MiddleLeft: 
	    	    	  pointcount[1] += pvalue; //R2
	    	    	  pointcount[3] += pvalue; //C1
	    	    	  squaresremaining.remove(new Integer(3));
	    	    	  squaremoves[moves] = pvalue * 4;
	    	          break;
	    	      case R.id.MiddleMiddle:
	    	    	  pointcount[1] += pvalue; //R2
	    	    	  pointcount[4] += pvalue; //C2
	    	    	  pointcount[6] += pvalue; //DD
	    	    	  pointcount[7] += pvalue; //DU
	    	    	  squaresremaining.remove(new Integer(4));
	    	    	  squaremoves[moves] = pvalue * 5;
	    	          break;
	    	      case R.id.MiddleRight: 
	    	    	  pointcount[1] += pvalue; //R2
	    	    	  pointcount[5] += pvalue; //C3
	    	    	  squaresremaining.remove(new Integer(5));
	    	    	  squaremoves[moves] = pvalue * 6;
	    	          break;    	                            
	    	      case R.id.BottomLeft:
	    	    	  pointcount[2] += pvalue; //R3
	    	    	  pointcount[3] += pvalue; //C1
	    	    	  pointcount[7] += pvalue; //DU
	    	    	  squaresremaining.remove(new Integer(6));
	    	    	  squaremoves[moves] = pvalue * 7;
	    	          break;
	    	      case R.id.BottomMiddle: 
	    	    	  pointcount[2] += pvalue; //R3
	    	    	  pointcount[4] += pvalue; //C2
	    	    	  squaresremaining.remove(new Integer(7));
	    	    	  squaremoves[moves] = pvalue * 8;
	    	          break; 
	    	      case R.id.BottomRight: 
	    	    	  pointcount[2] += pvalue; //R3
	    	    	  pointcount[5] += pvalue; //C3
	    	    	  pointcount[6] += pvalue; //DD
	    	    	  squaresremaining.remove(new Integer(8));
	    	    	  squaremoves[moves] = pvalue * 9;
	    	          break; 
	    	      default:
	    	          break;
	    	     }
				moves++;
				TextView tv = (TextView) findViewById(R.id.textView1);
				TextView tvs = (TextView) findViewById(R.id.textViewScore);
				if (moves > 4) {
		    		if (checkForWinCondition()) {
		    			buttonGlow(pvalue * 3);
		    			if (playername.equals("X"))
		    				xscore += 1;
		    			else {
		    				oscore += 1; 
		    				if (computeropponent == true)
		    					playComputerWinTrashTalk();
		    			}
		    			tv.setText("Player " + playername + " WINS!");
		    			tvs.setText("Click Any Square To Start New Game");
		    			gameover = true;
		    			return;
		    		}
		    		else {
		    			if (moves == squarecount) {
		    				tv.setText("Game Over: Tie");
		    				tscore += 1;
		    				tvs.setText("Click Any Square To Start New Game");
		    				gameover = true;
		    				return;
		    			}
		    		}
				}
	    		player = (player == R.string.you) ? R.string.me : R.string.you;
	    		playername = (playername.equals("X")) ? "O" : "X";
	    		tv.setText("Player " + playername + "'s turn");
	    		if (playername.equals("O") && computeropponent == true)
	    			computerMove();	
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
    		case R.id.saveGame:
    			saveGame();
    			return true;
    		case R.id.loadGame:
    			loadGame();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

	private void startOver() {
		Button button = (Button) findViewById(R.id.TopLeft);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.TopMiddle);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.TopRight);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.MiddleLeft);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.MiddleMiddle);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.MiddleRight);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.BottomLeft);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.BottomMiddle);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		button = (Button) findViewById(R.id.BottomRight);
		button.setText("");
		button.setTextColor(0xFF000000);
		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		gameover = false;
		moves = 0;
		Arrays.fill(pointcount, 0);
		Arrays.fill(squaremoves, 0);
		squaresremaining.clear();
		Collections.addAll(squaresremaining, 0,1,2,3,4,5,6,7,8);
		startingplayer = (startingplayer.equals("X")) ? "O" : "X";
		playername = startingplayer;
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Player " + playername + "'s turn");
		TextView tvs = (TextView) findViewById(R.id.textViewScore);
		tvs.setText("Score:   X:" + xscore + "   O:" + oscore + "   Tie:" + tscore);
		if (playername.equals("O") && computeropponent == true)
			computerMove();
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
	

	public void randomMove() {
		Button button;
		int randomsquare = (int) Math.ceil(Math.random() * (squaresremaining.size() - 1));
		switch(squaresremaining.get(randomsquare)) {
      		case 0:
      			button = (Button) findViewById(R.id.TopLeft);
      			button.performClick();
      			break;
      		case 1:
      			button = (Button) findViewById(R.id.TopMiddle);
      			button.performClick();
      			break;
      		case 2:
      			button = (Button) findViewById(R.id.TopRight);
      			button.performClick();
      			break;
      		case 3: 
      			button = (Button) findViewById(R.id.MiddleLeft);
      			button.performClick();
      			break;
      		case 4:		
      			button = (Button) findViewById(R.id.MiddleMiddle);
      			button.performClick();
      			break;
      		case 5:		
      			button = (Button) findViewById(R.id.MiddleRight);
      			button.performClick();
      			break;
      		case 6:
      			button = (Button) findViewById(R.id.BottomLeft);
      			button.performClick();
      			break;
      		case 7:	
      			button = (Button) findViewById(R.id.BottomMiddle);
      			button.performClick();
      			break;
      		case 8: 
      			button = (Button) findViewById(R.id.BottomRight);
      			button.performClick();
      			break;
      		default:
      			break;
		}			
	}
	

	public void computerMove() {
		Button button;
		//alternate between easy mode and hard mode depending on who's winning
		if (oscore >= xscore) { 
		//dumb AI - random selection
			randomMove();
			return;
		}
		else { 
		//smart AI 
			if (squaresremaining.size() == 9) {
      			button = (Button) findViewById(R.id.MiddleMiddle);
      			button.performClick();
      			return;
			}
			else {
				for (int q = 0; q < 8; q++) { //look for O about to win and complete
					if (pointcount[q] == 2) {
						switch(q) {
			      			case 0: //R1
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.TopMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 1: //R2
				      			button = (Button) findViewById(R.id.MiddleLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 2: //R3
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 3: //C1
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 4:	//C2	
				      			button = (Button) findViewById(R.id.TopMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 5:	//C3
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 6: //DD
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 7:	//DU
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		default:
				      			break;
						}		
					}
				}
				
				for (int p = 0; p < 8; p++) { //look for X about to win and block
					if (pointcount[p] == -2) {
						switch(p) {
			      			case 0: //R1
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.TopMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 1: //R2
				      			button = (Button) findViewById(R.id.MiddleLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 2: //R3
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 3: //C1
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 4:	//C2	
				      			button = (Button) findViewById(R.id.TopMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 5:	//C3
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 6: //DD
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 7:	//DU
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		default:
				      			break;
						}		
					}
				}
				
				//take center if possible
				button = (Button) findViewById(R.id.MiddleMiddle);
      			if (button.getText().equals("")) {
      				button.performClick();
      				return;
      			}
      			
				for (int d = 0; d < 8; d++) { //look for O with one and add to it
					if (pointcount[d] == 1) {
						switch(d) {
			      			case 0: //R1
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.TopMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 1: //R2
				      			button = (Button) findViewById(R.id.MiddleLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 2: //R3
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 3: //C1
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 4:	//C2	
				      			button = (Button) findViewById(R.id.TopMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomMiddle);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 5:	//C3
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.MiddleRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 6: //DD
				      			button = (Button) findViewById(R.id.TopLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		case 7:	//DU
				      			button = (Button) findViewById(R.id.TopRight);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			button = (Button) findViewById(R.id.BottomLeft);
				      			if (button.getText().equals("")) {
				      				button.performClick();
				      				return;
				      			}
				      			break;
				      		default:
				      			break;
						}		
					}
				}
				//take corner if possible
				button = (Button) findViewById(R.id.TopLeft);
      			if (button.getText().equals("")) {
      				button.performClick();
      				return;
      			}
      			button = (Button) findViewById(R.id.TopRight);
      			if (button.getText().equals("")) {
      				button.performClick();
      				return;
      			}
      			button = (Button) findViewById(R.id.BottomLeft);
      			if (button.getText().equals("")) {
      				button.performClick();
      				return;
      			}
      			button = (Button) findViewById(R.id.BottomRight);
      			if (button.getText().equals("")) {
      				button.performClick();
      				return;
      			}
      			//might not ever get here
      			randomMove();
			}
		}
	}
	
	public static void playComputerWinTrashTalk() {
	    if (!sound) return; // if sound is turned off no need to continue
		trashTalk.start();
	    //sounds.play(computerwintrashtalk, 1, 1, 1, 0, 1);
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


	public void saveGame() {	
		
		if (moves > 0) {	
	        SharedPreferences settings = getSharedPreferences("SAVEGAME", 0);
	        SharedPreferences.Editor editor = settings.edit();
	        editor.putString("gmoves", Arrays.toString(squaremoves).replace("[", "").replace("]", "").replace(" ", ""));
			editor.putBoolean("vscomputer", computeropponent);
		    editor.putBoolean("gameover", gameover);
		    editor.putString("playername", playername);
		    editor.commit();
		}
	}
	
	public void loadGame() {	
				
		String token = ",";
        SharedPreferences settings = getSharedPreferences("SAVEGAME", 0);
        
        computeropponent = settings.getBoolean("vscomputer", computeropponent);
	    CheckBox cb = (CheckBox) findViewById(R.id.checkBoxAI);
		if (computeropponent) {
			if (!cb.isChecked()) {
				playername = "X";
				cb.setChecked(true);
			}
		}
		else {
			if (cb.isChecked())
				cb.setChecked(false);
		}
		
		startOver();
		
		String smoves = settings.getString("gmoves", "");
        int[] convertedIntArray = StringToArrayConverter.convertTokenizedStringToIntArray(smoves, token);
    
        Button button;
        Integer mov;
        for (int s = 0; s < 9; s++) {
            mov = convertedIntArray[s];
            if (mov < 0)
            	playername = "X";
            else
            	playername = "O";
            switch(Math.abs(mov) - 1) {
				case 0:
					button = (Button) findViewById(R.id.TopLeft);
					button.performClick();
					break;
				case 1:
					button = (Button) findViewById(R.id.TopMiddle);
					button.performClick();
					break;
				case 2:
					button = (Button) findViewById(R.id.TopRight);
					button.performClick();
					break;
				case 3: 
					button = (Button) findViewById(R.id.MiddleLeft);
					button.performClick();
					break;
				case 4:		
					button = (Button) findViewById(R.id.MiddleMiddle);
					button.performClick();
					break;
				case 5:		
					button = (Button) findViewById(R.id.MiddleRight);
					button.performClick();
					break;
				case 6:
					button = (Button) findViewById(R.id.BottomLeft);
					button.performClick();
					break;
				case 7:	
					button = (Button) findViewById(R.id.BottomMiddle);
					button.performClick();
					break;
				case 8: 
					button = (Button) findViewById(R.id.BottomRight);
					button.performClick();
					break;
	      		default:
	      			break;
			}  
        }
	
		gameover = settings.getBoolean("gameover", gameover);
		if (!gameover) {
			TextView tv = (TextView) findViewById(R.id.textView1);
			playername = settings.getString("playername", playername);
			tv.setText("Player " + playername + "'s turn");
    		if (playername.equals("O") && computeropponent == true) {
    			computerMove();		
    		}
		}
	}
}