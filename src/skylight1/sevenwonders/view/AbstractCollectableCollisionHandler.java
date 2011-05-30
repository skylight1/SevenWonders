package skylight1.sevenwonders.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skylight1.opengl.CollisionDetector;
import skylight1.opengl.FastGeometryBuilder;
import skylight1.opengl.FastGeometryBuilderFactory;
import skylight1.opengl.OpenGLGeometry;
import android.os.Handler;
import android.util.Log;

public abstract class AbstractCollectableCollisionHandler implements GeometryAwareCollisionObserver, Cloneable {

	private static final Map<Integer, FastGeometryBuilder<?, ?>> mapOfNumebrOfVerticesToSomewhereFarFarAway = new HashMap<Integer, FastGeometryBuilder<?, ?>>();

	protected final Handler uiHandler;

	protected final SevenWondersGLRenderer renderer;

	private List<OpenGLGeometry> spellGeometries = new ArrayList<OpenGLGeometry>(SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES);

	public AbstractCollectableCollisionHandler(Handler aUiHandler,
			SevenWondersGLRenderer aRenderer) {
		uiHandler = aUiHandler;
		renderer = aRenderer;
	}

	@Override
	public AbstractCollectableCollisionHandler clone() throws CloneNotSupportedException {
		// create a clone, but make it have its own array of spell geometries
		final AbstractCollectableCollisionHandler clone = (SpellCollisionHandler) super.clone();
		clone.spellGeometries = new ArrayList<OpenGLGeometry>(SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES);
		return clone;
	}

	@Override
	public void addGeometry(OpenGLGeometry anOpenGLGeometry) {
		spellGeometries.add(anOpenGLGeometry);

		if (!mapOfNumebrOfVerticesToSomewhereFarFarAway.containsKey(anOpenGLGeometry.getNumberOfVerticies())) {
			// create a fast geometry that is out of sight
			final FastGeometryBuilder<?, ?> somewhereFarFarAway = FastGeometryBuilderFactory.createTexturableNormalizable(anOpenGLGeometry);
			// TODO there has to be a better way to make a correctly sized geometry, than to know it has 3 times as many
			// vertices as triangles
			for (int silly = 0; silly < 3 * anOpenGLGeometry.getNumberOfVerticies(); silly++) {
				somewhereFarFarAway.add3DTriangle(0, 0, -100, 0, 0, -100, 0, 0, -100);
			}
			mapOfNumebrOfVerticesToSomewhereFarFarAway.put(anOpenGLGeometry.getNumberOfVerticies(), somewhereFarFarAway);
		}
	}

	@Override
	public boolean collisionOccurred(final float[] aBoundingSphere) {
		Log.i(SevenWondersGLRenderer.class.getName(), String.format("collided with " + Arrays.toString(aBoundingSphere)));

		// iterate through all animation geometries, moving the objects off screen
		final FastGeometryBuilder<?, ?> somewhereFarFarAway = mapOfNumebrOfVerticesToSomewhereFarFarAway.get(spellGeometries.get(0));
		for (int spellAnimationIndex = 0; spellAnimationIndex < SevenWondersGLRenderer.NUMBER_OF_SPINNING_ANIMATION_FRAMES; spellAnimationIndex++) {
			spellGeometries.get(spellAnimationIndex).updateModel(somewhereFarFarAway);
		}

		performCustomCollisionOccurredAction();
		
		// suppress future collisions
		return true;
	}

	protected abstract void performCustomCollisionOccurredAction();
}