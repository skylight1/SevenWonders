package skylight1.sevenwonders.levels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import skylight1.sevenwonders.R;
import skylight1.sevenwonders.view.CubeBounds;
import android.opengl.Matrix;

public enum GameLevel {
	FIRST(5, 3, 3, 1), SECOND(2, 6, 3, 2);

	private static final float HEIGHT_OF_HAZZARDS_FROM_GROUND = 9f;

	private static final float HEIGHT_OF_SPELLS_FROM_GROUND = 11f;

	private static final int WORLD_OBJECT_MARGIN = 200;

	private final int numberOfSpells;

	private final int numberOfHazzards;

	private final int numberOfSpellsRequired;

	private final Random random;

	private GameLevel(final int aNumberOfSpells, final int aNumberOfHazzards, final int aNumberOfSpellsRequired,
			final long aRandomSeed) {
		numberOfSpells = aNumberOfSpells;
		numberOfHazzards = aNumberOfHazzards;
		numberOfSpellsRequired = aNumberOfSpellsRequired;
		random = new Random(aRandomSeed);
	}

	/**
	 * @return number of spells in the level
	 */
	public int getNumberOfSpells() {
		return numberOfSpells;
	}

	/**
	 * @return number of spells to win
	 */
	public int getNumberOfSpellsRequired() {
		return numberOfSpellsRequired;
	}

	/**
	 * Things that will earn the player a point if the player "collects" them: ankhs, gems, etc.
	 */
	public Collection<GameObjectDescriptor> getSpells() {
		// the texture is within the main texture, so it needs a little transformation to map onto the spell
		float[] textureTransform = new float[16];
		Matrix.setIdentityM(textureTransform, 0);
		Matrix.translateM(textureTransform, 0, 576f / 1024f, 0, 0);
		Matrix.scaleM(textureTransform, 0, 0.25f, 0.25f, 1f);

		return createObjectsAtRandomLocations(HEIGHT_OF_SPELLS_FROM_GROUND, numberOfSpells, textureTransform, R.raw.ankh);
	}

	/**
	 * Things that will kill the player if the player flies into them: swords, whirlwinds, etc.
	 */
	public Collection<GameObjectDescriptor> getHazzards() {
		return createObjectsAtRandomLocations(HEIGHT_OF_HAZZARDS_FROM_GROUND, numberOfHazzards, null, R.raw.textured_sword);
	}

	private Collection<GameObjectDescriptor> createObjectsAtRandomLocations(final float aHeightOfObjectFromGround,
			final int aNumberOfObjects, float[] aTextureTransform, final int anObjectFileResourceId) {
		final float minX1 = (CubeBounds.TERRAIN.x1 + WORLD_OBJECT_MARGIN);
		final float minX2 = (CubeBounds.TERRAIN.x2 - WORLD_OBJECT_MARGIN);
		final float minZ1 = (CubeBounds.TERRAIN.z1 + WORLD_OBJECT_MARGIN);
		final float minZ2 = (CubeBounds.TERRAIN.z2 - WORLD_OBJECT_MARGIN);

		final Collection<GameObjectDescriptor> objects = new ArrayList<GameObjectDescriptor>(aNumberOfObjects);

		// create random locations for the objects
		for (int objectIndex = 0; objectIndex < aNumberOfObjects; objectIndex++) {
			final float x = createRandomInRange(minX1, minX2);
			final float z = createRandomInRange(minZ1, minZ2);

			final float[] transformationMatrix = new float[16];
			android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
			android.opengl.Matrix.translateM(transformationMatrix, 0, x, aHeightOfObjectFromGround, z);

			final GameObjectDescriptor newGameObjectDescriptor = new GameObjectDescriptor(transformationMatrix, aTextureTransform, anObjectFileResourceId);
			objects.add(newGameObjectDescriptor);
		}

		return objects;
	}

	private final float createRandomInRange(final float min, final float max) {
		final float range = max - min + 1;
		return min + random.nextFloat() * range;
	}
}