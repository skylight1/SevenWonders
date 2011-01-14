package skylight1.sevenwonders.view;

import skylight1.sevenwonders.view.SevenWondersGLRenderer.ScoreObserver;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	
	protected static final String TAG = SevenWondersGLSurfaceView.class.getName();
	
	private final SevenWondersGLRenderer renderer;
	
	private final RendererListener rendererListener;
	
	private final ScoreObserver scoreObserver;

	private final TiltControl tiltControl;
	
	public SevenWondersGLSurfaceView(final Context aContext, 
			final RendererListener aListener, final ScoreObserver aScoreObserver,
			final Handler aEndGameHandler) {
		super(aContext);
		rendererListener = aListener;
		scoreObserver = aScoreObserver;

		setDebugFlags(DEBUG_CHECK_GL_ERROR);
	
		renderer = new SevenWondersGLRenderer(aContext, scoreObserver, aEndGameHandler);
		renderer.setRendererListener(rendererListener);
		setRenderer(renderer);
		tiltControl = new TiltControl(aContext, renderer);
	
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
		if (aKeyCode == KeyEvent.KEYCODE_DPAD_CENTER || aKeyCode == KeyEvent.KEYCODE_SPACE) {
			renderer.setPlayerVelocity(0);
			return true;
			// left/q -> left
		} else if (aKeyCode == KeyEvent.KEYCODE_DPAD_LEFT || aKeyCode == KeyEvent.KEYCODE_Q) {
			renderer.turn(-5f);
			return true;
			// right/w -> right
		} else if (aKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT || aKeyCode == KeyEvent.KEYCODE_W) {
			renderer.turn(+5f);
			return true;
			// up -> pause
		} else if (aKeyCode == KeyEvent.KEYCODE_DPAD_UP) {
			renderer.changeVelocity(SevenWondersGLRenderer.MAXIMUM_VELOCITY / 10f);
			return true;
		} else if (aKeyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			renderer.changeVelocity(-SevenWondersGLRenderer.MAXIMUM_VELOCITY / 10f);
			return true;
		}
		return false;
	}
	
}
