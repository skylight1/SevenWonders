package skylight1.sevenwonders;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	public static final String KEY_IS_SOUND_ENABLED = "KEY_IS_SOUND_ENABLED";
	public static final String KEY_IS_DEBUG_ENABLED = "KEY_IS_DEBUG_ENABLED";

	private static final String KEY_GAME_WAS_STARTED_AT_LEAST_ONCE = "KEY_GAME_WAS_STARTED_AT_LEAST_ONCE";

	public static final String PREFS_NAME = "SevenWondersPrefs";

	private static final String KEY_LEVEL_LOCKED = "LevelLocked.chapter.1.level.%d";

	private static final String KEY_LEVEL_HIGH_SCORE = "LevelHighScore.chapter.1.level.%d";

	private final SharedPreferences prefs;

	public Settings(Context context) {
		prefs = context.getSharedPreferences(PREFS_NAME, 0);

	}

	public boolean isSoundEnabled() {
		return prefs.getBoolean(KEY_IS_SOUND_ENABLED, true);
	}

	public void setSoundEnabled(boolean isSoundEnabled) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KEY_IS_SOUND_ENABLED, isSoundEnabled);
		editor.commit();
	}

	public void setGameWasStartedAtLeastOnceFlag() {
		if (!prefs.contains(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE)
				|| !prefs.getBoolean(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE, false)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE, true);
			editor.commit();
		}
	}

	public boolean wasGameStartedAtLeastOnce() {
		return prefs.getBoolean(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE, false);
	}

	public boolean isLevelLocked(final int aLevelNumber) {
		if (aLevelNumber == 1) {
			return false;
		}
		
		if(SevenWondersApplication.isDebug && isDebugEnabled()) {
			return false;
		}
		
		return prefs.getBoolean(String.format(KEY_LEVEL_LOCKED, aLevelNumber), true);
	}

	public void unlockLevel(final int aLevelNumber) {
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(String.format(KEY_LEVEL_LOCKED, aLevelNumber), false);
		editor.commit();
	}
	public void lockLevel(final int aLevelNumber) {
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(String.format(KEY_LEVEL_LOCKED, aLevelNumber), true);
		editor.commit();
	}

	public int getHighScore(final int aLevelNumber) {
		return prefs.getInt(String.format(KEY_LEVEL_HIGH_SCORE, aLevelNumber), 0);
	}

	public void setHighScore(final int aLevelNumber, final int aHighScore) {
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(String.format(KEY_LEVEL_HIGH_SCORE, aLevelNumber), aHighScore);
		editor.commit();
	}

	public void setDebugEnabled(boolean isDebugEnabled) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KEY_IS_DEBUG_ENABLED, isDebugEnabled);
		editor.commit();
	}
	public boolean isDebugEnabled() {
		return prefs.getBoolean(KEY_IS_DEBUG_ENABLED, false);
	}
}
