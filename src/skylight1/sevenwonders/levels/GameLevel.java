package skylight1.sevenwonders.levels;

import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addHazard;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addPyramid;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addRuby;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addCoin;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addEmerald;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addSpell;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addSphynx;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.createNewIdentityMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import skylight1.sevenwonders.R;

public enum GameLevel {

	// This level has one ankh and one pyramid.
	// Both are immediately in view as soon as you start so are easy to find.
	// The pyramid makes a good landmark for users who get lost 
	// and can't see the ankh from far away.
	TEACH_COLLECTION_WITH_EASY_LANDMARK(
		"The ankh is a symbol of powerful magic." +
		"Collect the ankh in front of the pyramid.") {
		{
			addPyramid(this, 0, -80, 0, -450);
			addSpell(this, -20, -310);
		}
	},
	
	// This level has one ankh near each of three pyramids.
	// The user has to navigate between them.
	TEACH_COLLECTING_MULTIPLE_WITH_EASY_LANDMARKS(
		"A powerful ankh is near each pyramid." +
		"Collect them all.") {
		{
			addPyramid(this, 0, -80, 0, -450);
			addSpell(this, -20, -310);

			addPyramid(this, 0, -140, 0, -350);
			addSpell(this, -160, -210);

			addPyramid(this, 0, -140, 0, 350);
			addSpell(this, -160, 400);
		}
	},
	
	FIRST("The ankh is a symbol of powerful magic. Collect all three.") {
		{
			addSphynx(this, 90, -190, -30, -90);
			addPyramid(this, 0, -220, 0, 100);
			addPyramid(this, 0, 655, 0, 110);
			addPyramid(this, 0, -620, -7, 100);

			addRuby(this, 30, -170);

			addCoin(this, 30, -190);
			
			addEmerald(this, 30, -210);
			
			addSpell(this, 30, -130);
			addSpell(this, 150, -200);
			addSpell(this, 300, -180);
		}
	},
	SECOND("Nebtawi, the evil vizier, has spread the ankhs far and wide.") {
		{
			addSphynx(this, 90, -190, -30, -90);
			addPyramid(this, 0, -220, 0, 100);
			addPyramid(this, 0, 655, 0, 110);
			addPyramid(this, 0, -620, -7, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);
			addSpell(this, 400, -160);
			addSpell(this, 500, -100);

		}
	},
	THIRD("Race to find all five ankhs before the nefarious Nebtawi arrives.") {
		{
			addSphynx(this, 90, -190, -30, -90);
			addPyramid(this, 0, -220, 0, 100);
			addPyramid(this, 0, 655, 0, 110);
			addPyramid(this, 0, -620, -7, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);
			addSpell(this, -200, 100);
			addSpell(this, 100, 400);
		}
	},
	FOURTH("Beware! Nebtawi has set a deadly flying sword to block your path.") {
		{
			addSphynx(this, 90, -190, -30, -90);
			addPyramid(this, 0, -220, 0, 100);
			addPyramid(this, 0, 655, 0, 110);
			addPyramid(this, 0, -620, -7, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);
			addSpell(this, -200, 100);
			addSpell(this, 100, 400);

			addHazard(this, 30, -145);
		}
	},
	FIFTH("Many swords will bar your way, but the ankhs must be recovered if Egypt is to be saved.") {
		{
			addSphynx(this, 90, -190, -30, -90);
			addPyramid(this, 0, -220, 0, 100);
			addPyramid(this, 0, 655, 0, 110);
			addPyramid(this, 0, -620, -7, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);

			addHazard(this, 35, -120);
			addHazard(this, 30, -145);
		}
	},
	SIXTH("With each ankh recovered, Nebtawi's power wanes. Hurry now!") {
		{
			addSphynx(this, 90, -190, -30, -90);
			addPyramid(this, 0, -220, 0, 100);
			addPyramid(this, 0, 655, 0, 110);
			addPyramid(this, 0, -620, -7, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);

			addHazard(this, 35, -130);
			addHazard(this, 30, -135);
			addHazard(this, 145, -130);
			addHazard(this, 290, -125);
		}
	},
	SEVENTH("Collect the remaining ankhs and Nebtawi will be defeated in Egypt!") {
		{
			addSphynx(this, 90, -190, -30, -90);
			addPyramid(this, 0, -220, 0, 100);
			addPyramid(this, 0, 655, 0, 110);
			addPyramid(this, 0, -620, -7, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);

			addHazard(this, 35, -130);
			addHazard(this, 25, -130);
			addHazard(this, 30, -135);
			addHazard(this, 30, -125);
		}
	};

	private final String loadingMessage;

	final Map<GameObjectDescriptor, CollisionAction> mapOfCollectablesToCollisionActions = new HashMap<GameObjectDescriptor, CollisionAction>();

	final Map<GameObjectDescriptor, CollisionAction> mapOfHazardsToCollisionActions = new HashMap<GameObjectDescriptor, CollisionAction>();

	final Collection<GameObjectDescriptor> decorations = new ArrayList<GameObjectDescriptor>();

	final Collection<float[]> obstacles = new ArrayList<float[]>();
	
	int numberOfSpells = 0;

	/**
	 */
	private GameLevel(final String aLoadingMessage) {
		loadingMessage = aLoadingMessage;

		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.ground, R.raw.dunes));
		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.water, R.raw.textures));
	}

	/**
	 * @return number of mapOfCollectablesToCollisionActions in the level
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
	public Map<GameObjectDescriptor, CollisionAction> getCollectables() {
		return mapOfCollectablesToCollisionActions;
	}

	/**
	 * Things that will kill the player if the player flies into them: swords, whirl winds, etc.
	 */
	public Map<GameObjectDescriptor, CollisionAction> getHazards() {
		return mapOfHazardsToCollisionActions;
	}

	/**
	 * Things that are pure eye candy, such as the ground and river.
	 */
	public Collection<GameObjectDescriptor> getDecorations() {
		return decorations;
	}

	/**
	 * Things that the player cannot pass through: pyramids, etc.
	 * 
	 * Returns a bounding sphere, i.e., x, y, z, r.
	 */
	public Collection<float[]> getObstacles() {
		return obstacles;
	}
}