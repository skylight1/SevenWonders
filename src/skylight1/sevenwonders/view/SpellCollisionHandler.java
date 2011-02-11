/**
 * 
 */
package skylight1.sevenwonders.view;

import skylight1.opengl.CollisionDetector;
import skylight1.opengl.FastGeometryBuilder;
import skylight1.opengl.FastGeometryBuilderFactory;
import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.CollisionDetector.CollisionObserver;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.services.SoundTracks;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class SpellCollisionHandler implements CollisionObserver {
	private CollisionDetector collisionDetector;

	private GameLevel level;

	private Handler uiHandler;

	private SevenWondersGLRenderer renderer;

	private FastGeometryBuilder<?, ?> somewhereFarFarAway;

	final private OpenGLGeometry[][] spellGeometries = new OpenGLGeometry[SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES][];

	public SpellCollisionHandler(CollisionDetector aCollisionDetector, GameLevel aLevel, Handler aUiHandler,
			SevenWondersGLRenderer aRenderer) {
		collisionDetector = aCollisionDetector;
		level = aLevel;
		uiHandler = aUiHandler;
		renderer = aRenderer;
	}

	public void addGeometry(OpenGLGeometry anOpenGLGeometry, int anAnimationIndex, int aGeometryIndex) {
		if (spellGeometries[anAnimationIndex] == null) {
			spellGeometries[anAnimationIndex] = new OpenGLGeometry[level.getNumberOfSpells()];
		}
		spellGeometries[anAnimationIndex][aGeometryIndex] = anOpenGLGeometry;

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
	public void collisionOccurred(OpenGLGeometry anOpenGLGeometry) {
		Log.i(SevenWondersGLRenderer.class.getName(), String.format("collided with " + anOpenGLGeometry));

		collisionDetector.removeGeometry(anOpenGLGeometry);

		// find the spell index of this geometry
		for (int spellIndex = 0; spellIndex < level.getNumberOfSpells(); spellIndex++) {
			if (spellGeometries[SevenWondersGLRenderer.ANIMATION_INDEX_FOR_COLLISION_DETECTION][spellIndex] == anOpenGLGeometry) {
				// iterate through all animation geometries, moving the objects off screen
				for (int spellAnimationIndex = 0; spellAnimationIndex < SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES; spellAnimationIndex++) {
					spellGeometries[spellAnimationIndex][spellIndex].updateModel(somewhereFarFarAway);
				}
				// once a match is found, there's no need to continue looking
				break;
			}
		}

		// add one to the score for colliding with a spell
		final int newScore = renderer.incrementScore(1);

		// notify the observer
		Message message = uiHandler.obtainMessage(PlayActivity.NEW_SCORE_MESSAGE, newScore, 0);
		uiHandler.sendMessage(message);

		SoundTracks.getInstance().play(SoundTracks.SPELL);
	}
}