package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

public class HazardCollisionAction implements CollisionAction {
	@Override
	public void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer) {
		SoundTracks.getInstance().play(SoundTracks.DEATH);
		aUiHandler.sendEmptyMessage(PlayActivity.START_END_GAME_MESSAGE);
	}
}