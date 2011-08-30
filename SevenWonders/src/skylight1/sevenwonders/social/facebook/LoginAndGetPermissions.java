/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package skylight1.sevenwonders.social.facebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginAndGetPermissions extends Activity {
    
	// XXX if these ever change have to invlidate any past sessions? they might need more permissions...
	// XXX what if user removes permission later? we would want to reprompt them then too...
	private static final String[] PERMISSIONS =
		new String[] {"publish_stream", "read_stream", "offline_access"};
	
    private Facebook mFacebook;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//ALog.m();
		
		super.onCreate(savedInstanceState);

		mFacebook = new Facebook();
        
        if ( mFacebook.isSessionValid() ) {
    		//ALog.i("Already have a saved Facebook session, skipping authorization.");
    		
        	setResult(RESULT_OK);
        	finish();
        	return;
        }        
        
        mFacebook.authorize(this, FacebookConfig.getAppId(), PERMISSIONS, new LoginDialogListener());
		//setContentView(R.layout.facebook);
    }
	
	private void showErrorAndFinish() {
		//ALog.m();
		
		FacebookUtil.showFacebookErrorToast(this);
    	setResult(RESULT_CANCELED);
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

    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
    		//ALog.m();
    		
            SessionStore.save(mFacebook, LoginAndGetPermissions.this);
        	setResult(RESULT_OK);
        	finish();
        }
        public void onFacebookError(FacebookError error) {
    		//ALog.m();
    		
        	showErrorAndFinish();
        }        
        public void onError(DialogError error) {
    		//ALog.m();
    		
        	showErrorAndFinish();
        }
        public void onCancel() {
    		//ALog.m();
    		
        	setResult(RESULT_CANCELED);
            finish();
        }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//ALog.m();
		
		if ( KeyEvent.KEYCODE_BACK == keyCode ) {
    		//ALog.i("Back button detected, setting result to canceled.");
			setResult(RESULT_CANCELED);
		}
		return super.onKeyDown(keyCode, event);
	}    
}
