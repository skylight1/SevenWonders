package skylight1.sevenwonders.view;

import skylight1.sevenwonders.GameState;
import skylight1.sevenwonders.SevenWondersApplication;
import skylight1.sevenwonders.levels.GameLevel;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	
	protected static final String TAG = SevenWondersGLSurfaceView.class.getName();
	
	private SevenWondersGLRenderer renderer;
	
	private TiltControl tiltControl;
	
	private int controlMode;
	static final int CONTROL_MODE_DEFAULT = 0;
	// 0: default accelerometer / D-Pad
	static final int CONTROL_MODE_XPERIA_PLAY = 1;
	// 1: Sony Ericsson Xperia Play	

	private final GameState gameState;
	
	public SevenWondersGLSurfaceView(final Context context, final GameState aGameState) {
		super(context);
		gameState = aGameState;
	}

	public void loadLevel(final Handler aUpdateUiHandler, final GameLevel aLevel) {
		renderer = new SevenWondersGLRenderer(getContext(), aUpdateUiHandler, aLevel, gameState);
		setRenderer(renderer);
	}

	public void initialize() {
		//setDebugFlags(DEBUG_CHECK_GL_ERROR);
	
		if(Build.MODEL.contains("R800a")) {
			controlMode = 1;
		} else {
			controlMode = 0;
			tiltControl = new TiltControl(getContext(), renderer);
		}
		
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	
		setKeepScreenOn(true);
	}
	
	@Override
	public void onPause() {
		if(controlMode == 0)
			tiltControl.stop();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if(controlMode == 0)
			tiltControl.start();
	}

	@Override
	public boolean onKeyDown(final int aKeyCode, final KeyEvent aEvent) {
		
		if(controlMode == CONTROL_MODE_XPERIA_PLAY) {
			switch(aKeyCode) {
				// left
				case KeyEvent.KEYCODE_DPAD_LEFT:
					renderer.turn(-5f);
					return true;
					
				// right	
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					renderer.turn(5f);
					return true;
					
				// accelerate
				case KeyEvent.KEYCODE_DPAD_CENTER:
					renderer.changeVelocity(SevenWondersGLRenderer.MAXIMUM_VELOCITY / 10f);
					return true;
					
				case KeyEvent.KEYCODE_BACK:
					if(aEvent.isAltPressed()) {
						renderer.changeVelocity(-SevenWondersGLRenderer.MAXIMUM_VELOCITY / 10f);
						return true;
					} else {
						// pass regular back button event to system
						return super.onKeyDown(aKeyCode, aEvent);
					}
					
				case 99:
				case 100:
					// API 9 introduced KEYCODE_BUTTON_X, KEYCODE_BUTTON_Y
					renderer.setPlayerVelocity(0);
					return true;
			}
		} else {
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
		}

		return false;
	}

	public void togglePaused() {
		if(renderer != null) {
			renderer.togglePaused();
		}
	}
	
}
