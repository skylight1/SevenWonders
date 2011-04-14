package skylight1.sevenwonders.levels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import skylight1.sevenwonders.R;
import android.opengl.Matrix;

public enum GameLevel {
	FIRST(5, "The ankh is a symbol of powerful magic. Collect all five.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = new ArrayList<GameObjectDescriptor>();
			decorations.addAll(super.getDecorations());
			decorations.add(createSphynx(90, -190, -30, -90));
			decorations.add(createPyramid(0, -220, 0, 100));
			decorations.add(createPyramid(0, 655, 0, 110));
			decorations.add(createPyramid(0, -620, -7, 100));
			return decorations;
		}

		@Override
		public Collection<GameObjectDescriptor> getSpells() {
			// three spells on this level
			final List<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>();
			spells.add(createSpell(30, -130));
			spells.add(createSpell(150, -200));
			spells.add(createSpell(300, -180));
			spells.add(createSpell(400, -160));
			spells.add(createSpell(500, -100));
			return spells;
		}
		
		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
		
		@Override
		public Collection<GameObjectDescriptor> getHazards() {
			// no hazards on this level
			return Collections.<GameObjectDescriptor>emptyList();
		}
	},
	SECOND(3, "Nebtawi, the evil vizier, has spread the ankhs far and wide.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(createSphynx(90, -190, -30, -90));
			return decorations;
		}

		@Override
		public Collection<GameObjectDescriptor> getSpells() {
			// three spells on this level
			final List<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>();
			spells.add(createSpell(30, -130));
			spells.add(createSpell(130, -150));
			spells.add(createSpell(300, -100));
			return spells;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
		
		@Override
		public Collection<GameObjectDescriptor> getHazards() {
			// no hazards on this level
			return Collections.<GameObjectDescriptor>emptyList();
		}
	},
	THIRD(5, "Race to find all five ankhs before the nefarious Nebtawi arrives.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(createSphynx(90, -190, -30, -90));
			return decorations;
		}

		@Override
		public Collection<GameObjectDescriptor> getSpells() {
			// three spells on this level
			final List<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>();
			spells.add(createSpell(30, -130));
			spells.add(createSpell(130, -150));
			spells.add(createSpell(300, -100));
			spells.add(createSpell(-200, 100));
			spells.add(createSpell(100, 400));
			return spells;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
		
		@Override
		public Collection<GameObjectDescriptor> getHazards() {
			// no hazards on this level
			return Collections.<GameObjectDescriptor>emptyList();
		}
	},
	FOURTH(5, "Beware! Nebtawi has set a deadly flying sword to block your path.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(createSphynx(90, -190, -30, -90));
			return decorations;
		}

		@Override
		public Collection<GameObjectDescriptor> getSpells() {
			// three spells on this level
			final List<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>();
			spells.add(createSpell(30, -130));
			spells.add(createSpell(130, -150));
			spells.add(createSpell(300, -100));
			spells.add(createSpell(-200, 100));
			spells.add(createSpell(100, 400));
			return spells;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
		
		@Override
		public Collection<GameObjectDescriptor> getHazards() {
			// three spells on this level
			final List<GameObjectDescriptor> hazards = new ArrayList<GameObjectDescriptor>();
			hazards.add(createHazard(30, -145));
			return hazards;
		}
	},
	FIFTH(3, "Many swords will bar your way, but the ankhs must be recovered if Egypt is to be saved.") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(createSphynx(90, -190, -30, -90));
			return decorations;
		}

		@Override
		public Collection<GameObjectDescriptor> getSpells() {
			// three spells on this level
			final List<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>();
			spells.add(createSpell(30, -130));
			spells.add(createSpell(130, -150));
			spells.add(createSpell(300, -100));
			
			return spells;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
		
		@Override
		public Collection<GameObjectDescriptor> getHazards() {
			// three spells on this level
			final List<GameObjectDescriptor> hazards = new ArrayList<GameObjectDescriptor>();
			hazards.add(createHazard(35, -130));
			hazards.add(createHazard(30, -135));
			return hazards;
		}
	},
	SIXTH(3, "With each ankh recovered, Nebtawi's power wanes. Hurry now!") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(createSphynx(90, -190, -30, -90));
			return decorations;
		}

		@Override
		public Collection<GameObjectDescriptor> getSpells() {
			// three spells on this level
			final List<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>();
			spells.add(createSpell(30, -130));
			spells.add(createSpell(130, -150));
			spells.add(createSpell(300, -100));
			
			return spells;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
		
		@Override
		public Collection<GameObjectDescriptor> getHazards() {
			// three spells on this level
			final List<GameObjectDescriptor> hazards = new ArrayList<GameObjectDescriptor>();
			hazards.add(createHazard(35, -130));
			hazards.add(createHazard(30, -135));
			hazards.add(createHazard(145, -130));
			hazards.add(createHazard(290, -125));
			return hazards;
		}
	},
	SEVENTH(3, "Collect the remaining ankhs and Nebtawi will be defeated in Egypt!") {
		public List<GameObjectDescriptor> getDecorations() {
			final List<GameObjectDescriptor> decorations = super.getDecorations();
			decorations.add(createSphynx(90, -190, -30, -90));
			return decorations;
		}

		@Override
		public Collection<GameObjectDescriptor> getSpells() {
			// three spells on this level
			final List<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>();
			spells.add(createSpell(30, -130));
			spells.add(createSpell(130, -150));
			spells.add(createSpell(300, -100));
			
			return spells;
		}

		public List<float[]> getObstacles() {
			final List<float[]> obstacles = new ArrayList<float[]>();
			obstacles.add(createBoundingSphere(0f, 0f, 0f, 100f));
			return obstacles;
		}
		
		@Override
		public Collection<GameObjectDescriptor> getHazards() {
			// three spells on this level
			final List<GameObjectDescriptor> hazards = new ArrayList<GameObjectDescriptor>();
			hazards.add(createHazard(35, -130));
			hazards.add(createHazard(25, -130));
			hazards.add(createHazard(30, -135));
			hazards.add(createHazard(30, -125));
			return hazards;
		}
	};

	private static final float HEIGHT_OF_HAZARDS_FROM_GROUND = 9f;

	private static final float HEIGHT_OF_SPELLS_FROM_GROUND = 11f;

	private static final float[] SPELLS_TEXTURE_TRANSFORM = new float[16];
	
	static {
		// the texture is within the main texture, so it needs a little transformation to map onto the spell
		Matrix.setIdentityM(SPELLS_TEXTURE_TRANSFORM, 0);
		Matrix.translateM(SPELLS_TEXTURE_TRANSFORM, 0, 576f / 1024f, 0, 0);
		Matrix.scaleM(SPELLS_TEXTURE_TRANSFORM, 0, 0.25f, 0.25f, 1f);
	}
	
	private final int numberOfSpells;

	private final String loadingMessage;

	/**
	 */
	private GameLevel(final int aNumberOfSpells, final String aLoadingMessage) {
		numberOfSpells = aNumberOfSpells;
		loadingMessage = aLoadingMessage;
	}

	/**
	 * @return number of spells in the level
	 */
	public int getNumberOfSpells() {
		return numberOfSpells;
	}
	
	public String getLoadingMessage() {
		return loadingMessage;
	}

	/**
	 * Things that will earn the player a point if the player "collects" them: ankhs, gems, etc.
	 */
	public abstract Collection<GameObjectDescriptor> getSpells();
	
	/**
	 * Things that will kill the player if the player flies into them: swords, whirl winds, etc.
	 */
	public abstract Collection<GameObjectDescriptor> getHazards();
	
	/**
	 * Things that the player cannot pass through: pyramids, etc.
	 */
	public abstract List<float[]> getObstacles();

	public List<GameObjectDescriptor> getDecorations() {
		final List<GameObjectDescriptor> decorations = new ArrayList<GameObjectDescriptor>();
		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.ground, R.raw.dunes));
		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.water, R.raw.textures));
		return decorations;
	}
	
	private static GameObjectDescriptor createSpell(int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_SPELLS_FROM_GROUND, aZ);
		
		return new GameObjectDescriptor(transformationMatrix, SPELLS_TEXTURE_TRANSFORM, R.raw.ankh, R.raw.textures);
	}
	
	private static GameObjectDescriptor createHazard(int anX, int aZ) {
		final float[] transformationMatrix = new float[16];
		android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
		android.opengl.Matrix.translateM(transformationMatrix, 0, anX, HEIGHT_OF_HAZARDS_FROM_GROUND, aZ);
		
		return new GameObjectDescriptor(transformationMatrix, null, R.raw.textured_sword, R.raw.textures);
	}

	private static float[] createNewIdentityMatrix() {
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		return matrix;
	}

	private static float[] createBoundingSphere(float anX, float aY, float aZ, float aRadius) {
		return new float[] { anX, aY, aZ, aRadius };
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
}