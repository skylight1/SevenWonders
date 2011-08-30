package skylight1.sevenwonders;

import skylight1.util.BuildInfo;
import android.app.Application;

public class SevenWondersApplication extends Application {
	public static final boolean isDebug = false; //TODO: make false when publishing !
	@Override
	public void onCreate() {
		super.onCreate();
		if (isDebug) {
			if(!BuildInfo.isDebuggable(this)) {
				throw new Error("******** SET SevenWondersApplication.isDebug = false ! ********");
			}
		}
		// make sure the audio stops if the application ends
		Thread.setDefaultUncaughtExceptionHandler(new SoundTracksStoppingExceptionHandler());
	}
}
