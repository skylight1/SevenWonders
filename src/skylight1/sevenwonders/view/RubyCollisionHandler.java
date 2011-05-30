/**
 * 
 */
package skylight1.sevenwonders.view;

import skylight1.opengl.CollisionDetector;
import skylight1.sevenwonders.PlayActivity;
import android.os.Handler;
import android.os.Message;

final class RubyCollisionHandler extends AbstractCollectableCollisionHandler {
	public RubyCollisionHandler(CollisionDetector aCollisionDetector, Handler aUiHandler,
			SevenWondersGLRenderer aRenderer) {
		super(aUiHandler, aRenderer);
	}

	@Override
	protected void performCustomCollisionOccurredAction() {
		// TODO add a cool sound here
		
		// notify the message handler that the time has been increased 
		final Message message = uiHandler.obtainMessage(PlayActivity.MODIFY_REMAINING_TIME_MESSAGE, 30, 0);
		uiHandler.sendMessage(message);
	}
}