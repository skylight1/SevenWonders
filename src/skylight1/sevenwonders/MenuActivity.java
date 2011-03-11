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

    public static final String PREFS_NAME = "SevenWondersPrefs";

	private static final String TAG = MenuActivity.class.getName();
	private boolean SOUNDENABLED; 
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Restore preferences
       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       SOUNDENABLED = settings.getBoolean("SOUNDENABLED", false);

		Log.i(TAG, "started");
		setContentView(R.layout.menu);
		final CheckBox soundCB = (CheckBox)findViewById(R.id.soundCheckBox);
        soundCB.setChecked(SOUNDENABLED);
		soundCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				SOUNDENABLED = soundCB.isChecked();

                //save preference
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("SOUNDENABLED", SOUNDENABLED);
                editor.commit();
			}
		});
		
		final View view = findViewById(R.id.EnterEgypt);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				final Intent gameActivity = new Intent(MenuActivity.this, PlayActivity.class);
				gameActivity.putExtra("ENABLESOUND", SOUNDENABLED);
				startActivity(gameActivity);
			}
		});
	}
}