package skylight1.sevenwonders.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	SevenWondersGLRenderer renderer;

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

	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			public void run() {
			}
		});
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
			renderer.setPlayerVelocity(0);
			return true;
			// left/q -> left
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_Q) {
			renderer.turn(-5f);
			return true;
			// right/w -> right
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_W) {
			renderer.turn(+5f);
			return true;
			// up -> pause
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			renderer.changeVelocity(SevenWondersGLRenderer.INITIAL_VELOCITY / 10f);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			renderer.changeVelocity(-SevenWondersGLRenderer.INITIAL_VELOCITY / 10f);
			return true;
		}
		return false;
	}
}
