package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MenuActivity extends Activity {

	private static final String TAG = MenuActivity.class.getName();

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "started");
		setContentView(R.layout.menu);
		final View button = findViewById(R.id.EnterEgypt);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				final Intent gameActivity = new Intent(MenuActivity.this, SevenWondersActivity.class);
				startActivity(gameActivity);
			}
		});
	}
}