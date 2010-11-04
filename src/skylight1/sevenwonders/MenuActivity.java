package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MenuActivity extends Activity implements OnClickListener{ 

		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			Log.i("seven wonders menu activity", "started");
			setContentView(R.layout.menu);
			View button = findViewById(R.id.EnterEgypt);
			button.setOnClickListener(this);
		}
		@Override
		protected void onResume() {
			super.onResume();
		}

		@Override
		protected void onDestroy() {
			super.onPause();
		}
		@Override
		public void onClick(View arg0) {
			
			Intent gameActivity = new Intent(this, SevenWondersActivity.class);
			this.startActivity(gameActivity);
		}

		@Override
		protected void onPause() {
			super.onPause();
		}
	}

