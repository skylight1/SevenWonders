/**
 * 
 */
package skylight1.sevenwonders.view;

import skylight1.opengl.CollisionDetector;
import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import android.os.Handler;
import android.os.Message;

final class SpellCollisionHandler extends AbstractCollectableCollisionHandler {
	public SpellCollisionHandler(CollisionDetector aCollisionDetector, Handler aUiHandler,
			SevenWondersGLRenderer aRenderer) {
		super(aUiHandler, aRenderer);
	}

	@Override
	protected void performCustomCollisionOccurredAction() {
		// play a sound
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		// add one to the score for colliding with a spell
		final int newScore = renderer.incrementScore(1);

		// notify the message handler of the new score
		final Message message = uiHandler.obtainMessage(PlayActivity.NEW_SCORE_MESSAGE, newScore, 0);
		uiHandler.sendMessage(message);
	}
}