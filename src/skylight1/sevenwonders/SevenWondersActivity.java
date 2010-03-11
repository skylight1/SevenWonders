package skylight1.sevenwonders;

import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import android.app.Activity;
import android.os.Bundle;


public class SevenWondersActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new SevenWondersGLSurfaceView(this));
	}
}