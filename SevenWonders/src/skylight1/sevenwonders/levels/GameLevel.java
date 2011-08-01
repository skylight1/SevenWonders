package skylight1.sevenwonders.levels;

import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addHazard;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addPyramid;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addRuby;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addCoin;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addProtection;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addSpell;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.addSphynx;
import static skylight1.sevenwonders.levels.LevelConstructionToolkit.createNewIdentityMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import skylight1.sevenwonders.R;

public enum GameLevel {

	// This level=1 has one ankh and one pyramid.
	// Both are immediately in view as soon as you start so are easy to find.
	// The pyramid makes a good landmark for users who get lost 
	// and can't see the ankh from far away.
	TEACH_COLLECTION_WITH_EASY_LANDMARK(
		"The ankh is a symbol of powerful magic. Collect it before Nebtawi arrives.", R.drawable.icon) {
		{
			setTotalTimeAllowedInSeconds(100);
			addPyramid(this,-80, -450);
			addSpell(this, 20, -310);
		}
	},
	
	// This level=2 has one ankh near each of three pyramids.
	// The user has to navigate between them.
	TEACH_COLLECTING_MULTIPLE_WITH_EASY_LANDMARKS(
		"Each of three pyramids has an ankh nearby.  Collect all three.", R.drawable.icon) {
		{
			addPyramid(this,-80, -450);
			addSpell(this, 20, -310);

			addPyramid(this,-340, -550);
			addSpell(this, -350, -350);

			addPyramid(this,-340, 550);
			addSpell(this, -480, 430);
		}
	},
	
	// This level=3 has a trail of ankhs and coins the user has to follow.
	TEACH_COLLECTING_NO_LANDMARKS(
		"Nebtawi, the evil vizier, has spread the ankhs far and wide. Race to find all five.", R.drawable.icon) {
		{
			addSphynx(this, -190, -90);
			addPyramid(this,-220, 100);
			addPyramid(this,655, 110);
			addPyramid(this,-620, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);
			addSpell(this, -370, 100);
			addSpell(this, 100, 400);
		}
	},
	// This level=4
	TEACH_COIN(
		"Nebtawi's magic has brought forth lost treasures from the desert. Riches await you.", R.drawable.icon) {
		{
			addPyramid(this,-80, -450);
			addSphynx(this, -190, -90);
			
			addCoin(this, 130, -180);
			addSpell(this, 280,  -30);
			addCoin(this, 430, 120);
			addSpell(this, 570, 270);
			addCoin(this, 590, 300);
			addSpell(this, 610, 350);
		}
	},
	
	// This level=5 has a sword that has to be flown around to get the ankh.
	TEACH_AVOIDING_SWORDS(
		"Fly wide around Nebtawi's flying sword to collect the ankh behind it!", R.drawable.icon) {
		{
			addPyramid(this,-80, -450);
			addSphynx(this, -190, -90);
		
			addSpell(this, 20, -310);
			addHazard(this, 20, -250);
		}
	},
	// This level=6
	MANY_SWORDS(
		"Many swords will bar your way, but the ankhs must be recovered if Egypt is to be saved.", R.drawable.icon) {
		{
			addPyramid(this,-80, -450);
			addSphynx(this, -190, -90);

			addSpell(this,  -150, -400);
			addHazard(this, -190, -400);
			addHazard(this, -125, -425);
			addHazard(this, -100, -450);
			addHazard(this, -75, -425);
			addSpell(this,  100, 400);
			addHazard(this, 150, 400);
			addHazard(this, 125, 425);
			addHazard(this, 100, 450);
			addHazard(this, 75, 425);
			addCoin(this, 200, 200);
			addCoin(this, 200, 210);
		}
	},
	// This level=7
	THE_GAUNTLET(
		"Fly carefully between the swords to recover the ankhs.", R.drawable.icon) {
		{
			addPyramid(this,-80, -450);
			addSphynx(this, -190, -90);
			
			addSpell(this,  -170, -300);
			addHazard(this, -150, -400);
			addHazard(this, -125, -425);
			addHazard(this, -100, -450);
			addHazard(this, -75, -425);
			addHazard(this, 150, 400);
			addHazard(this, 125, 425);
			addHazard(this, 100, 450);
			addHazard(this, 75, 425);
			addCoin(this, 200, 200);
			addCoin(this, 200, 210);
		}
	},
	
	// This level=8 teaches using the shield.
	TEACH_INVULNERABILITY(
		"Collect the Sphinx's shield to pass safely through the storm of swords.", R.drawable.shield) {
		{
			addPyramid(this,-80, -450);
			addSphynx(this, -190, -90);

			addSpell(this,  -100, -300);
			addHazard(this, -150, -400);
			addHazard(this, -125, -425);
			addHazard(this, -100, -450);
			addHazard(this,  -75, -425);
			addHazard(this,  -50, -400);
			addHazard(this,  -75, -375);
			addHazard(this, -100, -350);
			addHazard(this, -125, -375);
			addHazard(this, -150, -400);
			addProtection(this, -25, 90);
		}
	},
	
