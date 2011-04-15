/**
 * 
 */
package skylight1.sevenwonders.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import skylight1.opengl.CollisionDetector;
import skylight1.opengl.FastGeometryBuilder;
import skylight1.opengl.FastGeometryBuilderFactory;
import skylight1.opengl.OpenGLGeometry;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.services.SoundTracks;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class SpellCollisionHandler implements GeometryAwareCollisionObserver, Cloneable {
	private static FastGeometryBuilder<?, ?> somewhereFarFarAway;
	
	private CollisionDetector collisionDetector;

	private Handler uiHandler;

	private SevenWondersGLRenderer renderer;

	private List<OpenGLGeometry> spellGeometries = new ArrayList<OpenGLGeometry>(SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES);

	public SpellCollisionHandler(CollisionDetector aCollisionDetector, Handler aUiHandler,
			SevenWondersGLRenderer aRenderer) {
		collisionDetector = aCollisionDetector;
		uiHandler = aUiHandler;
		renderer = aRenderer;
	}

	@Override
	public SpellCollisionHandler clone() throws CloneNotSupportedException {
		// create a clone, but make it have its own array of spell geometries
		final SpellCollisionHandler clone = (SpellCollisionHandler) super.clone();
		clone.spellGeometries = new ArrayList<OpenGLGeometry>(SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES);
		return clone;
	}
	
	@Override
	public void addGeometry(OpenGLGeometry anOpenGLGeometry) {
		spellGeometries.add(anOpenGLGeometry);
		
		if (somewhereFarFarAway == null) {
			// create a fast geometry that is out of sight
			somewhereFarFarAway = FastGeometryBuilderFactory.createTexturableNormalizable(anOpenGLGeometry);
			// TODO there has to be a better way to make a correctly sized geometry, than to know it has 60 quads = 120
			// triangles
			for (int silly = 0; silly < 60 * 2; silly++) {
				somewhereFarFarAway.add3DTriangle(0, 0, -100, 0, 0, -100, 0, 0, -100);
			}
		}
	}

	@Override
	public void collisionOccurred(final float[] aBoundingSphere) {
		SoundTracks.getInstance().play(SoundTracks.SPELL);
		Log.i(SevenWondersGLRenderer.class.getName(), String.format("collided with " + Arrays.toString(aBoundingSphere)));

		collisionDetector.removeBoundingSphere(aBoundingSphere);

		// iterate through all animation geometries, moving the objects off screen
		for (int spellAnimationIndex = 0; spellAnimationIndex < SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES; spellAnimationIndex++) {
			spellGeometries.get(spellAnimationIndex).updateModel(somewhereFarFarAway);
		}

		// add one to the score for colliding with a spell
		final int newScore = renderer.incrementScore(1);

		// notify the observer
		Message message = uiHandler.obtainMessage(PlayActivity.NEW_SCORE_MESSAGE, newScore, 0);
		uiHandler.sendMessage(message);
	}
}