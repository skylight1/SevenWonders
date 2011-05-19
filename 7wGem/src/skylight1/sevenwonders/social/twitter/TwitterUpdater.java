package skylight1.sevenwonders.social.twitter;

import skylight1.sevenwonders.social.NoNPEWebView;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Posts a status message to Twitter using the Twitter4J library for Twitter and SignPost for OAuth.
 * 
 * @author Lance Nanek
 *
 */
public class TwitterUpdater extends Activity {

	public static final int RESULT_ERROR = Activity.RESULT_FIRST_USER;
	
	private static final String LOG_TAG = TwitterUpdater.class.getSimpleName();
	
	private static String CONSUMER_KEY_EXTRA_KEY = TwitterUpdater.class.getName() + ".CONSUMER_KEY_EXTRA_KEY";
	
	private static String CONSUMER_SECRET_EXTRA_KEY = TwitterUpdater.class.getName() + ".CONSUMER_SECRET_EXTRA_KEY";
	
	private static String CALLBACK_URL_EXTRA_KEY = TwitterUpdater.class.getName() + ".CALLBACK_URL_EXTRA_KEY";
	
	private static String STATUS_EXTRA_KEY = TwitterUpdater.class.getName() + ".STATUS_EXTRA_KEY";
	
	private static final int POST_STATUS_SUCCESS_MSG_ID = 1;
	
	private static final int SAVED_AUTH_POST_STATUS_FAIL_MSG_ID = 2;
	
	private static final int NEW_AUTH_POST_STATUS_FAIL_MSG_ID = 3;
	
	private AuthRequest mAuthSetup;
	
	private WebView mWebView;
	
	private String mConsumerKey;
	
	private String mConsumerSecret;
	
	private String mCallbackUrl;
	
	private String mStatus;
	
	//Unfortunately, AsyncTwitter does callback on a thread other than the main UI thread. 
	//So a handler is needed to get back to the main thread.
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//ALog.m(msg);
						
