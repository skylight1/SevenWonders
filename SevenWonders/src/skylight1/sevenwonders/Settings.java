package skylight1.sevenwonders;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	
	public static final String KEY_IS_SOUND_ENABLED = "KEY_IS_SOUND_ENABLED";
	
	private static final String KEY_GAME_WAS_STARTED_AT_LEAST_ONCE = "KEY_GAME_WAS_STARTED_AT_LEAST_ONCE";

	public static final String PREFS_NAME = "SevenWondersPrefs";
	
	private final SharedPreferences prefs;

	public Settings(Context context) {
		prefs = context.getSharedPreferences(PREFS_NAME, 0);
		
	}

	public boolean isSoundEnabled() {
		return prefs.getBoolean(KEY_IS_SOUND_ENABLED, false);
	}

	public void setSoundEnabled(boolean isSoundEnabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_SOUND_ENABLED, isSoundEnabled);
        editor.commit();		
	}

	public void setGameWasStartedAtLeastOnceFlag() {
        if (!prefs.contains(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE) ||
        		!prefs.getBoolean(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE, false)) { 
	        SharedPreferences.Editor editor = prefs.edit();
	        editor.putBoolean(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE, true);
	        editor.commit();
        }		
	}
	
	public boolean wasGameStartedAtLeastOnce() {
		return prefs.getBoolean(KEY_GAME_WAS_STARTED_AT_LEAST_ONCE, false);
	}

}
