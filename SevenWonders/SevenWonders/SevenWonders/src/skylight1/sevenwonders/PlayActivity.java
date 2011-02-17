package skylight1.sevenwonders;

import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {

	public static final int FPS_MESSAGE = 0;
	public static final int COUNTDOWN_MESSAGE = 1;
	public static final int END_GAME_MESSAGE = 2;
	public static final int NEW_SCORE_MESSAGE = 3;
	public static final int START_RENDERING_MESSAGE = 4;

	protected static final int TOTAL_TIME_ALLOWED = 180;

	private static final long ONE_SECOND_IN_MILLISECONDS = 1000;

	private static final String TAG = PlayActivity.class.getName();

	private SevenWondersGLSurfaceView gLSurfaceView;
	
	private RelativeLayout mainLayout;
  
	private TextView countdownView;

	private long countdownStartTime;
    
	private ImageView splashView;
	
	private TextView debugView;
	
	private GameLevel currentLevel = GameLevel.FIRST;
	
	private int latestScore;

	private int latestRemainingTimeSeconds=250;
	
	
    //Handler to draw debug info (fps) and countdown and end the game
    private Handler updateUiHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if(mainLayout!=null) {
    			switch (msg.what) {
    				case START_RENDERING_MESSAGE:
    					// Remove splash screen
   				    	Log.i(TAG,"startedRendering()");
   						SoundTracks.getInstance().fadeoutSplashSoundTrack(SoundTracks.SOUNDTRACK);
   						splashView.setVisibility(View.GONE);
   		    			
   		    			// start the countdown NOW!
   		    			countdownStartTime = SystemClock.uptimeMillis();
   		    			//if(countdownStartTime<0){
   		    			sendUpdateCountdownMessage();
    					break;
    				case FPS_MESSAGE:
    					debugView.setText(Integer.toString(msg.arg1));
    					break;
    				case COUNTDOWN_MESSAGE:
    					latestRemainingTimeSeconds = TOTAL_TIME_ALLOWED - (int) ((SystemClock.uptimeMillis() - countdownStartTime)
								/ ONE_SECOND_IN_MILLISECONDS);
    					countdownView.setText(Integer.toString(latestRemainingTimeSeconds));
    					if(  latestRemainingTimeSeconds<100  )if(latestRemainingTimeSeconds%2==1)countdownView.setBackgroundColor(Color.YELLOW);
    					if(  latestRemainingTimeSeconds<100  )if(latestRemainingTimeSeconds%2==0) countdownView.setBackgroundColor(Color.MAGENTA);
    					if(latestRemainingTimeSeconds==0){finish();}
    					sendUpdateCountdownMessage();
    					break;
    				case END_GAME_MESSAGE:
    					changeToScoreActivity();
    					break;
    				case NEW_SCORE_MESSAGE:
    					latestScore = msg.arg1;
						if (latestScore >= currentLevel.getNumberOfSpellsRequired()) {
							changeToScoreActivity();
						}
						TextView scoreTextView = (TextView) findViewById(R.id.Score);
						scoreTextView.setText("" + latestScore);
						break;
    			
    			
    		}}
    		
    		};
    	};
    		
    		
    
private void sendUpdateCountdownMessage() {
		final Message countdownMessage = updateUiHandler.obtainMessage(COUNTDOWN_MESSAGE);
		updateUiHandler.sendMessageDelayed(countdownMessage, ONE_SECOND_IN_MILLISECONDS);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG,"onCreate()");

		SoundTracks.setEnabled(getIntent().getBooleanExtra("ENABLESOUND", true));
		SoundTracks soundTrack = SoundTracks.getInstance();
		soundTrack.init(getApplicationContext());		

		setContentView(R.layout.main);		
		countdownView = (TextView) findViewById(R.id.Countdown);
		debugView = (TextView) findViewById(R.id.FPS);
		mainLayout = (RelativeLayout) findViewById(R.id.RelativeLayout01);
		splashView = (ImageView) findViewById(R.id.splashView);;
		gLSurfaceView = (SevenWondersGLSurfaceView) findViewById(R.id.surfaceView);;
		gLSurfaceView.initialize(updateUiHandler, currentLevel);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
		Log.i(TAG,"onPause()");
		SoundTracks.getInstance().pause();
		gLSurfaceView.onPause();
	}
    
    @Override
	public void onResume() {
		super.onResume();
		Log.i(TAG,"onResume()");
		SoundTracks.getInstance().resume();
		gLSurfaceView.onResume();
	}
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG,"onDestroy()");
		if(SoundTracks.getInstance()!=null) {
			SoundTracks.getInstance().stop();
		}
	}

	private void changeToScoreActivity() {
		Intent intent = new Intent().setClass(PlayActivity.this, ScoreActivity.class);
		intent.putExtra(ScoreActivity.KEY_COLLECTED_SPELL_COUNT, latestScore); 
		intent.putExtra(ScoreActivity.KEY_REMAINING_TIME_SECONDS, latestRemainingTimeSeconds); 
		startActivity(intent);
		finish();
	}

 
    }

    



