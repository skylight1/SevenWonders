package skylight1.sevenwonders.social.twitter;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthException;
import twitter4j.http.AccessToken;
import android.net.Uri;

/**
 * Requests authorization to update a user's Twitter account.
 * 
 * @author Lance Nanek
 *
 */
public class AuthRequest {

	private OAuthProvider provider;
	
	private OAuthConsumer consumer;

	private String callbackUrl;

	public AuthRequest(String consumerKey, String consumerSecret, String callbackUrl) {
		
		this.callbackUrl = callbackUrl;

		consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);

		provider = new CommonsHttpOAuthProvider(
				"https://api.twitter.com/oauth/request_token",
				"https://api.twitter.com/oauth/access_token",
				"https://api.twitter.com/oauth/authorize");
	}
	
	public String getAuthorizationUrl() throws OAuthException {
		String authUrl = provider.retrieveRequestToken(consumer, callbackUrl);
		return authUrl;
	}
	
	public AccessToken getAccessToken(String url) throws OAuthException {
		if ( null == url ) {
			return null;
		}
		
		Uri uri = Uri.parse(url);
		if ( ! uri.toString().startsWith(callbackUrl) ) {
			return null;
		}
		
		String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
		
		//Sets token and tokenSecret in consumer.
		provider.retrieveAccessToken(consumer, verifier);
			
		AccessToken a = new AccessToken(consumer.getToken(), consumer.getTokenSecret());
		return a;
	}

}
