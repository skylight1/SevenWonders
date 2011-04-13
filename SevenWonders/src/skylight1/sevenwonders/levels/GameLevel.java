package skylight1.sevenwonders.levels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import skylight1.sevenwonders.R;
import skylight1.sevenwonders.view.CubeBounds;
import android.opengl.Matrix;

public enum GameLevel {
	FIRST(5, 3, 1, "The ankh is a symbol of powerful magic. Collect all five.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = new ArrayList<GameObjectDescriptor>();
			decorations.add(createSphynx(90, -140, -46, 0));
			decorations.addAll(super.getDecorations());
			decorations.add(createPyramid(0, -220, 0, 100));
			decorations.add(createPyramid(0, 655, 0, 110));
			decorations.add(createPyramid(0, -620, -7, 100));
			return decorations;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
	},
	SECOND(2, 6, 2, "Nebtawi, the evil vizier, has spread the ankhs far and wide.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.sphinx_scaled, R.raw.sphinx));
			return decorations;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
	},
	THIRD(5, 4, 3, "Race to find all five ankhs before the nefarious Nebtawi arrives.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.sphinx_scaled, R.raw.sphinx));
			return decorations;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
	},
	FOURTH(5, 6, 3, "Beware! Nebtawi has set a deadly flying sword to block your path.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.sphinx_scaled, R.raw.sphinx));
			return decorations;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
	},
	FIFTH(4, 8, 3, "Many swords will bar your way, but the ankhs must be recovered if Egypt is to be saved.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.sphinx_scaled, R.raw.sphinx));
			return decorations;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
	},
	SIXTH(4, 10, 3, "With each ankh recovered, Nebtawi's power wanes. Hurry now!") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.sphinx_scaled, R.raw.sphinx));
			return decorations;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
	},
	SEVENTH(3, 12, 3, "Collect the remaining ankhs and Nebtawi will be defeated in Egypt!") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.sphinx_scaled, R.raw.sphinx));
			return decorations;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
	};

	private static final float HEIGHT_OF_HAZARDS_FROM_GROUND = 9f;

	private static final float HEIGHT_OF_SPELLS_FROM_GROUND = 11f;

	private static final int WORLD_OBJECT_MARGIN = 200;

	private final int numberOfSpells;

	private final int numberOfHazards;

	private final Random random;
	
	private final String loadingMessage;

	/**
	 * @param aRandomSeed
	 *            Warning! The seed must not result in spells being located within obstacles as it will result in
	 *            uncollectable spells and incompletable levels!
	 */
	private GameLevel(final int aNumberOfSpells, final int aNumberOfHazzards, final long aRandomSeed, final String aLoadingMessage) {
		numberOfSpells = aNumberOfSpells;
		numberOfHazards = aNumberOfHazzards;
		random = new Random(aRandomSeed);
		loadingMessage = aLoadingMessage;
	}
	
	/**
	 * @return number of spells in the level
	 */
	public int getNumberOfSpells() {
		return numberOfSpells;
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

		return createObjectsAtRandomLocations(HEIGHT_OF_SPELLS_FROM_GROUND, numberOfSpells, textureTransform, R.raw.ankh, R.raw.textures);
	}

	/**
	 * Things that will kill the player if the player flies into them: swords, whirlwinds, etc.
	 */
	public Collection<GameObjectDescriptor> getHazards() {
		return createObjectsAtRandomLocations(HEIGHT_OF_HAZARDS_FROM_GROUND, numberOfHazards, null, R.raw.textured_sword, R.raw.textures);
	}

	public List<GameObjectDescriptor> getDecorations() {
		final List<GameObjectDescriptor> decorations = new ArrayList<GameObjectDescriptor>();
		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.ground, R.raw.dunes));
		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.water, R.raw.textures));
		return decorations;
	}

	private static float[] createNewIdentityMatrix() {
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		return matrix;
	}

	abstract public List<float[]> getObstacles();

	private static float[] createBoundingSphere(float anX, float aY, float aZ, float aRadius) {
		return new float[] { anX, aY, aZ, aRadius };
	}

	private Collection<GameObjectDescriptor> createObjectsAtRandomLocations(final float aHeightOfObjectFromGround,
			final int aNumberOfObjects, float[] aTextureTransform, final int anObjectFileResourceId,
			final int aTextureFileResourceId) {
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

			final GameObjectDescriptor newGameObjectDescriptor = new GameObjectDescriptor(transformationMatrix, aTextureTransform, anObjectFileResourceId, aTextureFileResourceId);
			objects.add(newGameObjectDescriptor);
		}

		return objects;
	}

	private final float createRandomInRange(final float min, final float max) {
		final float range = max - min + 1;
		return min + random.nextFloat() * range;
	}

	private final static GameObjectDescriptor createPyramid(float aRotation, final float anX, final float aY, final float aZ) {
		float[] coordinateTransform = new float[16];
		Matrix.setIdentityM(coordinateTransform, 0);
		Matrix.rotateM(coordinateTransform, 0, aRotation, 0, 1, 0);
		Matrix.translateM(coordinateTransform, 0, anX, aY, aZ);

		return new GameObjectDescriptor(coordinateTransform, null, R.raw.pyramid, R.raw.textures);
	}
	
	private final static GameObjectDescriptor createSphynx(final float aRotation, final float anX, final float aY, final float aZ) {
		float[] coordinateTransform = new float[16];
		Matrix.setIdentityM(coordinateTransform, 0);
		Matrix.rotateM(coordinateTransform, 0, aRotation, 0, 1, 0);
		Matrix.translateM(coordinateTransform, 0, anX, aY, aZ);
		float[] textureTransform = new float[16];
		Matrix.setIdentityM(textureTransform, 0);

		return new GameObjectDescriptor(coordinateTransform, null, R.raw.sphinx_scaled, R.raw.sphinx);
	}

	public String getLoadingMessage() {
		return loadingMessage;
	}
}