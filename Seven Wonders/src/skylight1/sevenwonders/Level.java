package skylight1.sevenwonders;

public enum Level {
	FIRST(5, 3);

	private int numberOfSpells;
	
	private int numberOfSpellsRequired;

	private Level(int aNumberOfSpells, int aPercentOfSpellsRequired) {
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
}
