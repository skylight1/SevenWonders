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

final class SpellCollisionAction implements CollisionAction {
	
	private int glowIndex;
	
	public SpellCollisionAction(final int aGlowIndex) {
		glowIndex = aGlowIndex;
	}

	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		// play a sound
		SoundTracks.getInstance().play(SoundTracks.SPELL);
		
		aSevenWondersGLRenderer.hideGlowAt(glowIndex);

		// notify the message handler of the new score
		aUiHandler.sendEmptyMessage(PlayActivity.SPELL_COLLECTED_MESSAGE);
		GameMessagesDisplay.postMessage(GameEvent.ANHK_COLLECTED);
	}
}