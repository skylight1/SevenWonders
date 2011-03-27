package skylight1.sevenwonders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsActivity extends BaseActivityWithMenu {



	private static final String TAG = SettingsActivity.class.getName();
 
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Restore preferences
		
		final Settings settings = new Settings(this);

		Log.i(TAG, "started");
		setContentView(R.layout.settings);
				
		final CheckBox soundCB = (CheckBox)findViewById(R.id.soundCheckBox);		
		
        soundCB.setChecked(settings.isSoundEnabled());
		soundCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {			
				settings.setSoundEnabled(soundCB.isChecked());	
			}
		});
		
		final TextView backButton = (TextView)findViewById(R.id.settings_back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {			
				finish();
			}
		});
		
		
		TextStyles wonderFonts = new TextStyles(this);
		wonderFonts.applyBodyTextStyle(soundCB);
		wonderFonts.applyHeaderTextStyle(backButton);
	}
}