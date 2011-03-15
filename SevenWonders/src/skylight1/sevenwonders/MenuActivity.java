package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class MenuActivity extends Activity {

    private static final String SOUND_ENABLED = "SOUNDENABLED";

	public static final String PREFS_NAME = "SevenWondersPrefs";

	private static final String TAG = MenuActivity.class.getName();
	private boolean soundEndabled; 
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		Log.i(TAG, "started");

		super.onCreate(savedInstanceState);

        // Restore preferences
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        soundEndabled = settings.getBoolean(SOUND_ENABLED, false);

		setContentView(R.layout.menu);
		final CheckBox soundCB = (CheckBox)findViewById(R.id.soundCheckBox);
        soundCB.setChecked(soundEndabled);
		soundCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				soundEndabled = soundCB.isChecked();

                //save preference
                final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SOUND_ENABLED, soundEndabled);
                editor.commit();
			}
		});
		
		final View view = findViewById(R.id.EnterEgypt);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				final Intent gameActivity = new Intent(MenuActivity.this, PlayActivity.class);
				gameActivity.putExtra("ENABLESOUND", soundEndabled);
				startActivity(gameActivity);
			}
		});
	}
}