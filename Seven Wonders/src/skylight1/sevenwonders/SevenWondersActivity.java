package skylight1.sevenwonders;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.RendererListener;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SevenWondersActivity extends Activity {

	public static final int FPS_MESSAGE = 0;
	public static final int COUNTDOWN_MESSAGE = 1;
	public static final int END_GAME_MESSAGE = 2;

	protected static final int TOTAL_TIME_ALLOWED = 180;

	private static String TAG = SevenWondersActivity.class.getName();
	private SevenWondersGLSurfaceView gLSurfaceView;
	private RelativeLayout mainLayout;

	private static final long ONE_SECOND_IN_MILLISECONDS = 1000;
	
	//Handler to remove splash screen
    private Handler splashHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if(mainLayout!=null) {
		    	Log.i(TAG,"startedRendering()");
				SoundTracks.getInstance().fadeoutSplashSoundTrack(SoundTracks.SOUNDTRACK);
    			splashView.setVisibility(View.GONE);
    			
    			// start the countdown NOW!
    			countdownStartTime = SystemClock.uptimeMillis();
    			sendUpdateCountdownMessage();
    		}
    	}
    };
    
	private TextView countdownView;

	private long countdownStartTime;
	
	private void sendUpdateCountdownMessage() {
		final Message countdownMessage = updateUiHandler.obtainMessage(COUNTDOWN_MESSAGE);
		updateUiHandler.sendMessageDelayed(countdownMessage, ONE_SECOND_IN_MILLISECONDS);
	}
	
    //Handler to draw debug info (fps) and countdown and end the game
    private Handler updateUiHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if(mainLayout!=null) {
    			switch (msg.what) {
    				case FPS_MESSAGE:
    					debugView.setText(Integer.toString(msg.arg1));
    					break;
    				case COUNTDOWN_MESSAGE:
    					latestRemainingTimeSeconds = TOTAL_TIME_ALLOWED - (int) ((SystemClock.uptimeMillis() - countdownStartTime)
								/ ONE_SECOND_IN_MILLISECONDS);
    					countdownView.setText(Integer.toString(latestRemainingTimeSeconds));
    					sendUpdateCountdownMessage();
    					break;
    				case END_GAME_MESSAGE:
    					changeToScoreActivity();
    			}
    		}
    	}
    };
    
	private ImageView splashView;
	private TextView debugView;
	private Level currentLevel = Level.FIRST;
	private volatile int latestScore;
	private int latestRemainingTimeSeconds;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG,"onCreate()");

		SoundTracks.setEnabled(getIntent().getBooleanExtra("ENABLESOUND", true));
		SoundTracks soundTrack = SoundTracks.getInstance();
		soundTrack.init(getApplicationContext());		

		if(gLSurfaceView==null) {
			gLSurfaceView = new SevenWondersGLSurfaceView(this, new RendererListener() {

				@Override
			    public void startedRendering() {
					splashHandler.sendMessage(splashHandler.obtainMessage());
			    }

				@Override
				public void drawFPS(int fPS) {
					Message msg  = updateUiHandler.obtainMessage();
					msg.arg1 = fPS;
					updateUiHandler.sendMessage(msg);
				}
			}, new SevenWondersGLRenderer.ScoreObserver(){
				@Override
				public void observerNewScore(final int aNewScore) {
					latestScore = aNewScore;
					// TODO don't create a new object every time... don't want the GC to kick in
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (aNewScore >= currentLevel.getNumberOfSpellsRequired()) {
								changeToScoreActivity();
							}
							TextView scoreTextView = (TextView) findViewById(R.id.Score);
							scoreTextView.setText(""+ aNewScore);
						}});
				}}, updateUiHandler);
		}

		setContentView(R.layout.main);
		
		mainLayout = (RelativeLayout) findViewById(R.id.RelativeLayout01);

		mainLayout.addView(gLSurfaceView, 0, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.FILL_PARENT));

		splashView = new ImageView(this);
		splashView.setBackgroundResource(R.drawable.bg);

		mainLayout.addView(splashView, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.FILL_PARENT));
		
		countdownView = (TextView) findViewById(R.id.Countdown);
		debugView = (TextView) findViewById(R.id.FPS);
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
		Intent intent = new Intent().setClass(SevenWondersActivity.this, ScoreActivity.class);
		intent.putExtra(ScoreActivity.KEY_COLLECTED_SPELL_COUNT, latestScore); 
		intent.putExtra(ScoreActivity.KEY_REMAINING_TIME_SECONDS, latestRemainingTimeSeconds); 
		startActivity(intent);
		finish();
	}

}
