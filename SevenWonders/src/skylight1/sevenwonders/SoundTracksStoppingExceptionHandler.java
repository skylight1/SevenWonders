package skylight1.sevenwonders;

import java.lang.Thread.UncaughtExceptionHandler;

import skylight1.sevenwonders.services.SoundTracks;
import android.util.Log;

/**
 * @author Rob
 * 
 *         For when we crash for unknown reasons. First use: turn off the sound
 */
public class SoundTracksStoppingExceptionHandler implements UncaughtExceptionHandler {

	private static final String TAG = SoundTracksStoppingExceptionHandler.class.getName();

	private UncaughtExceptionHandler defaultVersion;

	SoundTracksStoppingExceptionHandler() {
		defaultVersion = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(final Thread aThread, final Throwable aThrowable) {
		try {
			if (SoundTracks.getInstance() != null) {
				SoundTracks.getInstance().stop();
			}
		} catch (final Throwable anotherT) {
			Log.e(TAG, "'You can't stop the music' - Village People", anotherT);
		}
		if(aThrowable.getMessage()!=null && aThrowable.getMessage().indexOf("adwhirl")>0 ) {
			return;
		}
		defaultVersion.uncaughtException(aThread, aThrowable);
	}
}
