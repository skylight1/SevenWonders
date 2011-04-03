package skylight1.sevenwonders;

import skylight1.sevenwonders.services.SoundTracks;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public class SplashActivity extends Activity {
	private static final long SPLASH_DURATION_IN_MILLISECONDS = 3000;

	private Handler handler = new Handler();

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		SWExceptionHandler.start();
		final View view = findViewById(R.id.EnterEgypt);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				dismissSplashActivity();
			}
		});

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dismissSplashActivity();
			}
		}, SPLASH_DURATION_IN_MILLISECONDS);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	private void dismissSplashActivity() {
		if (!isFinishing()) {
			startActivity(new Intent(this, MenuActivity.class));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SoundTracks.setVolume(this);
	}
}