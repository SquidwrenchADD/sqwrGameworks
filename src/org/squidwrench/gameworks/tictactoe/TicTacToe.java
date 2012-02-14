package org.squidwrench.gameworks.tictactoe;

import com.example.thor.threeby3.R;
import android.app.Activity;
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

public class TicTacToe extends Activity implements SensorEventListener {
	public int player = R.string.you;
	private SensorManager sensMgr;
	private Sensor accelerometer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
		sensMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        setContentView(R.layout.main);
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
    	Button button = (Button)view;
    	if(button.getText().equals(getString(R.string.unused))) {
    		button.setText(getString(this.player));
    		checkForWinCondition();
    		player = (player == R.string.you) ? R.string.me : R.string.you;
    	}
    }
    
    public boolean checkForWinCondition() {
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
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.TopMiddle);
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.TopRight);
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.MiddleLeft);
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.MiddleMiddle);
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.MiddleRight);
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.BottomLeft);
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.BottomMiddle);
		button.setText(getString(R.string.unused));
		button = (Button) findViewById(R.id.BottomRight);
		button.setText(getString(R.string.unused));
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
}
