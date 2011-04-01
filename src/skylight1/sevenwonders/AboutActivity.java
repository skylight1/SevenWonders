package skylight1.sevenwonders;

import skylight1.sevenwonders.view.TextStyles;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * About box launched from the menu.
 * 
 * @author Rob
 */
public class AboutActivity extends Activity {
	
	private String buildAboutText() {
		ComponentName comp = new ComponentName(this, AboutActivity.class);
		PackageInfo pi = null;
		try {
			pi = getPackageManager().getPackageInfo(comp.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return "Seven Wonders"; // what else can you do?
		}
		return getString(R.string.aboutText1) + " " + pi.versionName + getString(R.string.aboutText2);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		final TextStyles textStyles = new TextStyles(this);
		final TextView aboutText = (TextView) findViewById(R.id.aboutText);
		aboutText.setText(buildAboutText());
		textStyles.applyHeaderTextStyle(aboutText);
		final Button website = (Button) findViewById(R.id.visitWebsite);
		textStyles.applyBodyTextStyle(website);
		website.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final WebView wv = new WebView(AboutActivity.this);
				wv.loadUrl("http://sevenwondersgame.com");
				AboutActivity.this.setContentView(wv);
			}
		});

	}

}
