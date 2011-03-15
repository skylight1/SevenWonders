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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	
	private GameLevel currentLevel;
	
	private int latestScore;

	private int latestRemainingTimeSeconds=250;
		
    //Handler to draw debug info (fps) and countdown and end the game
    private Handler updateUiHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		// Ignore messages while finishing. This prevents starting multiple
    		// score activities, for example.
    		if (isFinishing()) {
    			return;
    		}
    		
    		if(mainLayout!=null) {
    			switch (msg.what) {
    				case START_RENDERING_MESSAGE:
    					// Remove splash screen
   				    	Log.i(TAG,"startedRendering()");
   						SoundTracks.getInstance().fadeoutSplashSoundTrack(SoundTracks.SOUNDTRACK);
   						splashView.setVisibility(View.GONE);
   		    			
   		    			// start the countdown NOW!
   		    			countdownStartTime = SystemClock.uptimeMillis();
   		    			sendUpdateCountdownMessage();
    					break;
    				case FPS_MESSAGE:
    					debugView.setText(Integer.toString(msg.arg1));
    					break;
    				case COUNTDOWN_MESSAGE:
    					latestRemainingTimeSeconds = TOTAL_TIME_ALLOWED - (int) ((SystemClock.uptimeMillis() - countdownStartTime)
								/ ONE_SECOND_IN_MILLISECONDS);
    					// Finish game if out of time.
    					if (latestRemainingTimeSeconds < 0) {
    						 //finish();
    						changeToScoreActivity(false);
    						break;
    					}
    					// Change time background color if running out of time.
    					if (latestRemainingTimeSeconds < 100) {
    						int backgroundColor = latestRemainingTimeSeconds %2 == 1 ? Color.YELLOW : Color.MAGENTA;
    						countdownView.setBackgroundColor(backgroundColor);
    					}
    					// Update time text view and send a delayed message to update again later.
    					countdownView.setText(Integer.toString(latestRemainingTimeSeconds));
    					sendUpdateCountdownMessage();    						
    					break;
    				case END_GAME_MESSAGE:
    					changeToScoreActivity(false);
    					break;
    				case NEW_SCORE_MESSAGE:
    					latestScore = msg.arg1;
						if (latestScore >= currentLevel.getNumberOfSpells()) {
							changeToScoreActivity(true);
						}
						TextView scoreTextView = (TextView) findViewById(R.id.score);
						scoreTextView.setText("" + latestScore);
						break;
    			}
    		}
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
		
		final int levelOrdinal = getIntent().getIntExtra(ScoreActivity.KEY_LEVEL_ORDINAL, 0);
		currentLevel = GameLevel.values()[levelOrdinal];

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

		final TextView scoreTextView = (TextView) findViewById(R.id.score);
		scoreTextView.setText("0");
		final TextView targetScoreTextView = (TextView) findViewById(R.id.targetScore);
		targetScoreTextView.setText("" + currentLevel.getNumberOfSpells());
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

	private void changeToScoreActivity(boolean wonLevel) {
		Intent intent = new Intent().setClass(PlayActivity.this, ScoreActivity.class);
		intent.putExtra(ScoreActivity.KEY_COLLECTED_SPELL_COUNT, latestScore); 
		intent.putExtra(ScoreActivity.KEY_REMAINING_TIME_SECONDS, latestRemainingTimeSeconds); 
		intent.putExtra(ScoreActivity.KEY_LEVEL_ORDINAL, currentLevel.ordinal()); 
		intent.putExtra(ScoreActivity.KEY_WON_LEVEL, wonLevel); 
		startActivity(intent);
		finish();
	} 
}