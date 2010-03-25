package skylight1.sevenwonders.view;

import skylight1.sevenwonders.SevenWondersActivity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	protected static final String TAG = SevenWondersGLSurfaceView.class.getName();
	private SevenWondersGLRenderer renderer;
	private RendererListener rendererListener;

	public SevenWondersGLSurfaceView(Context context, SevenWondersActivity activity) {
		super(context);
		rendererListener = activity;
		init(context);
	}
	public SevenWondersGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setRendererListener(SevenWondersActivity activity) {
		rendererListener = activity;
	}

	private void init(Context context) {
		setDebugFlags(DEBUG_CHECK_GL_ERROR);

		renderer = new SevenWondersGLRenderer(context);
		renderer.setRendererListener(rendererListener);
		setRenderer(renderer);

		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

		setKeepScreenOn(true);
	}

	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			public void run() {
				Log.i(TAG,String.format("touched %s,%s",event.getXPrecision(),event.getXPrecision()));
			}
		});
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(SevenWondersGLSurfaceView.class.getName(), "Key Down: " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
			Log.i(SevenWondersGLSurfaceView.class.getName(), "Pausing...");
			renderer.setPlayerVelocity(0);
			return true;
			// left/q -> left
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_Q) {
			Log.i(SevenWondersGLSurfaceView.class.getName(), "Turning left");
			renderer.turn(-5f);
			return true;
			// right/w -> right
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_W) {
			Log.i(SevenWondersGLSurfaceView.class.getName(), "Turning right");
			renderer.turn(+5f);
			return true;
			// up -> pause
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			Log.i(SevenWondersGLSurfaceView.class.getName(), "Speeding up.");
			renderer.changeVelocity(SevenWondersGLRenderer.INITIAL_VELOCITY / 10f);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			Log.i(SevenWondersGLSurfaceView.class.getName(), "Slowing down.");
			renderer.changeVelocity(-SevenWondersGLRenderer.INITIAL_VELOCITY / 10f);
			return true;
		}
		return false;
	}
}
