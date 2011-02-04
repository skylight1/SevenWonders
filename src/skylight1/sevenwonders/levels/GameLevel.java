package skylight1.sevenwonders.levels;

import java.util.ArrayList;
import java.util.Collection;

import skylight1.sevenwonders.R;
import skylight1.sevenwonders.view.CubeBounds;

public enum GameLevel {
	FIRST(5, 3);

	private static final float HEIGHT_OF_SPELLS_FROM_GROUND = 11f;

	private static final int WORLD_SPELL_MARGIN = 200;

	private int numberOfSpells;

	private int numberOfSpellsRequired;

	private GameLevel(int aNumberOfSpells, int aPercentOfSpellsRequired) {
		numberOfSpells = aNumberOfSpells;
		numberOfSpellsRequired = aPercentOfSpellsRequired;
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
		final float minX1 = (CubeBounds.TERRAIN.x1 + WORLD_SPELL_MARGIN);
		final float minX2 = (CubeBounds.TERRAIN.x2 - WORLD_SPELL_MARGIN);
		final float minZ1 = (CubeBounds.TERRAIN.z1 + WORLD_SPELL_MARGIN);
		final float minZ2 = (CubeBounds.TERRAIN.z2 - WORLD_SPELL_MARGIN);

		final Collection<GameObjectDescriptor> spells = new ArrayList<GameObjectDescriptor>(numberOfSpells);

		// create random locations for the spells
		for (int spellNumber = 0; spellNumber < numberOfSpells; spellNumber++) {
			final float x = createRandomInRange(minX1, minX2);
			final float z = createRandomInRange(minZ1, minZ2);

			final float[] transformationMatrix = new float[16];
			android.opengl.Matrix.setIdentityM(transformationMatrix, 0);
			android.opengl.Matrix.translateM(transformationMatrix, 0, x, HEIGHT_OF_SPELLS_FROM_GROUND, z);

			final GameObjectDescriptor newGameObjectDescriptor = new GameObjectDescriptor(transformationMatrix, R.raw.ankh);
			spells.add(newGameObjectDescriptor);
		}

		return spells;
	}

	private static final float createRandomInRange(final float min, final float max) {
		final float range = max - min + 1;
		return (float) (min + Math.random() * range);
	}
}
