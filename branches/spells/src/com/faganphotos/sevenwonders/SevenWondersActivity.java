package com.faganphotos.sevenwonders;

import android.app.Activity;
import android.os.Bundle;

import com.faganphotos.sevenwonders.view.SevenWondersGLSurfaceView;

public class SevenWondersActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new SevenWondersGLSurfaceView(this));
	}
}