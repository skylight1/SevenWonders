package skylight1.sevenwonders.view;

import skylight1.sevenwonders.view.SevenWondersGLRenderer.ScoreObserver;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	
	/** 
	 * The tilt from the horizontal along the y-axis that should produce no 
	 * speed up or slow down.
	 */
	private static final float NO_ACCEL_Y_TILT = 7.5f;
	
	/**
	 * Conversion from roll to velocity. Higher numbers mean faster per amount 
	 * of roll.
	 */
	private static final float ROLL_TO_VELOCITY_FACTOR = 0.5f;
	
	protected static final String TAG = SevenWondersGLSurfaceView.class.getName();
	private SevenWondersGLRenderer renderer;
	private RendererListener rendererListener;
	private ScoreObserver scoreObserver;

	public SevenWondersGLSurfaceView(Context context, RendererListener listener, ScoreObserver aScoreObserver) {
		super(context);
		rendererListener = listener;
		scoreObserver = aScoreObserver;
		init(context);
	}

	public void setRendererListener(RendererListener listener) {
		rendererListener = listener;
	}

	private void init(Context context) {
		SensorManager sensorManager = (SensorManager) ((Activity)context).getSystemService(android.content.Context.SENSOR_SERVICE);
		SensorEventListener sensorEventListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// do nothing
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (event.sensor.getType() != SensorManager.SENSOR_ORIENTATION 
						|| event.values.length < 3) {
					return;
				}

				// event.values[0] is the azimuth, rotation around the z-aixs
				final int pitch = (int) event.values[1]; //rotation around x-axis
				renderer.turn(pitch);	
				
				final float roll =  event.values[2]; // rotation around y-axis	
				final float velocityChange = 
					(roll - NO_ACCEL_Y_TILT) * ROLL_TO_VELOCITY_FACTOR;
				renderer.setVelocity(velocityChange);
			}
		};

		sensorManager.registerListener(sensorEventListener, 
			sensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION),
			SensorManager.SENSOR_DELAY_GAME);

		setDebugFlags(DEBUG_CHECK_GL_ERROR);

		renderer = new SevenWondersGLRenderer(context, scoreObserver);
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
