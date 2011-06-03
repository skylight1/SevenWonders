/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

final class EmeraldCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// TODO add a different sound here
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		// TODO have a level with a story title that explains this. 
		// e.g. "Find the magic green ruby for safety from swords!"
		// TODO some sort of UI indicator as well. emerald icon on screen, dialog message, swords disappear, etc.
		// would making the item be a shield be more obvious?
		aSevenWondersGLRenderer.getGameState().isPlayerInvincible = true;
	}
}