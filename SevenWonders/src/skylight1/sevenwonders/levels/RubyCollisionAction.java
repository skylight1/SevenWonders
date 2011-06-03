/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

final class RubyCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// TODO add a different sound here
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		// TODO have a level with a story title that explains this. 
		// e.g. "An ankh is hidden within a pyramid! Find the magic red ruby to fly through its walls!"
		// TODO some sort of UI indicator as well. ruby icon on screen, dialog message, etc.
		aSevenWondersGLRenderer.getGameState().isPlayerAbleToFlyThroughObstacles = true;
	}
}