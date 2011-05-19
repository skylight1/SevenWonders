package skylight1.sevenwonders;
import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import skylight1.sevenwonders.view.TextStyles;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayActivity extends Activity {
	private static final int MILLISECONDS_TO_SHOW_MESSAGE = 2500;
	public static final int FPS_MESSAGE = 0;
	public static final int COUNTDOWN_MESSAGE = 1;
	public static final int START_END_GAME_MESSAGE = 2;
	public static final int NEW_SCORE_MESSAGE = 3;
	public static final int START_RENDERING_MESSAGE = 4;
	protected static final int END_GAME_MESSAGE = 5;

	protected static final int TOTAL_TIME_ALLOWED_IN_SECONDS = 180;

	private static final int REMAINING_SECONDS_AFTER_WHICH_COUNTDOWN_FLASHES = 30;

	private static final long ONE_SECOND_IN_MILLISECONDS = 1000;

	private static final String TAG = PlayActivity.class.getName();

	private SevenWondersGLSurfaceView gLSurfaceView;

	private boolean gLSurfaceViewAdded;
	
	private RelativeLayout mainLayout;
  
	private TextView countdownView;
	
	private long lastGameTimeUptimeMillis;
    
	private View splashView;
	
	private TextView debugView;
	
	private GameLevel currentLevel;
	
	private int latestScore;

	private long remainingGameTimeMillis = TOTAL_TIME_ALLOWED_IN_SECONDS * ONE_SECOND_IN_MILLISECONDS;
	
	/** If the game has been paused by the menu button. */
	private boolean isGameManuallyPaused;
	
	private boolean isActivityPaused;
	
	private boolean isRenderingStarted;
		
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
   		    			
   		    			// start the countdown NOW!
						lastGameTimeUptimeMillis = SystemClock.uptimeMillis();
						isRenderingStarted = true;
   		    			sendUpdateCountdownMessage();
    					break;
    				case FPS_MESSAGE:
    					debugView.setText(Integer.toString(msg.arg1));
    					break;
    				case COUNTDOWN_MESSAGE:
    					if ( isGameTimeMoving() ) {
    						long gameTimeElapsedMillis = SystemClock.uptimeMillis() - lastGameTimeUptimeMillis;
    						remainingGameTimeMillis -= gameTimeElapsedMillis;
    						long remainingGameTimeSeconds = remainingGameTimeMillis / ONE_SECOND_IN_MILLISECONDS;
    						lastGameTimeUptimeMillis = SystemClock.uptimeMillis();
    						
	    					// Finish game if out of time.
	    					if (remainingGameTimeMillis < 0) {
	    						changeToScoreActivity(false);
	    						break;
	    					}
	    					// Change time background color if running out of time.
	    					if (remainingGameTimeSeconds < REMAINING_SECONDS_AFTER_WHICH_COUNTDOWN_FLASHES) {
	    						int backgroundColor = remainingGameTimeSeconds %2 == 1 ? Color.YELLOW : Color.RED;
	    						countdownView.setTextColor(backgroundColor);
	    					}
	    					// Update time text view.
	    					long minutes = remainingGameTimeSeconds / 60;
	    					long seconds = remainingGameTimeSeconds % 60;
	    					countdownView.setText(String.format("%d:%02d", minutes, seconds));
    					}
    					//send a delayed message to update again later.
    					sendUpdateCountdownMessage();    						
    					break;
    				case START_END_GAME_MESSAGE:
    					gLSurfaceView.togglePaused();
    					//TODO: add red tint or something
    					sendEndGameMessage();
    					break;
    				case END_GAME_MESSAGE:
    					if ( isGameTimeMoving() ) {
    						changeToScoreActivity(false);
    					} else {
    						sendEndGameMessage();
    					}
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
   		}
   	};

	private boolean isGameTimeMoving() {
		return isRenderingStarted 
			&& !isGameManuallyPaused 
			&& hasWindowFocus() 
			&& !isActivityPaused;
	};
	
    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		lastGameTimeUptimeMillis = SystemClock.uptimeMillis();
	}

	private void sendUpdateCountdownMessage() {
		final Message countdownMessage = updateUiHandler.obtainMessage(COUNTDOWN_MESSAGE);
		updateUiHandler.sendMessageDelayed(countdownMessage, ONE_SECOND_IN_MILLISECONDS);
	}

	protected void sendEndGameMessage() {
		final Message endGameMessage = updateUiHandler.obtainMessage(END_GAME_MESSAGE);
		updateUiHandler.sendMessageDelayed(endGameMessage, 2*ONE_SECOND_IN_MILLISECONDS);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Settings settings =  new Settings(this);
		settings.setGameWasStartedAtLeastOnceFlag();

		Log.i(TAG,"onCreate()");
		
		final int levelOrdinal = getIntent().getIntExtra(ScoreActivity.KEY_LEVEL_ORDINAL, 0);
		currentLevel = GameLevel.values()[levelOrdinal];
		
		SoundTracks.setEnabled(settings.isSoundEnabled());
		SoundTracks soundTrack = SoundTracks.getInstance();
		soundTrack.init(getApplicationContext());		

		setContentView(R.layout.main);	
		
		TextStyles textStyles = new TextStyles(this);
		countdownView = (TextView) findViewById(R.id.Countdown);
		textStyles.applyHeaderTextStyle(countdownView);
		
		debugView = (TextView) findViewById(R.id.FPS);
		
		mainLayout = (RelativeLayout) findViewById(R.id.RelativeLayout01);
		splashView = findViewById(R.id.splashView);
		
		TextView loadingTextView = (TextView) findViewById(R.id.loading_textview);
		textStyles.applyHeaderTextStyle(loadingTextView);
		
		final TextView scoreTextView = (TextView) findViewById(R.id.score);
		scoreTextView.setText("0");
		textStyles.applyHeaderTextStyle(scoreTextView);

		final TextView scoreDivider = (TextView) findViewById(R.id.scoreDivider);
		textStyles.applyHeaderTextStyle(scoreDivider);
		
		final TextView targetScoreTextView = (TextView) findViewById(R.id.targetScore);
		targetScoreTextView.setText("" + currentLevel.getNumberOfSpells());
		textStyles.applyHeaderTextStyle(targetScoreTextView);

		gLSurfaceView = new SevenWondersGLSurfaceView(this);

		final TextView loadingMessage = (TextView) findViewById(R.id.loading_textview);
		loadingMessage.setText(currentLevel.getLoadingMessage());
		final long timeMessageWasShown = System.currentTimeMillis();
		
		// load the OpenGL objects on another thread, not the UI thread, and not
		// before displaying the screen
		new Thread(new Runnable() {
			@Override
			public void run() {
				// run the expensive part on a thread other than the UI thread
				gLSurfaceView.loadLevel(updateUiHandler, currentLevel);
				
				final long timeSinceMessageWasShown = System.currentTimeMillis() - timeMessageWasShown;
				// if necessary, wait a while before dismissing the loading message for this level
				final long remainingTimeForLoadingMessage = MILLISECONDS_TO_SHOW_MESSAGE - timeSinceMessageWasShown;
				if (remainingTimeForLoadingMessage > 0) {
					try {
						Thread.sleep(remainingTimeForLoadingMessage);
					} catch (InterruptedException e) {
						// ignore this possibility
					}
				}
				
				// need to be back in the UI thread to actually add the surfaceview to the screen
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
						RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.RelativeLayout01);
						topLayout.addView(gLSurfaceView, 0, params);

						// hide the splash screen
						topLayout.removeView(splashView);

						// start the surface
						gLSurfaceView.initialize();
						gLSurfaceView.onResume();
						
						// safe to "resume" the surface view now
						gLSurfaceViewAdded = true;
					}
				});
			}
		}).start();
	}

	@Override
    protected void onPause() {
        super.onPause();
        isActivityPaused = true;
		Log.i(TAG,"onPause()");
		if(SoundTracks.getInstance()!=null) {
			SoundTracks.getInstance().pause();
		}
		if (gLSurfaceViewAdded) {
			gLSurfaceView.onPause();
		}
	}
    
    @Override
	public void onResume() {
		super.onResume();
		Log.i(TAG,"onResume()");
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		if(SoundTracks.getInstance()!=null) {
			SoundTracks.getInstance().resume();
		}
		splashView.setVisibility(View.VISIBLE);
		if (gLSurfaceViewAdded) {
			gLSurfaceView.onResume();
		}
		// If the game was paused, we need to update the countdown start time.
		lastGameTimeUptimeMillis = SystemClock.uptimeMillis();
		isActivityPaused = false;
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
		intent.putExtra(ScoreActivity.KEY_REMAINING_TIME_SECONDS, (int) (remainingGameTimeMillis / ONE_SECOND_IN_MILLISECONDS)); 
		intent.putExtra(ScoreActivity.KEY_LEVEL_ORDINAL, currentLevel.ordinal()); 
		intent.putExtra(ScoreActivity.KEY_WON_LEVEL, wonLevel); 
		startActivity(intent);
		finish();
	} 

	@Override
	public boolean onKeyDown(final int aKeyCode, final KeyEvent aEvent) {
		switch (aKeyCode) {		
			case KeyEvent.KEYCODE_MENU:
				lastGameTimeUptimeMillis = SystemClock.uptimeMillis();
				isGameManuallyPaused = !isGameManuallyPaused;
				gLSurfaceView.togglePaused();
				return true;
		}
		return super.onKeyDown(aKeyCode, aEvent);
	}
}