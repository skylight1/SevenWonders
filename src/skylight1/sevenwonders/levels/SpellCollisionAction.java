/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;
import android.os.Message;

final class SpellCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// play a sound
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		// TODO update score via a message, not coupling with the score
		// add one to the score for colliding with a spell
		final int newScore = aSevenWondersGLRenderer.incrementScore(1);

		// notify the message handler of the new score
		final Message message = aUiHandler.obtainMessage(PlayActivity.NEW_SCORE_MESSAGE, newScore, 0);
		aUiHandler.sendMessage(message);
	}
}