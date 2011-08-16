package skylight1.sevenwonders.levels;

import static skylight1.sevenwonders.levels.LevelConstructionToolkit.*;
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
		"The ankh is a symbol of powerful magic. Collect it before Nebtawi arrives.", R.drawable.ankh) {
		{
			setTotalTimeAllowedInSeconds(100);
			addPyramid(this,-80, -450);
			addSpell(this, 20, -310);
		}
	},
	
	// This level=2 has one ankh near each of three pyramids.
	// The user has to navigate between them.
	TEACH_COLLECTING_MULTIPLE_WITH_EASY_LANDMARKS(
		"Each of three pyramids has an ankh nearby.  Collect all three.", R.drawable.ankh) {
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
		"Nebtawi, the evil vizier, has spread the ankhs far and wide. Race to find all five.", R.drawable.coin) {
		{
			addSphynx(this, -90, -190);
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
		"Nebtawi's magic has brought forth lost treasures from the desert. Riches await you.", R.drawable.coin) {
		{
			addSphynx(this, -190, -90);			
			addPyramid(this,-80, -450);
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
		"Fly wide around Nebtawi's flying sword to collect the ankh behind it!", R.drawable.sword) {
		{
			addSphynx(this, -190, -90);
			addPyramid(this,-80, -450);
			addSpell(this, 20, -310);
			addHazard(this, 20, -250);
		}
	},
	// This level=6
	MANY_SWORDS(
		"Many swords will bar your way, but the ankhs must be recovered if Egypt is to be saved.", R.drawable.sword) {
		{
			addSphynx(this, -190, -90);
			addPyramid(this,-80, -450);
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
		"Fly carefully between the swords to recover the ankhs.", R.drawable.sword) {
		{
			addSphynx(this, -190, -90);
			addPyramid(this, -465, -390);
			addHazard(this, -275, -334);
			addHazard(this, -312, -450);
			addHazard(this, -381, -421);
			addHazard(this, -312, -425);
			addSpell(this, -346, -371);
			addHazard(this, -378, -340);
			addHazard(this, -312, -331);
			addHazard(this, -356, -343);
			addSpell(this, -221, -365);
			
			addPyramid(this,-80, -450);			
		}
	},
	
	// This level=8 teaches using the shield.
	TEACH_INVULNERABILITY(
		"Collect the Sphinx's shield to pass safely through the storm of swords.", R.drawable.shield) {
		{
			addSphynx(this, -190, -90);
			addPyramid(this,-80, -650);
			addSpell(this,  -100, -400);
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
			addSphynx(this, -441, -328);
			addPyramid(this, -200, -800);
			addSpell(this, -122, -494);
			addHazard(this, -134, -487);
			addHazard(this, -137, -550);
			addHazard(this, -100, -531);
			addHazard(this, -156, -509);
			addHazard(this, -72, 359);
			addHazard(this, -137, 381);
			addHazard(this, -100, 359);
			addHazard(this, -69, 434);
			addCoin(this, -903, -878);
			addCoin(this, -722, -653);
			addProtection(this, -587, -484);
			addCoin(this, -834, -803);
			addHazard(this, -109, -491);
			addSpell(this, -94, 413);
			addHazard(this, -134, 431);
			addHazard(this, -100, 453);
			addHazard(this, -59, 394);
			addCoin(this, -781, -728);
			addCoin(this, -953, -950);
			addPyramid(this, 522, 631);
		}
	},
	
	// This level=10 requires using the shield.
	LOTS_OF_SWORDS_N_SHIELDS(
		"Magical swords fly everywhere! Evade and find the ankh.", R.drawable.shield) {
		{
			addSphynx(this, -190, -90);
			addPyramid(this,-80, -450);
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
	
	// This level=11 requires using the pass through objects ruby.
	TEACH_RUBY(
		"Get the Sphinx's red ruby of passage to retrieve the ankh inside the pyramid.", R.drawable.ruby) {
		{
			addPyramid(this, -220, 100);
			addPyramid(this, 655, 110);
			addPyramid(this, -620, 100);
			addSpell(this, -220, 100);
			addRuby(this, -62, 438);
			addCoin(this, 203, -256);
			addCoin(this, 206, 234);
			addSphynx(this, -197, 441);			
		}
	},	
	
	// This level=12, It has a shield inside a pyramid, requiring a Ruby to get.
	SECOND_LAST(
		"With each ankh recovered, Nebtawi's power wanes. Hurry now!", R.drawable.ruby) {
		{
			addPyramid(this, -156, 416);
			addPyramid(this, 750, -312);
			addPyramid(this, -620, 100);
			addProtection(this, -625, 100);
			addSpell(this, 78, -459);
			addRuby(this, -450, -441);
			addHazard(this, 78, -409);
			addHazard(this, 41, -428);
			addHazard(this, 116, -428);
			addHazard(this, 44, -484);
			addCoin(this, 894, 900);
			addCoin(this, 200, 210);
			addCoin(this, -919, 888);
			addCoin(this, -903, -919);
			addCoin(this, 909, -928);
			addHazard(this, 75, -497);
			addHazard(this, 113, -481);
			addSphynx(this, -600, -569);
		}
	},	

	LAST("Collect the remaining ankhs and Nebtawi will be defeated in Egypt! Magic scarabs have arrived to give you time.", R.drawable.scarab) {
		{
			setTotalTimeAllowedInSeconds(100);
			addSphynx(this, -16, -497);
			addPyramid(this, -534, 256);
			addPyramid(this, -278, 438);
			addPyramid(this, -312, 241);
			addSpell(this, -716, -641);
			addSpell(this, -841, -806);
			addSpell(this, 709, -697);
			addSpell(this, 825, -841);
			addSpell(this, -687, 669);
			addSpell(this, -825, 816);
			addSpell(this, 663, 628);
			addSpell(this, 781, 806);
			addTime(this, -16, -597);
			addTime(this, 78, -747);
			addHazard(this, -719, 803);
			addHazard(this, 719, 713);
			addHazard(this, 816, -737);
			addHazard(this, -753, -806);
			addProtection(this, -419, 369);
			addHazard(this, -700, 828);
			addHazard(this, -750, 759);
			addHazard(this, -781, 728);
			addHazard(this, -834, 675);
			addHazard(this, -809, 700);
			addTime(this, -91, -741);
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
	
	int numberOfCoins = 0;
	
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
	
	public int getNumberOfCoins() {
		return numberOfCoins;
	}
}