package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.view.GameMessagesDisplay;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import skylight1.sevenwonders.view.GameMessagesDisplay.GameEvent;
import android.os.Handler;

public class HazardCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// Return early without doing anything if the player is invincible.
		if ( aSevenWondersGLRenderer.getGameState().isPlayerInvincible() ) {
			GameMessagesDisplay.postMessage(GameEvent.NOT_HARMED);
			return;
		}
		
		// Otherwise, the player died.
		GameMessagesDisplay.postMessage(GameEvent.HIT_BY_SWORD);
		GameMessagesDisplay.postMessage(GameEvent.AAARGH);
		aUiHandler.sendEmptyMessage(PlayActivity.START_END_GAME_MESSAGE);
	}
}