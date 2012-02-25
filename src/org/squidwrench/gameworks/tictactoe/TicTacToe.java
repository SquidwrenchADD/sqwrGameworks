package org.squidwrench.gameworks.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class TicTacToe extends Activity implements SensorEventListener {
	public String playername = "X", startingplayer = "X";
	public int rows = 3, cols = 3, moves, moveslimit = rows * cols, xscore, oscore, tscore, toechosen;
	public int pointcount[] = new int[8]; //R1,R2,R3,C1,C2,C3,DD,DU = ways to win
	public int squaremoves[] = new int[13]; //0 = first move, positive = O, negative = X, TL,TM,TR,ML,MM,MR,BL,BM,BR
	public boolean gameover = false, computeropponent = false, toe = false;
	public final int row1[] =  {R.id.TopLeft,		R.id.TopRight,		R.id.TopMiddle};
	public final int row2[] =  {R.id.MiddleMiddle,R.id.MiddleLeft,	R.id.MiddleRight};
	public final int row3[] =  {R.id.BottomLeft,	R.id.BottomRight,	R.id.BottomMiddle};
	public final int col1[] =  {R.id.TopLeft,		R.id.BottomLeft,	R.id.MiddleLeft};
	public final int col2[] =  {R.id.MiddleMiddle,R.id.TopMiddle,		R.id.BottomMiddle};
	public final int col3[] =  {R.id.TopRight,	R.id.BottomRight,	R.id.MiddleRight};
	public final int ddown[] = {R.id.MiddleMiddle,R.id.TopLeft,		R.id.BottomRight};
	public final int dup[] =   {R.id.MiddleMiddle,R.id.TopRight,		R.id.BottomLeft};
	public final int rcd[][] = {row1,row2,row3,col1,col2,col3,ddown,dup};
	public final int tls[] = {0,3,6}, tms[] = {0,4}, trs[] = {0,5,7};
	public final int mls[] = {1,3}, mms[] = {1,4,6,7}, mrs[] = {1,5};
	public final int bls[] = {2,3,7}, bms[] = {2,4}, brs[] = {2,5,6};
	public final int inTrio[][] = {tls,tms,trs,mls,mms,mrs,bls,bms,brs};
	public final List<Integer> squares = new ArrayList<Integer>(Arrays.asList(R.id.TopLeft,R.id.TopMiddle,R.id.TopRight,R.id.MiddleLeft,R.id.MiddleMiddle,R.id.MiddleRight,R.id.BottomLeft,R.id.BottomMiddle,R.id.BottomRight)); //TL,TM,TR,ML,MM,MR,BL,BM,BR
	public List<Integer> squaresremaining = new ArrayList<Integer>(Arrays.asList(R.id.TopLeft,R.id.TopMiddle,R.id.TopRight,R.id.MiddleLeft,R.id.MiddleMiddle,R.id.MiddleRight,R.id.BottomLeft,R.id.BottomMiddle,R.id.BottomRight)); //TL,TM,TR,ML,MM,MR,BL,BM,BR
	public List<Integer> squarestaken = new ArrayList<Integer>(); //TL,TM,TR,ML,MM,MR,BL,BM,BR
	private SensorManager sensMgr;
	private Sensor accelerometer;
    private static SoundPool sounds;
    private static int xbeep, obeep, gamewin, gametie; 
    //private static MediaPlayer music;
    private static boolean sound = true;
    private static MediaPlayer trashTalk;
    public Random rand = new Random();
	//private static final String TAG = "MyActivity"; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        //TableLayout tl = (TableLayout) findViewById(R.id.TableLayout1);
        //TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(tl.getWidth(), tl.getWidth());
        //tl.setLayoutParams(layoutParams);
        SharedPreferences settings = getSharedPreferences("PREF",0);
        sound = settings.getBoolean("sound", true);
		sensMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        setContentView(R.layout.main);
        trashTalk = MediaPlayer.create(this, R.raw.maybeyoushould);
        //xbeep = MediaPlayer.create(context, R.raw.ttt_x);
        //obeep = MediaPlayer.create(context, R.raw.ttt_o);
	    sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	    xbeep = sounds.load(this, R.raw.ttt_x, 1);
	    obeep = sounds.load(this, R.raw.ttt_o, 1);
	    gamewin = sounds.load(this, R.raw.gamewin, 1);
	    gametie = sounds.load(this, R.raw.gametie, 1);
	    //music = MediaPlayer.create(context, R.raw.something);
		showWhoseTurn();
		showScore();
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
	    CheckBox cbtoe =(CheckBox)findViewById(R.id.checkBoxToe);
	    cbtoe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	    		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxAI);
	    		if (arg1) {
	    			toe = true;
	    			if (cb.isChecked())
	    				cb.setChecked(false);
	    			cb.setClickable(false);
	    			cb.setTextColor(Color.GRAY);
	    		}
	    		else {
	    			toe = false;
    				cb.setClickable(true);
    				cb.setTextColor(Color.WHITE);
	    		}
	    		if (moves > 0)
	    			startOver();
	    	}
	    });     
    }
    
    protected void onResume() {
    	super.onResume();
    	loadGame();
    	sensMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    
    protected void onPause() {
    	super.onPause();
    	saveGame();
    	sensMgr.unregisterListener(this);
    }
    
    protected void onSaveInstanceState (Bundle outState) {
    	
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
	    		if (toe) {
	    			if  (moves == 2 || moves == 5) {
	    				int randomsquare = rand.nextInt(squarestaken.size());
	    				toechosen = squarestaken.get(randomsquare);
	    				moveslimit += 1;
	    			}
	    		}
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
	    			//playObeep();
	    		}
	    		int squareid = view.getId();
	    		
	    		for (Integer sqtrio : inTrio[squares.indexOf(squareid)])
	    			pointcount[sqtrio] += pvalue;
	    		
  	    	  	squaresremaining.remove(new Integer(squareid));
  	    	  	squarestaken.add(new Integer(squareid));
  	    	  	squaremoves[moves] = pvalue * squareid;
				moves += 1;				
				TextView tv = (TextView) findViewById(R.id.textView1);
				TextView tvs = (TextView) findViewById(R.id.textViewScore);
				if (moves > 4) {
		    		if (checkForWinCondition()) {
		    			playGamewin();
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
		    			if (moves == moveslimit) {
		    				playGametie();
		    				tv.setText("Game Over: Tie");
		    				tscore += 1;
		    				tvs.setText("Click Any Square To Start New Game");
		    				gameover = true;
		    				return;
		    			}
		    		}
				}
	    		if (playername.equals("X"))
	    			playXbeep();
	    		else
	    			playObeep();
	    		playername = (playername.equals("X")) ? "O" : "X";
	    		showWhoseTurn();
	    		if (playername.equals("O") && computeropponent == true)
	    			computerMove();
	    		
//	    		// SLEEP 1 SEC
//	    	    Handler handler = new Handler(); 
//	    	    handler.postDelayed(new Runnable() { 
//	    	         public void run() { 
//	    	        	 
//	    	        	 if (toe) {	
//	    		    		if (moves == 3 || moves == 6)
//	    		    			playToe();
//	    		    		if (moves == 4 || moves == 7) 		
//	    		    			cleanToe();
//	    		    	} 
//	    	         } 
//	    	    }, 1000); 
	    		
	    		if (toe) {	
	    			if (moves == 3 || moves == 6)
	    				playToe();
	    			if (moves == 4 || moves == 7) 		
	    				cleanToe();
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
		squaresremaining.clear();
		Collections.addAll(squaresremaining, R.id.TopLeft,R.id.TopMiddle,R.id.TopRight,R.id.MiddleLeft,R.id.MiddleMiddle,R.id.MiddleRight,R.id.BottomLeft,R.id.BottomMiddle,R.id.BottomRight);
		Button button;
		for (Integer squareid : squaresremaining) {
			button = (Button) findViewById(squareid);
			button.setText("");
			button.setTextColor(0xFF000000);
			button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
		}
		gameover = false;
		moves = 0;
		moveslimit = 9;
		Arrays.fill(pointcount, 0);
		Arrays.fill(squaremoves, 0);
		toechosen = 0;
		squarestaken.clear();
		startingplayer = (startingplayer.equals("X")) ? "O" : "X";
		playername = startingplayer;
		showWhoseTurn();
		showScore();
		if (startingplayer.equals("O") && computeropponent == true)
			computerMove();
	}

	public void showScore() {
		TextView tvs = (TextView) findViewById(R.id.textViewScore);
		tvs.setText("Score:   X:" + xscore + "   O:" + oscore + "   Tie:" + tscore);
		//String ss = Arrays.toString(pointcount);
		//tvs.setText(moves + " " + ss);
	}
	
	public void showWhoseTurn() {
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Player " + playername + "'s turn");
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
		int randomsquare = rand.nextInt(squaresremaining.size());
		//int randomsquare = (int) Math.ceil(Math.random() * (squaresremaining.size() - 1));
		Button button = (Button) findViewById(squaresremaining.get(randomsquare));
    	button.performClick();		
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
						for (Integer squareid : rcd[q]) {
	      					button = (Button) findViewById(squareid);
	      					if (button.getText().equals("")) {
	      						button.performClick();
	      						return;
	      					}	
						}
					}
				}
				
				for (int p = 0; p < 8; p++) { //look for X about to win and block
					if (pointcount[p] == -2) {
						for (Integer squareid : rcd[p]) {
	      					button = (Button) findViewById(squareid);
	      					if (button.getText().equals("")) {
	      						button.performClick();
	      						return;
	      					}	
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
						for (Integer squareid : rcd[d]) {
	      					button = (Button) findViewById(squareid);
	      					if (button.getText().equals("")) {
	      						button.performClick();
	      						return;
	      					}	
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
	
	public void popup(String debug) {
		CharSequence text = debug;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	public void popup(int debug) {
		CharSequence text = Integer.toString(debug);
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
 	public static void playComputerWinTrashTalk() {
	    if (!sound) return; // if sound is turned off no need to continue
		trashTalk.start();
	    //sounds.play(computerwintrashtalk, 1, 1, 1, 0, 1);
	}

	public static void playXbeep() {
	    if (!sound) return; // if sound is turned off no need to continue
	    	sounds.play(xbeep, 1, 1, 1, 0, 1);
		//xbeep.start();
	}
	
	public static void playObeep() {
	    if (!sound) return; // if sound is turned off no need to continue
	    	sounds.play(obeep, 1, 1, 1, 0, 1);
	    //obeep.start();
	}
	
	public static void playGamewin() {
	    if (!sound) return; // if sound is turned off no need to continue
	    	sounds.play(gamewin, 1, 1, 1, 0, 1);
	    //obeep.start();
	}
	
	public static void playGametie() {
	    if (!sound) return; // if sound is turned off no need to continue
	    	sounds.play(gametie, 1, 1, 1, 0, 1);
	    //obeep.start();
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
				for (Integer squareid : rcd[p]) {
					button = (Button) findViewById(squareid);
  					button.getBackground().setColorFilter(new LightingColorFilter(0xFF555555, glowcolor));
				}
			}
		}
	}

	public void saveGame() {		
	    SharedPreferences settings = getSharedPreferences("SAVEGAME", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("gmoves", Arrays.toString(squaremoves).replace("[", "").replace("]", "").replace(" ", ""));
		editor.putBoolean("gameover", gameover);
		editor.putString("playername", playername);
		editor.putBoolean("vscomputer", computeropponent);
	    editor.putBoolean("toe", toe);
	    editor.putInt("xwins", xscore);
	    editor.putInt("owins", oscore);
	    editor.putInt("twins", tscore);
	    editor.commit();
	}
	
	public void loadGame() {	
				
		String token = ",";
		
		boolean remsound = sound;
		sound = false;
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
		
        toe = settings.getBoolean("toe", toe);
	    CheckBox cbtoe = (CheckBox) findViewById(R.id.checkBoxToe);
		if (toe) {
			if (!cbtoe.isChecked()) {
				cbtoe.setChecked(true);
			}
		}
		else {
			if (cbtoe.isChecked())
				cbtoe.setChecked(false);
		}
		
		startOver();
		
		String smoves = settings.getString("gmoves", "");
        int[] convertedIntArray = StringToArrayConverter.convertTokenizedStringToIntArray(smoves, token);
        
        boolean toehold = toe;
        boolean comphold = computeropponent;
                
        toe = false;
        computeropponent = false;
    
        Button button;
        Integer mov;
        for (int s = 0; s < 13; s++) {
            mov = convertedIntArray[s];
            if (mov == 0) 
            	break;
            if (mov < 0)
            	playername = "X";
            else
            	playername = "O";
            if (toehold) {
            	if (moves == 3 || moves == 6) {
	            	toechosen = convertedIntArray[s];
	            	playToe();
            	}
            	else if (moves == 4 || moves == 7)
            		cleanToe();
            	else {
            		button = (Button) findViewById(Math.abs(mov));
    	        	button.performClick();
            	}
            }
	        else {
	        	button = (Button) findViewById(Math.abs(mov));
	        	button.performClick();
	        }
        }
        
        sound = remsound;
        toe = toehold;
        computeropponent = comphold;
        
        xscore = Math.max(settings.getInt("xwins",xscore),xscore);
        oscore = Math.max(settings.getInt("owins",oscore),oscore);
        tscore = Math.max(settings.getInt("twins",tscore),tscore);
        showScore();
	
		gameover = settings.getBoolean("gameover", gameover);
		if (!gameover) {
			playername = settings.getString("playername", playername);
			showWhoseTurn();
    		if (playername.equals("O") && computeropponent == true) {
    			computerMove();		
    		}
		}
	}
	
	public void cleanToe() {
//		Button button = (Button) findViewById(toechosen);
//		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
//		button.setText("");
		squaresremaining.add(new Integer(toechosen));
		squarestaken.remove(new Integer(toechosen));
		
		// SLEEP 1 SEC
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	Button button = (Button) findViewById(toechosen);
	        	button.setText("");
	     		button.getBackground().setColorFilter(new LightingColorFilter(0xFFF8F8F8, 0));
	         } 
	    }, 1000); 
	}

	public void playToe() {
	 	Button button = (Button) findViewById(toechosen);
	 	int pvalue = 0;
		if (button.getText().equals("X")) 
   			pvalue = 1;
   		else if (button.getText().equals("O"))
   			pvalue = -1;
		
		for (Integer sqtrio : inTrio[squares.indexOf(toechosen)])
			pointcount[sqtrio] += pvalue;
		
		squaremoves[moves] = toechosen;
//		button.setText("T");
//		button.getBackground().setColorFilter(new LightingColorFilter(0xFFEEEEEE, 0xFF00FF00));
		
		// SLEEP 1 SEC
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	 Button button = (Button) findViewById(toechosen);
	        	 button.setText("T");
	     		 button.getBackground().setColorFilter(new LightingColorFilter(0xFFEEEEEE, 0xFF00FF00));
	         } 
	    }, 1000); 
 	}
 }
 