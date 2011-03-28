package skylight1.sevenwonders.view;

import skylight1.sevenwonders.levels.GameLevel;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	
	protected static final String TAG = SevenWondersGLSurfaceView.class.getName();
	
	private SevenWondersGLRenderer renderer;
	
	private TiltControl tiltControl;
	
	public SevenWondersGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SevenWondersGLSurfaceView(Context context) {
		super(context);
	}

	public void initialize(final Handler aUpdateUiHandler,
			final GameLevel aLevel) {
		setDebugFlags(DEBUG_CHECK_GL_ERROR);
	
		renderer = new SevenWondersGLRenderer(getContext(), aUpdateUiHandler, aLevel);
		setRenderer(renderer);
		tiltControl = new TiltControl(getContext(), renderer);
	
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	
		setKeepScreenOn(true);
	}
	
	@Override
	public void onPause() {
		tiltControl.stop();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		tiltControl.start();
	}

	public boolean onTouchEvent(final MotionEvent aEvent) {
		queueEvent(new Runnable() {
			public void run() {
				Log.i(TAG,String.format("touched %s,%s",aEvent.getXPrecision(),aEvent.getXPrecision()));
			}
		});
		return true;
	}

	@Override
	public boolean onKeyDown(final int aKeyCode, final KeyEvent aEvent) {
		switch (aKeyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_SPACE:
				renderer.setPlayerVelocity(0);
				return true;
				
			// left/q -> left
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_Q:
				renderer.turn(-5f);
				return true;
				
			// right/w -> right
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_W:
				renderer.turn(+5f);
				return true;
				
			case KeyEvent.KEYCODE_DPAD_UP:
				renderer.changeVelocity(SevenWondersGLRenderer.MAXIMUM_VELOCITY / 10f);
				return true;
				
			case KeyEvent.KEYCODE_DPAD_DOWN:
				renderer.changeVelocity(-SevenWondersGLRenderer.MAXIMUM_VELOCITY / 10f);
				return true;
		}

		return false;
	}

	public void togglePaused() {
		renderer.togglePaused();
	}
	
}
