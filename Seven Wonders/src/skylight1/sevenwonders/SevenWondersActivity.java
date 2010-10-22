package skylight1.sevenwonders;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import skylight1.sevenwonders.view.RendererListener;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SevenWondersActivity extends Activity {

	private static String TAG = SevenWondersActivity.class.getName();
	private SevenWondersGLSurfaceView gLSurfaceView;
	private RelativeLayout mainLayout;

	private final static int GAMELAYER = 0;
	private final static int MENULAYER = 1;

	//Handler to remove splash screen
    private Handler splashHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if(mainLayout!=null) {
		    	Log.i(TAG,"startedRendering()");
				SoundTracks.getInstance().fadeoutSplashSoundTrack(SoundTracks.SOUNDTRACK);
    			splashView.setVisibility(View.INVISIBLE);
    		}
    	}
    };
	//Handler to draw debug info (fps)
    private Handler debugHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if(mainLayout!=null) {
				debugView.setText(Integer.toString(msg.arg1));
    		}
    	}
    };
	private ImageView splashView;
	private TextView debugView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG,"onCreate()");

		SoundTracks.setEnabled(true);

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
					Message msg  = debugHandler.obtainMessage();
					msg.arg1 = fPS;
					debugHandler.sendMessage(msg);
				}
			}, new SevenWondersGLRenderer.ScoreObserver(){
				@Override
				public void observerNewScore(final int aNewScore) {
					// TODO don't create a new object every time... don't want the GC to kick in
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							TextView scoreTextView = (TextView) findViewById(R.id.Score);
							scoreTextView.setText(""+ aNewScore);
						}});
				}});
		}

//		setContentView(gLSurfaceView);
//		setContentView(R.layout.main);
//		gLSurfaceView = (GLSurfaceView) findViewById(R.id.swglsurface);


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

		debugView = new TextView(this);
		debugView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22f);
		debugView.setText("");
		debugView.setBackgroundColor(Color.TRANSPARENT);
		debugView.setTextColor(Color.YELLOW);
		mainLayout.addView(debugView, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.FILL_PARENT));
	}
    @Override
    protected void onPause() {
        super.onPause();
		Log.i(TAG,"onPause()");
		SoundTracks.getInstance().pause();
		gLSurfaceView.onPause();
		/*
		final boolean[] is_pausing = new boolean[1];
        synchronized (gLSurfaceView) {
            gLSurfaceView.onPause();
            gLSurfaceView.queueEvent(new Runnable() {
                public final void run() {
                    synchronized (is_pausing) {
                        is_pausing[0] = true;
                        is_pausing.notify();
                    }
                }
            });
        }
        synchronized (is_pausing) {
            while(!is_pausing[0]) {
                try {is_pausing.wait();} catch (InterruptedException e) {}
            }
        }
        */
	}
    @Override
	public void onResume() {
		super.onResume();
		Log.i(TAG,"onResume()");
		SoundTracks.getInstance().resume();
		gLSurfaceView.onResume();
		/*
        synchronized (gLSurfaceView) {
        	if(SoundTracks.getInstance().resume()) {
        		gLSurfaceView.onResume();
        	}
   		}
   		*/
	}
    @Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG,"onDestroy()");
		if(SoundTracks.getInstance()!=null) {
			SoundTracks.getInstance().stop();
		}
	}

}
