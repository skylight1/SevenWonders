package skylight1.sevenwonders.view;

import java.util.Arrays;

import skylight1.opengl.OpenGLGeometry;
import skylight1.sevenwonders.PlayActivity;
import android.os.Handler;
import android.util.Log;

public class HazardCollisionHandler implements GeometryAwareCollisionObserver, Cloneable {

	private final Handler updateUiHandler;

	public HazardCollisionHandler(final Handler anUpdateUiHandler) {
		updateUiHandler = anUpdateUiHandler;
	}

	@Override
	public HazardCollisionHandler clone() throws CloneNotSupportedException {
		return (HazardCollisionHandler) super.clone();
	}
	
	@Override
	public void collisionOccurred(final float[] aBoundingSphere) {
		Log.i(SevenWondersGLRenderer.class.getName(), String.format("collided with hazard " + Arrays.toString(aBoundingSphere)));
		updateUiHandler.sendEmptyMessage(PlayActivity.START_END_GAME_MESSAGE);
	}

	@Override
	public void addGeometry(OpenGLGeometry aAnOpenGLGeometry) {
		// do nothing here
	}
}