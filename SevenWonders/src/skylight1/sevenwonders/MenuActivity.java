package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MenuActivity extends Activity {

	public static final String KEY_IS_SOUND_ENABLED = "KEY_IS_SOUND_ENABLED";

	public static final String PREFS_NAME = "SevenWondersPrefs";
	
	
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);	 
		
		final View view = findViewById(R.id.EnterEgypt);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {			
				startActivity(new Intent(MenuActivity.this, RealMenuActivity.class));
				finish();
			}
		});
	}
}