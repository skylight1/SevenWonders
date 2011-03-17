/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package skylight1.sevenwonders.social.facebook;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;


public class FacebookScoreActivity extends Activity {

	public static final String WALL_POST_PARAMS_EXTRA_KEY = 
		FacebookScoreActivity.class.getName() + ".WALL_POST_PARAMS_EXTRA_KEY";
	
	public static final String MY_SCORE = "MY_SCORE";
	
	private Bundle mParams;
	
	private String myScore;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//ALog.m();
		
		super.onCreate(savedInstanceState);
		
		Intent startingIntent = getIntent();		
		mParams = startingIntent.getBundleExtra(WALL_POST_PARAMS_EXTRA_KEY);
		myScore = startingIntent.getStringExtra(MY_SCORE);
		if ( null == mParams ) {
			//ALog.w("No wall post parameters provided. Skipping Facebook.");
			finish();
			return;			
		}
		
		//setContentView(R.layout.facebook);
		
		Intent loginAndGetPermissions = new Intent(this, LoginAndGetPermissions.class);
		startActivityForResult(loginAndGetPermissions, 0);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//ALog.m();
		
		if ( RESULT_OK == resultCode ) {
			postScore();
			return;
		}
		if ( RESULT_CANCELED != resultCode ) {
			//ALog.w("Non-OK result code " + resultCode + ". Finishing.");
		}
		finish();
	}
	
	private void postScore() {
		//ALog.m();
		
		Facebook facebook = new Facebook();		 
		SessionStore.restore(facebook, this);

		Bundle parameters = new Bundle();
		parameters.putString("message","I got a score of " + myScore + " in Seven Wonders!");
		try {
			String resp = facebook.request("me/feed",parameters,"POST");
			resp.compareTo("hello");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showErrorAndFinish() {
		//ALog.m();
		FacebookUtil.showFacebookErrorToast(this);
		finish();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		//ALog.m(hasFocus);
		super.onWindowFocusChanged(hasFocus);
		/*Doesn't work if Facebook decides to show multiple dialogs, you get a spurious call inbetween.
		if ( hasFocus ) {
			//ALog.i("Received focus, assuming Facebook canceled, finishing and returning canceled result.");
	    	setResult(RESULT_CANCELED);
			finish();
		}
		*/
	}

	private class WallPostDialogListener implements DialogListener {
	    public void onFacebookError(FacebookError e) {
			//ALog.m();
	    	showErrorAndFinish();
	    }

	    public void onError(DialogError e) {
			//ALog.m();
	    	showErrorAndFinish();        
	    }

	    public void onCancel() {
			//ALog.m();
	    	finish();
	    }
	    
		public void onComplete(Bundle values) {
			//ALog.m();
			
			/*This happens if the user chooses skip instead of publish, so we don't actually want to  show an error for that case, I guess.
			final String postId = values.getString("post_id");
			if ( null == postId ) {
				showErrorAndFinish();
				return;
			}
			*/

			//Success.
			finish();
		}
	}
	
}
