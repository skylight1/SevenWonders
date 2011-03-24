package skylight1.sevenwonders.social;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class NoNPEWebView extends WebView {
	public NoNPEWebView(Context context) {
		super(context);
	}

	public NoNPEWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoNPEWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        try {
            super.onWindowFocusChanged(hasWindowFocus);
        } catch(NullPointerException e) {
        	//ALog.e(e, "Eating exception thrown by WebView#onWindowFocusChanged to prevent crash.");
        }
    }
}