	// This level=9 requires using the shield.
	TEACH_INVULNERABILITY_TIME_LIMIT(
		"The shield does not last forever. Move quickly through the storms of swords.", R.drawable.shield) {
		{
			addPyramid(this,-80, -450);
			addSphynx(this, -190, -90);
			
			addSpell(this,  -100, -200);
			addHazard(this, -150, -400);
			addHazard(this, -125, -425);
			addHazard(this, -100, -450);
			addHazard(this, -75, -425);
			addHazard(this, 150, 400);
			addHazard(this, 125, 425);
			addHazard(this, 100, 450);
			addHazard(this, 75, 425);
			addCoin(this, 200, 200);
			addCoin(this, 200, 210);
			addProtection(this, -25, 90);
		}
	},
	
	// This level=10 requires using the shield.
	LOTS_OF_SWORDS_N_SHIELDS(
		"Think of some clever title", R.drawable.shield) {
		{
			addPyramid(this,-80, -450);
			addSphynx(this, -190, -90);
			//the more negative(z) means farther away
			//addSpell(this,x,z)
			addSpell(this,  -100, -600);
			addHazard(this, -150, -400);
			addHazard(this, -125, -425);
			addHazard(this, -100, -450);
			addHazard(this, -75, -425);
			addHazard(this, 150, 400);
			addHazard(this, 125, 425);
			addHazard(this, 100, 450);
			addHazard(this, 75, 425);
			addCoin(this, 200, 200);
			addCoin(this, 200, 210);
			addProtection(this, -25, 90);
		}
	},
	
	// This level requires using the pass through objects ruby.
	TEACH_RUBY(
		"Get the Sphinx's red ruby of passage to retrieve the ankh inside the pyramid.", R.drawable.ruby) {
		{
			addPyramid(this,-220, 100);
			addPyramid(this,655, 110);
			addPyramid(this,-620, 100);
			addSphynx(this, -190, -90);
			
			addSpell(this, -80, -450);
			addRuby(this, -25, 90);
			addCoin(this, 200, 200);
			addCoin(this, 200, 210);
		}
	},	
	
	// sword and shield inside pyramids
	SECOND_LAST(
		"With each ankh recovered, Nebtawi's power wanes. Hurry now!", R.drawable.ruby) {
		{
			addPyramid(this,-220, 100);
			addPyramid(this,655, 110);
			addPyramid(this,-620, 100);
			addSphynx(this, -190, -90);
			
			addProtection(this, -220, 100);
			addSpell(this, -80, -450);
			addRuby(this, -25, 90);
			addHazard(this, 150, 400);
			addHazard(this, 150, 400);
			addHazard(this, 150, 400);
			addHazard(this, 150, 400);
			addCoin(this, 200, 200);
			addCoin(this, 200, 210);
		}
	},	

	LAST("Collect the remaining ankhs and Nebtawi will be defeated in Egypt!", R.drawable.icon) {
		{
			addSphynx(this, -190, -90);
			addPyramid(this,-220, 100);
			addPyramid(this, 655, 110);
			addPyramid(this,-620, 100);

			addSpell(this, 30, -130);
			addSpell(this, 130, -150);
			addSpell(this, 300, -100);
			addSpell(this, 400, -160);
			addSpell(this, 500, -100);
		}
	};

	protected static final int DEFAULT_TOTAL_TIME_ALLOWED_IN_SECONDS = 180;

	private final String loadingMessage;

	final Map<GameObjectDescriptor, CollisionAction> mapOfCollectablesToCollisionActions = new HashMap<GameObjectDescriptor, CollisionAction>();

	final Map<GameObjectDescriptor, CollisionAction> mapOfHazardsToCollisionActions = new HashMap<GameObjectDescriptor, CollisionAction>();

	final Collection<GameObjectDescriptor> decorations = new ArrayList<GameObjectDescriptor>();

	final Collection<GameObjectDescriptor> glows = new ArrayList<GameObjectDescriptor>();

	final Collection<float[]> obstacles = new ArrayList<float[]>();
	
	final int iconResourceId;
	
	int numberOfSpells = 0;
	
	
	private int totalTimeAllowedInSeconds = DEFAULT_TOTAL_TIME_ALLOWED_IN_SECONDS;

	/**
	 */
	private GameLevel(final String aLoadingMessage, final int anIconResourceId) {
		loadingMessage = aLoadingMessage;
		iconResourceId = anIconResourceId;
		
		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.ground, R.raw.dunes));
		decorations.add(new GameObjectDescriptor(createNewIdentityMatrix(), null, R.raw.water, R.raw.textures));
	}

	public int getIconResourceId() {
		return iconResourceId;
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

	/**
	 * Gets the number of seconds the player has to complete the level.
	 * @return int number of seconds
	 */
	public int getDefaultTotalTimeAllowedInSeconds() {
		return totalTimeAllowedInSeconds;
	}

	/**
	 * Sets the number of seconds the player has to complete the level.
	 * @param int aTotalTimeAllowedInSeconds
	 */
	public void setTotalTimeAllowedInSeconds(int aTotalTimeAllowedInSeconds) {
		totalTimeAllowedInSeconds = aTotalTimeAllowedInSeconds;
	}

	/**
	 * Glows that brighten things.
	 */
	public Collection<GameObjectDescriptor> getGlows() {
		return glows;
	}
}