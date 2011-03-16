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

	public static final String KEY_IS_SOUND_ENABLED = "KEY_IS_SOUND_ENABLED";

	public static final String PREFS_NAME = "SevenWondersPrefs";

	private static final String TAG = MenuActivity.class.getName();
	
	private boolean isSoundEnabled; 
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Restore preferences
       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       isSoundEnabled = settings.getBoolean(KEY_IS_SOUND_ENABLED, false);

		Log.i(TAG, "started");
		setContentView(R.layout.menu);
		final CheckBox soundCB = (CheckBox)findViewById(R.id.soundCheckBox);
        soundCB.setChecked(isSoundEnabled);
		soundCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				isSoundEnabled = soundCB.isChecked();

                //save preference
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(KEY_IS_SOUND_ENABLED, isSoundEnabled);
                editor.commit();
			}
		});
		
		final View view = findViewById(R.id.EnterEgypt);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {			
				startActivity(new Intent(MenuActivity.this, StoryActivity.class));
			}
		});
	}
}