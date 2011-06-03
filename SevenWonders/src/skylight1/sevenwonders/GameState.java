package skylight1.sevenwonders;

public class GameState {
	
	// Currently only access by main UI thread, so doesn't need synchronization.
	public int numberOfSpellsCollected;
	
	// Currently only accessed by render thread, so doesn't need synchronization.
	public boolean isPlayerAbleToFlyThroughObstacles;
	public boolean isPlayerInvincible;
	
	// Accessed by render thread, will need to be read by main thread for figuring out score later, so synchronized.
	private int numberofCoinsCollected;

	public synchronized void incrementNumberofCoinsCollected() {
		numberofCoinsCollected++;
	}

	public synchronized int getNumberofCoinsCollected() {
		return numberofCoinsCollected;
	}
	
}
