package skylight1.sevenwonders.view;

import skylight1.opengl.OpenGLGeometry;
import skylight1.sevenwonders.PlayActivity;
import android.os.Handler;
import android.util.Log;

public class HazzardCollisionHandler implements GeometryAwareCollisionObserver {

	private final Handler updateUiHandler;

	public HazzardCollisionHandler(final Handler anUpdateUiHandler) {
		updateUiHandler = anUpdateUiHandler;
	}

	@Override
	public void collisionOccurred(OpenGLGeometry anOpenGLGeometry) {
		Log.i(SevenWondersGLRenderer.class.getName(), String.format("Player hit a sword!"));
		updateUiHandler.sendEmptyMessage(PlayActivity.END_GAME_MESSAGE);
	}

	@Override
	public void addGeometry(OpenGLGeometry aAnOpenGLGeometry, int aAnAnimationIndex, int aGeometryIndex) {
		// do nothing here
	}
}
