package skylight1.sevenwonders;

public class GameState {
	private long remainingGameTimeMillis;
	
	public int numberOfSpellsCollected;
	private int remainingInvincibilityTimeMillis;
	
	private int remainingPassThroughObstaclesTimeMillis;
	
	private int numberofCoinsCollected;
	
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
