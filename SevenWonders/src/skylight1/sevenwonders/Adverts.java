package skylight1.sevenwonders;

import skylight1.util.Assets;
import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

//import com.adwhirl.AdWhirlLayout;
//import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
//import com.adwhirl.AdWhirlManager;
//import com.adwhirl.AdWhirlTargeting;

public class Adverts {
	
	public static void insertAdBanner(Context context, ViewGroup layout) {
       
	    String adwhirl_id = Assets.getString("adwhirl_id",context);
	    if(adwhirl_id!=null && adwhirl_id.length()>0) {
/*
    	   AdWhirlManager.setConfigExpireTimeout(1000 * 60 * 5);	    	   
    	   AdWhirlTargeting.setTestMode(SevenWondersApplication.isDebug);
    	   AdWhirlLayout adWhirlLayout = new AdWhirlLayout((Activity) context, adwhirl_id);
    	   RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(480, 75);
    	   layout.addView(adWhirlLayout, adWhirlLayoutParams);
    	   int diWidth = 480;
    	   int diHeight = 75;
    	   int density = (int) context.getResources().getDisplayMetrics().density;    	 
    	   adWhirlLayout.setAdWhirlInterface((AdWhirlInterface) context);
    	   adWhirlLayout.setMaxWidth((int)(diWidth * density));
    	   adWhirlLayout.setMaxHeight((int)(diHeight * density));
*/
    	   layout.invalidate();	    
	    }
    }
}