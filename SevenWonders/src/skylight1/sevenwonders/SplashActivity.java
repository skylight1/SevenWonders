package skylight1.sevenwonders;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import skylight1.sevenwonders.services.SoundTracks;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

//import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class SplashActivity extends Activity {
	
	private static final DateFormat NUMERIC_DATE = new SimpleDateFormat("MM/dd/yy");
	
	private static final long SPLASH_DURATION_IN_MILLISECONDS = 3000;
	
	private static final Date EXPIRATION = null;//parseNumericDate("6/5/11");
	
	private static final int EXPIRED_DIALOG_ID = 1;
	
//	private GoogleAnalyticsTracker tracker;

	private Handler handler = new Handler();

	private static Date parseNumericDate(final String aDateString) {
		try { 
			Date parsed = NUMERIC_DATE.parse(aDateString); 
			return parsed;
		} catch (ParseException e) {
			return null;
		} 
	}
	
	private boolean isExpired() {
		if (null != EXPIRATION) {
			Date today = new Date();
			if (today.after(EXPIRATION)) {
				return true;
			}
		}		
		return false;
	}
	
	@Override
	public void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		setContentView(R.layout.splashscreen);

		if (isExpired()) {
			showDialog(EXPIRED_DIALOG_ID);
		} else {
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
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	@Override
	protected Dialog onCreateDialog(final int aId) {
		if ( EXPIRED_DIALOG_ID == aId ) {
			return new AlertDialog.Builder(this)
				.setMessage(R.string.splash_activity_expired_message)
				.setCancelable(true)
				.setPositiveButton(R.string.splash_activity_visit_website_button, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface aDialog, final int aWhich) {
							final Uri websiteUri = Uri.parse(getString(R.string.website_uri));
							final Intent visitWebsiteIntent = new Intent(Intent.ACTION_VIEW, websiteUri);
							startActivity(visitWebsiteIntent);
							finish();
						}	
				})
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(final DialogInterface aDialog) {
						finish();
					}
				})
				.create();
		}		
		return super.onCreateDialog(aId);
	}

	private void dismissSplashActivity() {
		if (!isFinishing()) {
//TODO: fix - temp for AW			
//			startActivity(new Intent(this, MenuActivity.class));
			startActivity(new Intent(this, LevelChooserActivity.class));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SoundTracks.setVolume(this);
	}
}