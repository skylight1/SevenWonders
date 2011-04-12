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
	public static final int FPS_MESSAGE = 0;
	public static final int COUNTDOWN_MESSAGE = 1;
	public static final int START_END_GAME_MESSAGE = 2;
	public static final int NEW_SCORE_MESSAGE = 3;
	public static final int START_RENDERING_MESSAGE = 4;
	protected static final int END_GAME_MESSAGE = 5;

	protected static final int TOTAL_TIME_ALLOWED = 180;

	private static final long ONE_SECOND_IN_MILLISECONDS = 1000;

	private static final String TAG = PlayActivity.class.getName();


	private SevenWondersGLSurfaceView gLSurfaceView;

	private boolean gLSurfaceViewAdded;
	
	private RelativeLayout mainLayout;
  
	private TextView countdownView;
	
	private long countdownStartTime;
    
	private View splashView;
	
	private TextView debugView;
	
	private GameLevel currentLevel;
	
	private int latestScore;

	private int latestRemainingTimeSeconds=250;
	
	private boolean paused;
		
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
   		    			countdownStartTime = SystemClock.uptimeMillis();
   		    			sendUpdateCountdownMessage();
    					break;
    				case FPS_MESSAGE:
    					debugView.setText(Integer.toString(msg.arg1));
    					break;
    				case COUNTDOWN_MESSAGE:
    					if ( !paused ) {
    						latestRemainingTimeSeconds = TOTAL_TIME_ALLOWED - (int) ((SystemClock.uptimeMillis() - countdownStartTime)
    								/ ONE_SECOND_IN_MILLISECONDS);
    					}

    					// Finish game if out of time.
    					if (latestRemainingTimeSeconds < 0) {
    						changeToScoreActivity(false);
    						break;
    					}
    					// Change time background color if running out of time.
    					if (latestRemainingTimeSeconds < 100) {
    						int backgroundColor = latestRemainingTimeSeconds %2 == 1 ? Color.YELLOW : Color.RED;
    						countdownView.setBackgroundColor(backgroundColor);
    					}
    					// Update time text view and send a delayed message to update again later.
    					countdownView.setText(Integer.toString(latestRemainingTimeSeconds));
    					sendUpdateCountdownMessage();    						
    					break;
    				case START_END_GAME_MESSAGE:
    					gLSurfaceView.togglePaused();
    					//TODO: add red tint or something
    					final Message endGameMessage = updateUiHandler.obtainMessage(END_GAME_MESSAGE);
    					updateUiHandler.sendMessageDelayed(endGameMessage, 2*ONE_SECOND_IN_MILLISECONDS);
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

		// load the OpenGL objects on another thread, not the UI thread, and not
		// before displaying the screen
		new Thread(new Runnable() {
			@Override
			public void run() {
				// run the expensive part on a thread other than the UI thread
				gLSurfaceView.loadLevel(updateUiHandler, currentLevel);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// need to be back in the UI thread to actually add to the screen
						LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
						RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout01);
						relativeLayout.addView(gLSurfaceView, 0, params);

						// hide the splash screen
						relativeLayout.removeView(splashView);

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

	@Override
	public boolean onKeyDown(final int aKeyCode, final KeyEvent aEvent) {
		switch (aKeyCode) {		
			case KeyEvent.KEYCODE_MENU:
				paused = !paused;
				gLSurfaceView.togglePaused();
				return true;
		}
		return super.onKeyDown(aKeyCode, aEvent);
	}
}