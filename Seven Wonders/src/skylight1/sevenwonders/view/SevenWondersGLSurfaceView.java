package skylight1.sevenwonders.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	
	private SevenWondersGLRenderer renderer;

	public SevenWondersGLSurfaceView(Context context) {
		super(context);

		setDebugFlags(DEBUG_CHECK_GL_ERROR);

		renderer = new SevenWondersGLRenderer(context);
		setRenderer(renderer);

		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

		setKeepScreenOn(true);
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		//final int keyCode = event.getKeyCode();
		Log.i(SevenWondersGLSurfaceView.class.getName(), "Key Down: " + keyCode);
		
		switch( keyCode ) {
			case KeyEvent.KEYCODE_BACK:
				return false;
		}
		
		queueEvent(new Runnable() {
			@Override
			public void run() {
				renderer.handleInput(keyCode);				
			}
		});
		
		return true;
	}
}
