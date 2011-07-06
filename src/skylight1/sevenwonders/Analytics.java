package skylight1.sevenwonders;

import skylight1.util.Assets;
import skylight1.util.BuildInfo;
import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Analytics {

//TODO: REFLECT!
private GoogleAnalyticsTracker tracker;
	private String ga_id;
	
	public Analytics(Context context) {
		ga_id = Assets.getString("ga_id",context);
//TODO: REFLECT!
        tracker = GoogleAnalyticsTracker.getInstance();
        tracker.setProductVersion("7W", BuildInfo.getVersionName(context));
	}

	private boolean isValid() {
        if(ga_id.length()>0) {
        	return true;
        }
        return false;
	}
	
	public static Analytics getInstance(Context context, String name, String version) {
        Analytics analytics = new Analytics(context);
        if(!analytics.isValid()) {
    	    return null;
        }
        return analytics;
    }

	public void start(Context context) {
//TODO: REFLECT! and note: start tracker can be started with a dispatch interval (in seconds) so add that method!
        tracker.start(ga_id, context);
    }

	public void trackEvent(String string, String string2, String hashedPhoneId, int i) {
// TODO: REFLECT!
	    tracker.trackEvent(string, string2, hashedPhoneId, i);
    }

	public void trackPageView(String string) {
// TODO: REFLECT!
	    tracker.trackPageView(string);
    }

	public void dispatch() {
// TODO: REFLECT!
	    tracker.dispatch();
    }

	public void stop() {
// TODO: REFLECT!
	    tracker.stop();
    }
}
