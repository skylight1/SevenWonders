/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

final class CoinCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		SoundTracks.getInstance().play(SoundTracks.COIN);

		aSevenWondersGLRenderer.getGameState().incrementNumberofCoinsCollected();
	}
}