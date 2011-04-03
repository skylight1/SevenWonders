package skylight1.sevenwonders;

import java.lang.Thread.UncaughtExceptionHandler;

import skylight1.sevenwonders.services.SoundTracks;

/**
 * @author Rob
 *
 * For when we crash for unknown reasons. First use: turn off the sound
 */
public class SWExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler defaultVersion;
	private static SWExceptionHandler sweh = null; // singleton

	private SWExceptionHandler() {
		defaultVersion = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public static void start() {
		if(sweh == null) // I don't think we really have to worry about multiple concurrent callers
			sweh = new SWExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if(SoundTracks.getInstance() != null) {
			SoundTracks.getInstance().stop();
		}
		defaultVersion.uncaughtException(t, e);
	}

}
