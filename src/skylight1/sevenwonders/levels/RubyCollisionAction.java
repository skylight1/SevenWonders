/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;
import android.os.Message;

final class RubyCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// TODO add a cool sound here

		// notify the message handler that the time has been increased
		final Message message = aUiHandler.obtainMessage(PlayActivity.MODIFY_REMAINING_TIME_MESSAGE, 30, 0);
		aUiHandler.sendMessage(message);
	}
}