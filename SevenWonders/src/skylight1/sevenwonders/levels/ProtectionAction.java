/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.GameState;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

final class ProtectionAction implements CollisionAction {
	private static final int TIME_INVINCIBLE_MILLIS = 30000;

	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// TODO add a different sound here
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		final GameState gameState = aSevenWondersGLRenderer.getGameState();
		gameState.setRemainingInvincibilityTimeMillis(TIME_INVINCIBLE_MILLIS);
	}
}