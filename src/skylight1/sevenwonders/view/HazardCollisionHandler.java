package skylight1.sevenwonders.view;

import skylight1.opengl.OpenGLGeometry;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import android.os.Handler;

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
	public boolean collisionOccurred(final float[] aBoundingSphere) {
		SoundTracks.getInstance().play(SoundTracks.DEATH);
		updateUiHandler.sendEmptyMessage(PlayActivity.START_END_GAME_MESSAGE);
		
		// do not suppress future collisions
		return false;
	}

	@Override
	public void addGeometry(OpenGLGeometry aAnOpenGLGeometry) {
		// do nothing here
	}
}