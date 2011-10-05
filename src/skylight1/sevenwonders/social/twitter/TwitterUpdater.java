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
import android.widget.Toast;


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
	
	private static final int RETRIEVED_REQUEST_TOKEN_FAILED_MSG_ID = 4;
	
	private static final int RETRIEVED_REQUEST_TOKEN_MSG_ID = 5;
	
	private AuthRequest mAuthSetup;
	
	private WebView mWebView;
	
	private String mConsumerKey;
	
	private String mConsumerSecret;
	
	private String mCallbackUrl;
	
	private String mStatus;
	
	//AsyncTwitter does callback on a thread other than the main UI thread. 
	//So a handler is needed to get back to the main thread.
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message aMessage) {
						
			switch ( aMessage.what ) {					
				case POST_STATUS_SUCCESS_MSG_ID:
					setResult(RESULT_OK);
					finish();
					break;
					
				case SAVED_AUTH_POST_STATUS_FAIL_MSG_ID:
					// Failed to tweet using previously saved authorization.
					
					//TODO distinguish between different errors
					//network errors we may want to retry, auth errors we may want to clear the saved authorization, etc..
					//session.clear();
					//setErrorAndFinish();

					// Get new authorization and try one more time.
					obtainAuthorization();	
					break;
					
				case NEW_AUTH_POST_STATUS_FAIL_MSG_ID:
					// Failed to Tweet using new credentials.
					
					Toast.makeText(TwitterUpdater.this, 
						"Unable to tweet at this time. Please try again later.", Toast.LENGTH_LONG).show();
					setErrorAndFinish();
					break;
					
				case RETRIEVED_REQUEST_TOKEN_MSG_ID:
					
					// Show the Twitter web page to authorize the app and approve the authorization request
					// in the WebView component.
					String authUrl = (String) aMessage.obj;
					addWebView(authUrl);
					break;
					
				case RETRIEVED_REQUEST_TOKEN_FAILED_MSG_ID:

					Exception e = (Exception) aMessage.obj;
					Log.e(LOG_TAG, "Exception authorizing and posting to Twitter: ", e);
					Toast.makeText(TwitterUpdater.this, e.getMessage(), Toast.LENGTH_LONG).show();
					
					setErrorAndFinish();
					break;
			}
		}
		
	};

	public static Intent getIntent(final Context aContext, final String aConsumerKey, final String aConsumerSecret, 
			final String aCallbackUrl, final String aStatus) {
		Intent intent = new Intent(aContext, TwitterUpdater.class);
		intent.putExtra(CONSUMER_KEY_EXTRA_KEY, aConsumerKey);
		intent.putExtra(CONSUMER_SECRET_EXTRA_KEY, aConsumerSecret);
		intent.putExtra(CALLBACK_URL_EXTRA_KEY, aCallbackUrl);
		intent.putExtra(STATUS_EXTRA_KEY, aStatus);
		return intent;
	}

	private void parseIntent() {
		Intent intent = getIntent();
		mConsumerKey = intent.getStringExtra(CONSUMER_KEY_EXTRA_KEY);
		mConsumerSecret = intent.getStringExtra(CONSUMER_SECRET_EXTRA_KEY);
		mCallbackUrl = intent.getStringExtra(CALLBACK_URL_EXTRA_KEY);
		mStatus = intent.getStringExtra(STATUS_EXTRA_KEY);
	}
	
	@Override
	public void onCreate(final Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
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
		
	/**
	 * Handle Twitter events that occur when posting a status using saved authorization information.
	 */
	private class SavedAuthPostStatus extends TwitterAdapter {
		@Override
		public void onException(final TwitterException aException, final TwitterMethod aMethod) {
			//ALog.m(e, method);
			Message message = mHandler.obtainMessage(SAVED_AUTH_POST_STATUS_FAIL_MSG_ID, aException);
			mHandler.sendMessage(message);
		}
		@Override
		public void updatedStatus(final Status aStatus) {
			//ALog.m(statuses);
			mHandler.sendEmptyMessage(POST_STATUS_SUCCESS_MSG_ID);			
		}		
	}

	private void postStatus(final AccessToken aAccessToken, final TwitterListener aListener) {
		//ALog.m(a);
		AsyncTwitter twitter = new AsyncTwitterFactory(aListener)
			.getOAuthAuthorizedInstance(mConsumerKey, mConsumerSecret, aAccessToken);		
		twitter.updateStatus(mStatus);
	}
	
	/**
	 * Shows the Twitter page that requests authorization for the app from the user in a WebView.
	 */
	private void obtainAuthorization() {

		mAuthSetup = new AuthRequest(mConsumerKey, mConsumerSecret, mCallbackUrl);

		new Thread() {
			@Override
			public void run() {
				try {
					String authUrl = mAuthSetup.getAuthorizationUrl();
					Message message = mHandler.obtainMessage(RETRIEVED_REQUEST_TOKEN_MSG_ID, authUrl);
					mHandler.sendMessage(message);
				} catch (Exception e) {
					Message message = mHandler.obtainMessage(RETRIEVED_REQUEST_TOKEN_FAILED_MSG_ID, e);
					mHandler.sendMessage(message);
				}
			}
		}.start();

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
	private boolean completeIfCallback(final String aUrlToCheck) {
		Log.d(LOG_TAG, "completeIfCallback(" + aUrlToCheck + ")");
		
		if ( null == aUrlToCheck || !aUrlToCheck.startsWith(mCallbackUrl)  ) {
			return false;
		}

		Log.d(LOG_TAG, "Callback URL detected, extracting authorization...");

		try {
			AccessToken a = mAuthSetup.getAccessToken(aUrlToCheck);

			AuthStore session = new AuthStore(this);
			session.save(a.getToken(), a.getTokenSecret());

			postStatus(a, new NewAuthPostStatus());			
			finish();

		} catch (final Exception e) {
			Message message = mHandler.obtainMessage(NEW_AUTH_POST_STATUS_FAIL_MSG_ID, e);
			mHandler.sendMessage(message);
		}
		return true;
	}

	/**
	 * Handle Twitter events that occur when posting a status using new authorization information.
	 */
	private class NewAuthPostStatus extends TwitterAdapter {
		@Override
		public void onException(final TwitterException aException, final TwitterMethod aMethod) {
			Message message = mHandler.obtainMessage(NEW_AUTH_POST_STATUS_FAIL_MSG_ID, aException);
			mHandler.sendMessage(message);
		}
		@Override
		public void updatedStatus(final Status aStatus) {
			mHandler.sendEmptyMessage(POST_STATUS_SUCCESS_MSG_ID);	
		}		
	}

	private void addWebView(final String aAuthUrl) {

		mWebView = new NoNPEWebView(this);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		setContentView(mWebView, params);
		
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onCloseWindow(final WebView aWindow) {				
				TwitterUpdater.this.finish();
			}	
		});
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(final WebView aView, final String aUrl) {
				Log.d(LOG_TAG, "shouldOverrideUrlLoading(" + aUrl + ")");
				
				if ( !completeIfCallback(aUrl) ) {
					aView.loadUrl(aUrl);
				}
				return true;
			}
			
			@Override
			public void onReceivedError(final WebView aView, final int aErrorCode, final String aDescription, final String aFailingUrl) {
				setErrorAndFinish();
			}
		});
		
		mWebView.loadUrl(aAuthUrl);
	}

}

