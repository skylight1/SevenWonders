package com.faganphotos.sevenwonders;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.faganphotos.sevenwonders.view.SevenWondersGLSurfaceView;

public class SevenWondersActivity extends Activity implements SensorEventListener {

	
	private SensorManager sensorManager;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new SevenWondersGLSurfaceView(this));
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER),
				   SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// don't need to do anything
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != SensorManager.SENSOR_ACCELEROMETER || event.values.length < 3)
			return;
		float[] values = event.values;

		// Calculate the angle of tilt in X
		final float x = values[0];
		final float y = values[1];
		final float z = values[2];
		double m = (float) Math.sqrt(x*x + y*y + z*z);
		double tilt = Math.asin(x / m);
		
		Log.i("tilt", "" + tilt);
		

		
	}
}