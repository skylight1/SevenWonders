package skylight1.sevenwonders.social.facebook;

import android.content.Context;
import android.widget.Toast;

public class FacebookUtil {
	
	public static void showFacebookErrorToast(Context context) {
		Toast toast = Toast.makeText(context, 
				"Sorry, there was an error communicating with Facebook.", 
				Toast.LENGTH_LONG);
		toast.show();		
	}

}
