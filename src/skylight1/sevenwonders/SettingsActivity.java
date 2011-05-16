package skylight1.sevenwonders;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.TextStyles;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class SettingsActivity extends Activity {

	private static final String TAG = SettingsActivity.class.getName();

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Restore preferences

		final Settings settings = new Settings(this);

		Log.i(TAG, "started");
		setContentView(R.layout.settings);
		final CheckBox soundCB = (CheckBox) findViewById(R.id.soundCheckBox);

		soundCB.setChecked(settings.isSoundEnabled());
		soundCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				settings.setSoundEnabled(soundCB.isChecked());
			}
		});

		if(SevenWondersApplication.isDebug) {
			final CheckBox debugCB = (CheckBox) findViewById(R.id.debug);
			debugCB.setVisibility(View.VISIBLE);
			debugCB.setChecked(settings.isDebugEnabled());
			debugCB.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View aV) {
					settings.setDebugEnabled(debugCB.isChecked());
				}
			});
		} else {
			final CheckBox debugCB = (CheckBox) findViewById(R.id.debug);
			debugCB.setVisibility(View.GONE);
		}

		final Button backButton = (Button) findViewById(R.id.settings_back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				finish();
			}
		});

		final Button aboutButton = (Button) findViewById(R.id.settings_about_button);
		aboutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
			}
		});

		TextStyles wonderFonts = new TextStyles(this);
		wonderFonts.applyBodyTextStyle(soundCB);
		wonderFonts.applyHeaderTextStyle(backButton);
		wonderFonts.applyHeaderTextStyle(aboutButton);
	}

	@Override
	public void onResume() {
		super.onResume();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SoundTracks.setVolume(this);
	}
}