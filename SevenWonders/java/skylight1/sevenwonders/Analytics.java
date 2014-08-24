package skylight1.sevenwonders;

import skylight1.util.Assets;
import skylight1.util.BuildInfo;
import android.content.Context;

//import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Analytics {

//TODO: REFLECT!
//	private GoogleAnalyticsTracker tracker;
	private String ga_id;
	
	public Analytics(Context context) {
		ga_id = Assets.getString("ga_id",context);
//TODO: REFLECT!
        if(ga_id.length()>0) {
//	        tracker = GoogleAnalyticsTracker.getInstance();
//	        tracker.setProductVersion("7W", BuildInfo.getVersionName(context));
	        // using custom variable (index[1-5],name,value,scope{1:visitor;2:session;3:page})
//	        tracker.setCustomVar(1, "7W", BuildInfo.getVersionName(context), 2);
        }
	}
	
	public static Analytics getInstance(Context context, String name, String version) {		
        Analytics analytics = new Analytics(context);
        return analytics;
    }

	public void start(Context context) {
//TODO: REFLECT! and note: start tracker can be started with a dispatch interval (in seconds) so add that method!
//		if(tracker!=null) {
//			tracker.startNewSession(ga_id, context);
//	        tracker.setCustomVar(1, "7W", BuildInfo.getVersionName(context), 2);
//		}
    }

	public void trackEvent(String string, String string2, String hashedPhoneId, int i) {
// TODO: REFLECT!
//		if(tracker!=null) {
//			tracker.trackEvent(string, string2, hashedPhoneId, i);
//		}
    }

	public void trackPageView(String string) {
// TODO: REFLECT!
//		if(tracker!=null) {
//			tracker.trackPageView(string);
//		}
    }

	public void dispatch() {
// TODO: REFLECT!
//		if(tracker!=null) {
//			tracker.dispatch();
//		}
    }

	public void stop() {
// TODO: REFLECT!
//		if(tracker!=null) {
//			tracker.stopSession();
//		}
	}
}
