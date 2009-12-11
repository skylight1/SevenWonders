package com.faganphotos.sevenwonders.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SevenWondersGLSurfaceView extends GLSurfaceView {
	SevenWondersGLRenderer renderer;

	public SevenWondersGLSurfaceView(Context context) {
		super(context);

		setDebugFlags(DEBUG_CHECK_GL_ERROR);

		renderer = new SevenWondersGLRenderer(context);
		setRenderer(renderer);
		
		setKeepScreenOn(true);
	}

	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			public void run() {
			}
		});
		return true;
	}
}
