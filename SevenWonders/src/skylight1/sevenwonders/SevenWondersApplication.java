package skylight1.sevenwonders;

import android.app.Application;

public class SevenWondersApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// make sure the audio stops if the application ends
		Thread.setDefaultUncaughtExceptionHandler(new SoundTracksStoppingExceptionHandler());
	}
}
