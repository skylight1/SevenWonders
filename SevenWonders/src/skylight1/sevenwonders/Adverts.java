package skylight1.sevenwonders;

import skylight1.util.Assets;
import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.adwhirl.AdWhirlLayout;

public class Adverts {
	public static void insertAdBanner(Context context, ViewGroup layout) {
       
	    String adwhirl_id = Assets.getString("adwhirl_id",context);
	    if(adwhirl_id!=null && adwhirl_id.length()>0) {
//TODO: reflect! - and remove line following
	    	AdWhirlLayout adWhirlLayout = new AdWhirlLayout((Activity) context, adwhirl_id);
//	    	RelativeLayout adWhirlLayout = new RelativeLayout(context); 	
	    	RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(320, 52);
	    	layout.addView(adWhirlLayout, adWhirlLayoutParams);
	    }
    }
}