package skylight1.sevenwonders.view;

import skylight1.sevenwonders.SevenWondersApplication;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Controls the carpet by measuring the tilt of the phone.
 *
 */
public class TiltControl {

	/**
	 * The range of roll values you have to tilt the phone through to 
	 * access the full speed of the carpet. This makes it so that you 
	 * don't have to tilt the phone a full 90 degrees to go from stopped 
	 * to full speed.
	 */
	private static final int ROLL_TO_MOVE_RANGE_DEGREES = 30;

	/**
	 * The roll where the carpet is considered not to be moving. People 
	 * do not usually hold the phone vertical, so this allows stopping 
	 * the carpet without fully tilting the phone to vertical, which 
	 * makes the screen tough to read.
	 */
	private static final int MINIMUM_ROLL_TO_MOVE_DEGREES = 25;

	/**
	 * Value to start inverting the roll from. The phone's roll goes down
	 * as the phone is closer to being flat, but we want this to correspond
	 * to going faster.
	 */
	private static final int ROLL_INVERT_START_DEGREES = 90;

	/** 
	 * Divisor to convert the phone's pitch to the carpet's pitch. 
	 * This is used to make the carpet turn more than the actual phone.
	 */
	private static final int PHONE_PITCH_TO_CARPET_PITCH_DIVISOR = -10;
	
	private final SensorManager sensorManager;

	private Display display;
	
	private final SevenWondersGLRenderer renderer;
	
	private final Sensor orientationSensor;
	
	private final SensorEventListener orientationChangedListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(final Sensor aSensor, final int aAccuracy) {
			// do nothing
			
		}

		@Override
		public void onSensorChanged(final SensorEvent aEvent) {

			//adjust for rotation if landscape is the natural orientation
			//ROTATION_90 from natural orientation would be portrait based devices (only value I see w/phones) 
			if(display.getOrientation() == Surface.ROTATION_0) {
				float temp = aEvent.values[1];
				aEvent.values[1] = aEvent.values[2];
				aEvent.values[2] = temp + 180;
			}
	
			// not using event.values[0], it's the azimuth, rotation around the z-aixs
			// Point the carpet left or right depending on phone being tilted left or right.
			final int phonePitch = (int) aEvent.values[1]; // rotation around x-axis
			final int carpetPitch = phonePitch / PHONE_PITCH_TO_CARPET_PITCH_DIVISOR;
			if(SevenWondersApplication.isDebug) {
				Log.i("TiltControl", "carpetPitch: " + carpetPitch);
			}
			renderer.setTurningVelocity(carpetPitch);	
			
			// Speed up the carpet when tilted forward.
			final float roll =  aEvent.values[2]; // rotation around y-axis	
			final float velocityPercent = 
				( (ROLL_INVERT_START_DEGREES - roll) - MINIMUM_ROLL_TO_MOVE_DEGREES ) 
				/ ROLL_TO_MOVE_RANGE_DEGREES;
			renderer.setVelocityPercent(velocityPercent);
		}
	};
	
	public TiltControl(final Context aContext, final SevenWondersGLRenderer aRenderer) {
		this.renderer = aRenderer;
		WindowManager windowManager = (WindowManager) aContext.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		sensorManager = (SensorManager) aContext.getSystemService(android.content.Context.SENSOR_SERVICE);
		orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}
	
	public void stop() {
		sensorManager.unregisterListener(orientationChangedListener);
	}

	public void start() {
		// XXX This is deprecated in favor of SensorManager#getOrientation, 
		// but that requires listening to two sensors at once which is more complex.
		sensorManager.registerListener(orientationChangedListener, 
			orientationSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
}
