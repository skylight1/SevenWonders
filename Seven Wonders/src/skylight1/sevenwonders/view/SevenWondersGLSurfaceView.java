package skylight1.sevenwonders.view;

import skylight1.sevenwonders.view.SevenWondersGLRenderer.ScoreObserver;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
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
				long time = android.os.SystemClock.elapsedRealtime();

				if (event.sensor.getType() != SensorManager.SENSOR_ORIENTATION || event.values.length < 3)
					return;
				
				float[] values = event.values;

				final int zero = (int) values[0]; // not needed yet
				final int one = (int) values[1]; 
				final float two =  values[2]; // not needed yet
				
				final int turnRate = 1;
				final int turnAmt = one;
			
				renderer.turn(turnAmt *turnRate);			
				renderer.setVelocity(two * .005f);
				// TODO fix magic numbers
			}
		};

		sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);

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
