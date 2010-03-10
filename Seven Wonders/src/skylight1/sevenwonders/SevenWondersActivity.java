package skylight1.sevenwonders;

import skylight1.sevenwonders.view.SevenWondersGLSurfaceView;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;


public class SevenWondersActivity extends Activity {

	private GLSurfaceView surfaceView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		surfaceView = new SevenWondersGLSurfaceView(this);
		setContentView(surfaceView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		surfaceView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		surfaceView.onResume();
	}
	
	
}