package skylight1.sevenwonders;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import skylight1.sevenwonders.view.RendererListener;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SevenWondersActivity extends Activity {

	private static String TAG = SevenWondersActivity.class.getName();
	private SevenWondersGLSurfaceView gLSurfaceView;
	private RelativeLayout mainLayout;

	private final static int GAMELAYER = 0;
	private final static int MENULAYER = 1;

	//handler to remove splash screen
    private Handler handler = new Handler() {
    	public void handleMessage(Message msg) {
    		if(mainLayout!=null) {
    			splashView.setVisibility(View.INVISIBLE);
    		}
    	}
    };
	private ImageView splashView;

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
			    	Log.i(TAG,"startedRendering()");
					SoundTracks.getInstance().fadeoutSplashSoundTrack(SoundTracks.SOUNDTRACK);
					handler.sendMessage(handler.obtainMessage());
			    }});
		}

//		setContentView(gLSurfaceView);

//		setContentView(R.layout.main);
//		gLSurfaceView = (GLSurfaceView) findViewById(R.id.swglsurface);

		mainLayout = new RelativeLayout(this);
		mainLayout.setLayoutParams(new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.FILL_PARENT));

		splashView = new ImageView(this);

		splashView.setBackgroundResource(R.drawable.bg);

		mainLayout.addView(gLSurfaceView, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.FILL_PARENT));

		mainLayout.addView(splashView, new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT,
			RelativeLayout.LayoutParams.FILL_PARENT));

		setContentView(mainLayout);

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
