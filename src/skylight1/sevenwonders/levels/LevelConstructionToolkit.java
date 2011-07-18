package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.GameState;
import skylight1.sevenwonders.R;
import android.opengl.Matrix;

public class LevelConstructionToolkit {
	private static final ProtectionAction PROTECTION_ACTION = new ProtectionAction();

	private static final float HEIGHT_OF_HAZARDS_FROM_GROUND = 9f;

	private static final float HEIGHT_OF_SPELLS_FROM_GROUND = 11f;

	private static final float HEIGHT_OF_GEMS_FROM_GROUND = 12.5f;

	private static final float HEIGHT_OF_COINS_FROM_GROUND = 12f;

	private static final float[] SPELLS_TEXTURE_TRANSFORM = new float[16];
	static {
		// the texture is within the main texture, so it needs a little transformation to map onto the spell
		Matrix.setIdentityM(SPELLS_TEXTURE_TRANSFORM, 0);
		Matrix.translateM(SPELLS_TEXTURE_TRANSFORM, 0, 576f / 1024f, 0, 0);
		Matrix.scaleM(SPELLS_TEXTURE_TRANSFORM, 0, 0.25f, 0.25f, 1f);
	}

	private static final float[] COIN_TEXTURE_TRANSFORM = new float[16];

	static {
		// the texture is within the main texture, so it needs a little transformation to map onto the coin
		Matrix.setIdentityM(COIN_TEXTURE_TRANSFORM, 0);
		Matrix.translateM(COIN_TEXTURE_TRANSFORM, 0, 256f / 512f, 323f / 512f, 0);
		Matrix.scaleM(COIN_TEXTURE_TRANSFORM, 0, 100f / 512f, 189f / 512f, 1f);
	}

	static void addSpell(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] ankhTransformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(ankhTransformationMatrix, 0);
		android.opengl.Matrix.translateM(ankhTransformationMatrix, 0, anX, HEIGHT_OF_SPELLS_FROM_GROUND, aZ);
		// Ankhs were difficult to see, so double the size.
		android.opengl.Matrix.scaleM(ankhTransformationMatrix, 0, 2, 2, 2);
		final CollisionAction spellsCollisionAction = new SpellCollisionAction();
		aGameLevel.mapOfCollectablesToCollisionActions.put(new GameObjectDescriptor(ankhTransformationMatrix, SPELLS_TEXTURE_TRANSFORM, R.raw.ankh, R.raw.textures), spellsCollisionAction);

		aGameLevel.numberOfSpells++;

		// And show a column of transparent light on top of them.
		// TODO remove the column when appropriate: when the ankh is collected, on harder difficulty, after the level is won once, etc..
		final float[] highlightTransformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(highlightTransformationMatrix, 0);
		android.opengl.Matrix.translateM(highlightTransformationMatrix, 0, anX, 75, aZ);
		android.opengl.Matrix.scaleM(highlightTransformationMatrix, 0, 8, 2, 8);
		aGameLevel.glows.add(new GameObjectDescriptor(highlightTransformationMatrix, null, R.raw.transparent_white_cyclinder, R.raw.textures));
	}

	static void addRuby(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_GEMS_FROM_GROUND, aZ);
		android.opengl.Matrix.scaleM(transformationMatrix, 0, 2, 2, 2);
		final CollisionAction rubyCollisionAction = new RubyCollisionAction();
		aGameLevel.mapOfCollectablesToCollisionActions.put(new GameObjectDescriptor(transformationMatrix, null, R.raw.gem, R.raw.textures), rubyCollisionAction);
	}

	static void addProtection(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_GEMS_FROM_GROUND, aZ);
		android.opengl.Matrix.scaleM(transformationMatrix, 0, 0.5f, 0.5f, 0.5f);
		aGameLevel.mapOfCollectablesToCollisionActions.put(new GameObjectDescriptor(transformationMatrix, null, R.raw.shield, R.raw.textures), PROTECTION_ACTION);
	}

	static void addCoin(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_COINS_FROM_GROUND, aZ);
		android.opengl.Matrix.scaleM(transformationMatrix, 0, 2, 2, 2);
		final CollisionAction coinCollisionAction = new CoinCollisionAction();
		aGameLevel.mapOfCollectablesToCollisionActions.put(new GameObjectDescriptor(transformationMatrix, COIN_TEXTURE_TRANSFORM, R.raw.coin, R.raw.textures), coinCollisionAction);
	}
	
	static void addTimeAdded(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_COINS_FROM_GROUND, aZ);
		android.opengl.Matrix.scaleM(transformationMatrix, 0, 2, 2, 2);
		final CollisionAction extraTimeAction = new ExtraTimeAction();
		aGameLevel.mapOfCollectablesToCollisionActions.put(new GameObjectDescriptor(transformationMatrix, COIN_TEXTURE_TRANSFORM, R.raw.coin, R.raw.textures), extraTimeAction);
		throw new UnsupportedOperationException();
	}

	static void addHazard(final GameLevel aGameLevel, int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_HAZARDS_FROM_GROUND, aZ);
		final CollisionAction hazardCollisionAction = new HazardCollisionAction();
		aGameLevel.mapOfHazardsToCollisionActions.put(new GameObjectDescriptor(transformationMatrix, null, R.raw.textured_sword, R.raw.textures), hazardCollisionAction);
	}

	static float[] createNewIdentityMatrix() {
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		return matrix;
	}

	static float[] createBoundingSphere(float anX, float aY, float aZ, float aRadius) {
		return new float[] { anX, aY, aZ, aRadius };
	}

	/**
	 * 
	 * @param aGameLevel
	 * @param aRotation
	 * @param anX
	 * @param aY float how high the pyramid sticks out of the ground
	 * @param aZ
	 */
	static void addPyramid(final GameLevel aGameLevel, float aRotation, final float anX, final float aY, final float aZ) {
		float[] coordinateTransform = new float[16];
		Matrix.setIdentityM(coordinateTransform, 0);
		Matrix.rotateM(coordinateTransform, 0, aRotation, 0, 1, 0);
		Matrix.translateM(coordinateTransform, 0, anX, aY - 25, aZ);
		aGameLevel.decorations.add(new GameObjectDescriptor(coordinateTransform, null, R.raw.pyramid, R.raw.textures));

		aGameLevel.obstacles.add(new float[] { anX, GameState.HEIGHT_OF_CARPET_FROM_GROUND, aZ, 90 });
	}

	static void addSphynx(final GameLevel aGameLevel, final float aRotation, final float anX, final float aY,
			final float aZ) {
		float[] coordinateTransform = new float[16];
		Matrix.setIdentityM(coordinateTransform, 0);
		Matrix.rotateM(coordinateTransform, 0, aRotation, 0, 1, 0);
		Matrix.translateM(coordinateTransform, 0, anX, aY, aZ);
		float[] textureTransform = new float[16];
		Matrix.setIdentityM(textureTransform, 0);
		aGameLevel.decorations.add(new GameObjectDescriptor(coordinateTransform, null, R.raw.sphinx_scaled, R.raw.sphinx));
	}
}