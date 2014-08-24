/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.GameMessagesDisplay;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import skylight1.sevenwonders.view.GameMessagesDisplay.GameEvent;
import android.os.Handler;

final class CoinCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		SoundTracks.getInstance().play(SoundTracks.COIN);
		GameMessagesDisplay.postMessage(GameEvent.COIN_COLLECTED);
		aSevenWondersGLRenderer.getGameState().incrementNumberofCoinsCollected();
	}
}