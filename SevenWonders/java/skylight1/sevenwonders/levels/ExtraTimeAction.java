/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.GameMessagesDisplay;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import skylight1.sevenwonders.view.GameMessagesDisplay.GameEvent;
import android.os.Handler;
import android.os.Message;

final class ExtraTimeAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// TODO add a different sound here
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		// TODO maybe make an hour glass item for this instead? and coin could be just points?
		// notify the message handler that the time has been increased
		final Message message = aUiHandler.obtainMessage(PlayActivity.MODIFY_REMAINING_TIME_MESSAGE, 60, 0);
		aUiHandler.sendMessage(message);
		GameMessagesDisplay.postMessage(GameEvent.TIME_BONUS_FOUND);
	}
}