			switch ( msg.what ) {					
				case POST_STATUS_SUCCESS_MSG_ID:
					setResult(RESULT_OK);
					finish();
					break;
					
				case SAVED_AUTH_POST_STATUS_FAIL_MSG_ID:
					String message = "Failed to tweet using previously saved authorization.";
					//ALog.e((Throwable) msg.obj, message);
					
					//TODO distinguish between different errors
					//network errors we may want to retry, auth errors we may want to clear the saved authorization, etc..
					//session.clear();
					//setErrorAndFinish();
					
					obtainAuthorization();	
					break;
					
				case NEW_AUTH_POST_STATUS_FAIL_MSG_ID:
					//ALog.e((Throwable) msg.obj, "Failed to Tweet using new credentials.");
					setErrorAndFinish();
					break;
			}
		}
		
	};

	public static Intent getIntent(Context context, String consumerKey, String consumerSecret, String callbackUrl, String status) {
		//ALog.m(context, consumerKey, consumerSecret, callbackUrl, status);
		
		Intent intent = new Intent(context, TwitterUpdater.class);
		intent.putExtra(CONSUMER_KEY_EXTRA_KEY, consumerKey);
		intent.putExtra(CONSUMER_SECRET_EXTRA_KEY, consumerSecret);
		intent.putExtra(CALLBACK_URL_EXTRA_KEY, callbackUrl);
		intent.putExtra(STATUS_EXTRA_KEY, status);
		return intent;
	}

	private void parseIntent() {
		//ALog.m();
		
		Intent intent = getIntent();
		mConsumerKey = intent.getStringExtra(CONSUMER_KEY_EXTRA_KEY);
		mConsumerSecret = intent.getStringExtra(CONSUMER_SECRET_EXTRA_KEY);
		mCallbackUrl = intent.getStringExtra(CALLBACK_URL_EXTRA_KEY);
		mStatus = intent.getStringExtra(STATUS_EXTRA_KEY);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//ALog.m(savedInstanceState);

		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		parseIntent();

		//First try to post with any previous obtained authorization.
		AuthStore session = new AuthStore(this);
		AccessToken token = session.restore();
		if ( null != token ) {
			try {
				postStatus(token, new SavedAuthPostStatus());
			} catch (Exception e) {
				Message message = mHandler.obtainMessage(SAVED_AUTH_POST_STATUS_FAIL_MSG_ID, e);
				mHandler.sendMessage(message);
			}
		} else {
			obtainAuthorization();		
		}
	}
		
	private class SavedAuthPostStatus extends TwitterAdapter {
		@Override
		public void onException(TwitterException e, TwitterMethod method) {
			//ALog.m(e, method);
			Message message = mHandler.obtainMessage(SAVED_AUTH_POST_STATUS_FAIL_MSG_ID, e);
			mHandler.sendMessage(message);
		}
		@Override
		public void updatedStatus(Status statuses) {
			//ALog.m(statuses);
			mHandler.sendEmptyMessage(POST_STATUS_SUCCESS_MSG_ID);			
		}		
	}

	private void postStatus(AccessToken a, TwitterListener listener) {
		//ALog.m(a);
		AsyncTwitter twitter = new AsyncTwitterFactory(listener)
			.getOAuthAuthorizedInstance(mConsumerKey, mConsumerSecret, a);		
		twitter.updateStatus(mStatus);
	}
	
	/**
	 * Shows the Twitter page that requests authorization for the app from the user in a WebView.
	 */
	private void obtainAuthorization() {
		//ALog.m();
		
		try {
			mAuthSetup = new AuthRequest(mConsumerKey, mConsumerSecret, mCallbackUrl);
			String authUrl = mAuthSetup.getAuthorizationUrl();
			addWebView(authUrl);		
			
		} catch (Exception e) {
			String message = "Error contacting Twitter to authorize app.";
			//ALog.e(message, e);
			setErrorAndFinish();
		}
	}

	@Override
	public void finish() {
		//ALog.m();
		
		if ( null != mWebView ) {
			mWebView.stopLoading();
		}
		super.finish();
	}

	private void setErrorAndFinish() {
		//ALog.m();
		setResult(RESULT_ERROR);
		finish();
	}
	
	/**
	 * Processes the callback URL encountered when the user authorizes the app.
	 */
	private boolean completeIfCallback(String url) {
		Log.d(LOG_TAG, "completeIfCallback(" + url + ")");
		
		if ( null == url || !url.startsWith(mCallbackUrl)  ) {
			return false;
		}

		Log.d(LOG_TAG, "Callback URL detected, extracting authorization...");

		try {
			AccessToken a = mAuthSetup.getAccessToken(url);

			AuthStore session = new AuthStore(this);
			session.save(a.getToken(), a.getTokenSecret());

			postStatus(a, new NewAuthPostStatus());			
			finish();

		} catch (Exception e) {
			Message message = mHandler.obtainMessage(NEW_AUTH_POST_STATUS_FAIL_MSG_ID, e);
			mHandler.sendMessage(message);
		}
		return true;
	}
	
	private class NewAuthPostStatus extends TwitterAdapter {
		@Override
		public void onException(TwitterException e, TwitterMethod method) {
			//ALog.m(e, method);
			Message message = mHandler.obtainMessage(NEW_AUTH_POST_STATUS_FAIL_MSG_ID, e);
			mHandler.sendMessage(message);
		}
		@Override
		public void updatedStatus(Status statuses) {
			//ALog.m(statuses);
			mHandler.sendEmptyMessage(POST_STATUS_SUCCESS_MSG_ID);	
		}		
	}

	private void addWebView(String authUrl) {
		//ALog.m(authUrl);

		mWebView = new NoNPEWebView(this);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		setContentView(mWebView, params);
		
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onCloseWindow(WebView window) {
				//ALog.m(window);
				
				TwitterUpdater.this.finish();
			}	
		});
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(LOG_TAG, "shouldOverrideUrlLoading(" + url + ")");
				
				if ( !completeIfCallback(url) ) {
					view.loadUrl(url);
				}
				return true;
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				//ALog.m(view, errorCode, description, failingUrl);
				
				setErrorAndFinish();
			}
		});
		
		mWebView.loadUrl(authUrl);
	}

}

