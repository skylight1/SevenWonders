/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;
import android.os.Message;

final class CoinCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// TODO add a different sound here
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		aSevenWondersGLRenderer.getGameState().incrementNumberofCoinsCollected();
	}
}