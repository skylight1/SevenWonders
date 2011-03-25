package skylight1.sevenwonders.view;

import skylight1.opengl.OpenGLGeometry;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import android.os.Handler;
import android.util.Log;

public class HazardCollisionHandler implements GeometryAwareCollisionObserver {

	private final Handler updateUiHandler;

	public HazardCollisionHandler(final Handler anUpdateUiHandler) {
		updateUiHandler = anUpdateUiHandler;
	}

	@Override
	public void collisionOccurred(OpenGLGeometry anOpenGLGeometry) {
		Log.i(SevenWondersGLRenderer.class.getName(), String.format("Player hit a sword!"));
		SoundTracks.getInstance().play(SoundTracks.DEATH);
		updateUiHandler.sendEmptyMessage(PlayActivity.END_GAME_MESSAGE);
	}

	@Override
	public void addGeometry(OpenGLGeometry aAnOpenGLGeometry, int aAnAnimationIndex, int aGeometryIndex) {
		// do nothing here
	}
}