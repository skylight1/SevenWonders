package skylight1.sevenwonders;

import skylight1.sevenwonders.view.Position;

public class GameState {

	public static final float HEIGHT_OF_CARPET_FROM_GROUND = 12f;

	public int numberOfSpellsCollected;
		
	public Position playerWorldPosition = new Position(0, 0, 0);

	public float playerFacing;
	
	public float turningVelocity;

	public float velocity;
	
	private int remainingInvincibilityTimeMillis;
	
	private int remainingPassThroughObstaclesTimeMillis;
	
	private int numberofCoinsCollected;

	private long remainingGameTimeMillis;
	
	public int getRemainingPassThroughSolidsTimeMillis() {
		return remainingPassThroughObstaclesTimeMillis;
	}

	public void setRemainingPassThroughObstaclesTimeMillis(int aRemainingPassThroughSolidsTimeMillis) {
		remainingPassThroughObstaclesTimeMillis = aRemainingPassThroughSolidsTimeMillis;
	}
	
	public boolean isPlayerAbleToFlyThroughObstacles() {
		return remainingPassThroughObstaclesTimeMillis > 0;
	}
	
	public boolean isPlayerInvincible() {
		return remainingInvincibilityTimeMillis > 0;
	}
	
	public int getRemainingInvincibilityTimeMillis() {
		return remainingInvincibilityTimeMillis;
	}

	public void setRemainingInvincibilityTimeMillis(int aRemainingInvincibilityTimeMillis) {
		remainingInvincibilityTimeMillis = aRemainingInvincibilityTimeMillis;
	}

	public void setRemainingGameTimeMillis(long aRemainingGameTimeMillis) {
		remainingGameTimeMillis = aRemainingGameTimeMillis;
	}

	public synchronized void incrementNumberofCoinsCollected() {
		numberofCoinsCollected++;
	}

	public synchronized int getNumberofCoinsCollected() {
		return numberofCoinsCollected;
	}

	public void reduceRemainingTimeMillis(final long aChangeInTime) {
		remainingGameTimeMillis -= aChangeInTime;
		remainingInvincibilityTimeMillis -= aChangeInTime;
		remainingPassThroughObstaclesTimeMillis -= aChangeInTime;
	}

	public long getRemainingGameTimeMillis() {
		return remainingGameTimeMillis;
	}
}
