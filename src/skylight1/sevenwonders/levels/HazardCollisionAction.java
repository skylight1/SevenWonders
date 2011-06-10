package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

public class HazardCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// Return early without doing anything if the player is invincible.
		if ( aSevenWondersGLRenderer.getGameState().isPlayerInvincible() ) {
			return;
		}
		
		// Otherwise, the player died.
		aUiHandler.sendEmptyMessage(PlayActivity.START_END_GAME_MESSAGE);
	}
}