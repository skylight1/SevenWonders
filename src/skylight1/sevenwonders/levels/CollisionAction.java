package skylight1.sevenwonders.levels;

import skylight1.sevenwonders.view.SevenWondersGLRenderer;
import android.os.Handler;

public interface CollisionAction {
	void collision(Handler aUiHandler, SevenWondersGLRenderer aSevenWondersGLRenderer);
}
