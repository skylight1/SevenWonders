package skylight1.sevenwonders.view;

import static skylight1.sevenwonders.Configuration.*;
import static android.view.KeyEvent.*;
import static javax.microedition.khronos.opengles.GL10.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import skylight1.util.FPSLogger;
import android.content.Context;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class SevenWondersGLRenderer implements Renderer {
	
	private static final int FRAMES_BETWEEN_LOGGING_FPS = 60;
	
	private final Context context;

	private final World world;
	
	private FPSLogger fPSLogger = new FPSLogger(SevenWondersGLRenderer.class.getName(), FRAMES_BETWEEN_LOGGING_FPS);

	private long timeAtLastOnRenderCall;
	
	private boolean paused;
	
	public SevenWondersGLRenderer(Context aContext) {
		context = aContext;
		world = new World(context);
	}
	
	public void onSurfaceCreated(final GL10 aGl, final EGLConfig aConfig) {
	}

	public void onSurfaceChanged(final GL10 aGl, final int aW, final int aH) {		
		if ( LOG ) Log.i(TAG, "onSurfaceChanged(), thread name = " + Thread.currentThread().getName());
		
		aGl.glColor4f(1, 1, 1, 1);
		aGl.glClearColor(0.5f, 0.5f, 1, 1.0f);

		//Don't draw inside facing surfaces on things like the pyramid and sphinx.
		//This fixes a z-fighting issue: At long distances OpenGL doesn't have enough precision in the depth buffer
		//to tell if the front is closer or if the inside of a nearby back surface is closer and gets it wrong some times.
		aGl.glEnable(GL_CULL_FACE);
		
		aGl.glEnable(GL_DEPTH_TEST);

		aGl.glDisable(GL_BLEND);
		aGl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		
		aGl.glShadeModel(GL_SMOOTH);

		aGl.glEnable(GL_LIGHTING);
		aGl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[] { 0.75f, 0.75f, 0.75f, 1f }, 0);

		aGl.glEnable(GL_LIGHT0);
		aGl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] { -1f, 0f, 1f, 0.0f }, 0);
		aGl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] { 0.5f, 0.5f, 0.5f, 1f }, 0);

		aGl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		aGl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		aGl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, new float[] { 0.1f, 0.1f, 0.1f, 1.0f }, 0);
		aGl.glMaterialfv(GL_FRONT_AND_BACK, GL_SHININESS, new float[] { 50.0f }, 0);
		
		aGl.glMatrixMode(GL_PROJECTION);
		aGl.glViewport(0, 0, aW, aH);
		GLU.gluPerspective(aGl, 45, (float) aW / (float) aH, 0.1f, 1000f);

		aGl.glMatrixMode(GL_MODELVIEW);

		world.enable(aGl, context);
		
		timeAtLastOnRenderCall = SystemClock.uptimeMillis();
	}

	public void onDrawFrame(final GL10 aGl) {
		aGl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        final int timeDeltaMS = calculateTimeSinceLastRenderMillis();
        if ( !paused ) {
        	world.update(timeDeltaMS);
        }
        
		world.draw(aGl);

		fPSLogger.frameRendered();
	}

	private int calculateTimeSinceLastRenderMillis() {
		final long now = SystemClock.uptimeMillis();		
        final long timeDeltaMS = now - timeAtLastOnRenderCall;
        timeAtLastOnRenderCall = now;
        
        //Delta is a small difference, not a large time stamp, so no need to keep as a long.
		return (int) timeDeltaMS;
	}
	
	public boolean handleInput(final int keyCode) {
		switch ( keyCode ) {
			case KEYCODE_MENU:
				paused = !paused;
				return true;
		}

		return world.handleInput(keyCode);
	}

}
