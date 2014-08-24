package skylight1.sevenwonders.social.twitter;

//import twitter4j.http.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Stores previously obtained authorization to update a user's Twitter status.
 * 
 * @author Lance Nanek
 *
 */
public class AuthStore {
    
    private static final String OATH_TOKEN_PREF_KEY = 
    	AuthStore.class.getName() + ".OATH_TOKEN_PREF_KEY";
    private static final String OATH_TOKEN_SECRET_PREF_KEY = 
    	AuthStore.class.getName() + ".OATH_TOKEN_SECRET_PREF_KEY";
    private static final String PREF_FILE_NAME = 
    	AuthStore.class.getName() + ".PREF_FILE_NAME";
    
    private Context context;
    
    public AuthStore(Context context) {
		this.context = context;
	}

	private SharedPreferences getPrefs() {
    	return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }
    
    public boolean save(String oathToken, String oathTokenSecret) {
        Editor editor = getPrefs().edit();
        editor.putString(OATH_TOKEN_PREF_KEY, oathToken);
        editor.putString(OATH_TOKEN_SECRET_PREF_KEY, oathTokenSecret);
        return editor.commit();
    }
/*
    public AccessToken restore() {
        SharedPreferences savedSession = getPrefs();
        String oathToken = savedSession.getString(OATH_TOKEN_PREF_KEY, null);
        String oathTokenSecret = savedSession.getString(OATH_TOKEN_SECRET_PREF_KEY, null);
        if ( null == oathToken || null == oathTokenSecret ) {
        	return null;
        }

		AccessToken a = new AccessToken(oathToken, oathTokenSecret);
        return a;
    }
*/
    public void clear() {
        Editor editor = getPrefs().edit();
        editor.clear();
        editor.commit();
    }
}
