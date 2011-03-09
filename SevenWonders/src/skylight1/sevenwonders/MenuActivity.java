package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class MenuActivity extends Activity {

	private static final String TAG = MenuActivity.class.getName();
	private boolean SOUNDENABLED; 
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "started");
		setContentView(R.layout.menu);
		final CheckBox soundCB = (CheckBox)findViewById(R.id.soundCheckBox);
		soundCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				SOUNDENABLED = soundCB.isChecked();
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