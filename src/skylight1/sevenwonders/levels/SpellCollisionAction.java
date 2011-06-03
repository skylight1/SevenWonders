/**
 * 
 */
package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

final class SpellCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// play a sound
		SoundTracks.getInstance().play(SoundTracks.SPELL);

		// notify the message handler of the new score
		aUiHandler.sendEmptyMessage(PlayActivity.SPELL_COLLECTED_MESSAGE);
	}
}