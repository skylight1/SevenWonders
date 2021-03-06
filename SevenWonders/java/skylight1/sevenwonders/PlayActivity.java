package skylight1.sevenwonders;
import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.GameMessagesDisplay;
import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import skylight1.sevenwonders.view.TextStyles;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayActivity extends Activity {
	private static final int WARNING_TIME_FOR_POWER_DOWN_MILLIS = 5000;
	private static final int MILLISECONDS_TO_SHOW_MESSAGE = 2500;
	public static final int FPS_MESSAGE = 0;
	public static final int COUNTDOWN_MESSAGE = 1;
	public static final int START_END_GAME_MESSAGE = 2;
	public static final int SPELL_COLLECTED_MESSAGE = 3;
	public static final int START_RENDERING_MESSAGE = 4;
	protected static final int END_GAME_MESSAGE = 5;
	public static final int MODIFY_REMAINING_TIME_MESSAGE = 6;
	public static final int SHOW_GAME_EVENT_MESSAGE_MESSAGE = 7;

	private static final int REMAINING_SECONDS_AFTER_WHICH_COUNTDOWN_FLASHES = 30;

	private static final long ONE_SECOND_IN_MILLISECONDS = 1000;

	private static final String TAG = PlayActivity.class.getName();
	
	private static final HandlerThread loaderThread = new HandlerThread("SevenWonders loader");
	static {
		loaderThread.start();
	}

	private SevenWondersGLSurfaceView gLSurfaceView;

	private boolean gLSurfaceViewAdded;
	
	private RelativeLayout mainLayout;
  
	private TextView countdownView;
	
	private long lastGameTimeUptimeMillis;
    
	private View splashView;
	
	private TextView debugView;
	private ImageView invicibilityIconImageView;
	private ImageView passThroughObstaclesIconImageView;
	private ImageView soullessMageImageView;
	
	private GameLevel currentLevel;
	
	/** If the game has been paused by the menu button. */
	private boolean isGameManuallyPaused;
	
	private boolean isActivityPaused;
	
	private boolean isRenderingStarted;
	
	private boolean endedByWin;
		
	private boolean endedByTimeOut;
	
	private boolean endedByDeath;
	
	private GameState gameState = new GameState();
	
	private GameMessagesDisplay eventMessages;
	
	private TextView gameStatusView;
	
	private final Handler loadHandler = new Handler(loaderThread.getLooper());
	
	private final Handler initFinishedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.RelativeLayout01);
			topLayout.addView(gLSurfaceView, 0, params);

			// hide the splash screen
			topLayout.removeView(splashView);
			gameStatusView.setVisibility(View.GONE);

			// start the surface
			gLSurfaceView.initialize();
			gLSurfaceView.onResume();
			
			// safe to "resume" the surface view now
			gLSurfaceViewAdded = true;
		}
	};
	
    //Handler to draw debug info (fps) and countdown and end the game
    private Handler updateUiHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		// Ignore messages while finishing. This prevents starting multiple
    		// score activities, for example.
    		if (isFinishing()) {
    			return;
    		}
    		
    		if(mainLayout!=null) {
    			final long remainingGameTimeMillis = gameState.getRemainingGameTimeMillis();
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
    						gameState.reduceRemainingTimeMillis(gameTimeElapsedMillis);
    						long remainingGameTimeSeconds = remainingGameTimeMillis / ONE_SECOND_IN_MILLISECONDS;
    						lastGameTimeUptimeMillis = SystemClock.uptimeMillis();
    						
	    					// Finish game if out of time.
	    					if (remainingGameTimeMillis < 0) {
	    						endedByTimeOut = true;
	    						changeToScoreActivity(false);
	    						break;
	    					}
	    					// Change time background color if running out of time.
	    					if (remainingGameTimeSeconds < REMAINING_SECONDS_AFTER_WHICH_COUNTDOWN_FLASHES) {
	    						int backgroundColor = remainingGameTimeSeconds %2 == 1 ? Color.WHITE : Color.RED;
	    						countdownView.setTextColor(backgroundColor);
	    					}
	    					// Update time text view.
	    					long minutes = remainingGameTimeSeconds / 60;
	    					long seconds = remainingGameTimeSeconds % 60;
	    					countdownView.setText(String.format("%d:%02d", minutes, seconds));

	    					// update the special ability icons
	    					final int remainingInvincibilityTimeMillis = gameState.getRemainingInvincibilityTimeMillis();
							invicibilityIconImageView.setVisibility(gameState.isPlayerInvincible() && (remainingInvincibilityTimeMillis > WARNING_TIME_FOR_POWER_DOWN_MILLIS || remainingGameTimeMillis / 500 % 2 == 0) ? View.VISIBLE : View.INVISIBLE);
							final int remainingPassThroughtObstaclesTimeMillis = gameState.getRemainingPassThroughSolidsTimeMillis();
	    					passThroughObstaclesIconImageView.setVisibility(gameState.isPlayerAbleToFlyThroughObstacles() && (remainingPassThroughtObstaclesTimeMillis > WARNING_TIME_FOR_POWER_DOWN_MILLIS || remainingGameTimeMillis / 500 % 2 == 0) ? View.VISIBLE : View.INVISIBLE);
    					}
    					//send a delayed message to update again later.
    					sendUpdateCountdownMessage();    						
    					break;
    				case START_END_GAME_MESSAGE:
    					if(! endedByWin && ! endedByTimeOut) {
        					gLSurfaceView.togglePaused();
    						endedByDeath = true;
    						SoundTracks.getInstance().play(SoundTracks.DEATH);
    						
							final Animation animation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.soulless_mage_slides_in);
    						soullessMageImageView.startAnimation(animation);
    						soullessMageImageView.setVisibility(View.VISIBLE);
    						
        					sendEndGameMessage(false); // causes a 2 second delay, probably to let the death sound finish
    					}
    					break;
    				case END_GAME_MESSAGE:
    					Boolean wonLevel = (Boolean) msg.obj;
    					if ( isGameTimeMoving() ) {
    						changeToScoreActivity(wonLevel);
    					} else {
    						sendEndGameMessage(wonLevel);
    					}
    					break;
    				case SPELL_COLLECTED_MESSAGE:
    					gameState.numberOfSpellsCollected++;
						if (gameState.numberOfSpellsCollected >= currentLevel.getNumberOfSpells()) {
							endedByWin = true;
							
							// Show villain going poof if it is the last level.
							final boolean nextLevelExists = nextLevelExists();
							if ( !nextLevelExists ) {
								final int animationId = R.anim.soulless_mage_goes_poof;
								
								final Animation animation = AnimationUtils.loadAnimation(PlayActivity.this, animationId);
	    						soullessMageImageView.startAnimation(animation);
	    						animation.setAnimationListener(new AnimationListener() {
									@Override
									public void onAnimationStart(final Animation aAnimation) {
										// Nothing special done on start.
									}
									
									@Override
									public void onAnimationRepeat(final Animation aAnimation) {
										// Do nothing. We don't repeat.
									}
									
									@Override
									public void onAnimationEnd(final Animation aAnimation) {
										// After mage goes poof, don't show him.
			    						soullessMageImageView.setVisibility(View.GONE);
									}
								});
	    						soullessMageImageView.setVisibility(View.VISIBLE);
							}
							
        					sendEndGameMessage(true); // causes a 2 second delay, to show the villain going poof
						}
						
						TextView scoreTextView = (TextView) findViewById(R.id.score);
						scoreTextView.setText("" + gameState.numberOfSpellsCollected);
						
						break;
    				case MODIFY_REMAINING_TIME_MESSAGE:
    					final int timeDeltaInSeconds = msg.arg1;
    					gameState.setRemainingGameTimeMillis(remainingGameTimeMillis + (timeDeltaInSeconds * ONE_SECOND_IN_MILLISECONDS));
    					
    					// TODO would be nice to update the UI at this point, but it will be updated in at most one second anyway! 
						
						break;
						
    				case SHOW_GAME_EVENT_MESSAGE_MESSAGE:
    					eventMessages.showMessage(msg);
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
		updateUiHandler.sendMessageDelayed(countdownMessage, ONE_SECOND_IN_MILLISECONDS / 2);
	}

	protected void sendEndGameMessage(boolean aWonLevel) {
		final Message endGameMessage = updateUiHandler.obtainMessage(END_GAME_MESSAGE, aWonLevel);
		int secondsBeforeEndingGame = nextLevelExists() ? 2 : 4;
		updateUiHandler.sendMessageDelayed(endGameMessage, 
			secondsBeforeEndingGame * ONE_SECOND_IN_MILLISECONDS);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG,"onCreate()");

		super.onCreate(savedInstanceState);

		Settings settings =  new Settings(this);
		settings.setGameWasStartedAtLeastOnceFlag();

		final int levelOrdinal = getIntent().getIntExtra(ScoreActivity.KEY_LEVEL_ORDINAL, 0);
		currentLevel = GameLevel.values()[levelOrdinal];
		
		gameState.setRemainingGameTimeMillis(
			currentLevel.getDefaultTotalTimeAllowedInSeconds() * ONE_SECOND_IN_MILLISECONDS);
		
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
				
		final TextView scoreTextView = (TextView) findViewById(R.id.score);
		scoreTextView.setText("0");
		textStyles.applyHeaderTextStyle(scoreTextView);

		final TextView scoreDivider = (TextView) findViewById(R.id.scoreDivider);
		textStyles.applyHeaderTextStyle(scoreDivider);
		
		final TextView targetScoreTextView = (TextView) findViewById(R.id.targetScore);
		targetScoreTextView.setText("" + currentLevel.getNumberOfSpells());
		textStyles.applyHeaderTextStyle(targetScoreTextView);

		invicibilityIconImageView = (ImageView) findViewById(R.id.invincibilityIcon);
		passThroughObstaclesIconImageView = (ImageView) findViewById(R.id.passThroughObstaclesIcon);
		soullessMageImageView = (ImageView) findViewById(R.id.soullessMage);
		
		gLSurfaceView = new SevenWondersGLSurfaceView(this, gameState);

		gameStatusView = (TextView) findViewById(R.id.gameStatusView);
		textStyles.applyHeaderTextStyle(gameStatusView);
		gameStatusView.setText(currentLevel.getLoadingMessage());
		gameStatusView.setVisibility(View.VISIBLE);
		
		eventMessages = new GameMessagesDisplay(updateUiHandler, this);
		
		final long timeMessageWasShown = System.currentTimeMillis();
		
		// load the OpenGL objects on another thread, not the UI thread, and not
		// before displaying the screen
		loadHandler.post((new Runnable() {
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
				initFinishedHandler.sendEmptyMessage(0);
			}
		}));
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
		} else {
			initFinishedHandler.removeMessages(0);
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
		intent.putExtra(ScoreActivity.KEY_COLLECTED_SPELL_COUNT, gameState.numberOfSpellsCollected); 
		intent.putExtra(ScoreActivity.KEY_COLLECTED_COIN_COUNT, gameState.getNumberofCoinsCollected());
		intent.putExtra(ScoreActivity.KEY_REMAINING_TIME_SECONDS, (int) (gameState.getRemainingGameTimeMillis() / ONE_SECOND_IN_MILLISECONDS));
		intent.putExtra(ScoreActivity.KEY_LEVEL_ORDINAL, currentLevel.ordinal()); 
		intent.putExtra(ScoreActivity.KEY_WON_LEVEL, wonLevel && ! endedByDeath); // if they heard the Wilhem, we can't let them win 
		startActivity(intent);
		finish();
	} 

	@Override
	public boolean onKeyDown(final int aKeyCode, final KeyEvent aEvent) {
		switch (aKeyCode) {		
			case KeyEvent.KEYCODE_MENU:
				lastGameTimeUptimeMillis = SystemClock.uptimeMillis();
				isGameManuallyPaused = !isGameManuallyPaused;
				if ( isGameManuallyPaused ) {
					gameStatusView.setText(R.string.paused);
					gameStatusView.setVisibility(View.VISIBLE);
				} else {
					gameStatusView.setText("");
					gameStatusView.setVisibility(View.GONE);
				}
				gLSurfaceView.togglePaused();
				return true;
		}
		return super.onKeyDown(aKeyCode, aEvent);
	}

	private boolean nextLevelExists() {
		final boolean nextLevelExists = 
			currentLevel.ordinal() < GameLevel.values().length - 1;
		return nextLevelExists;
	}
}