package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends BaseActivityWithMenu implements OnClickListener {
	
	private TextView contentTextView;
	private Button leftButton;
	private Button middleButton;
	private Button rightButton;
	private TextStyles wonderFonts;
	private Settings settings;


	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realmenu);
		
        contentTextView = (TextView) findViewById(R.id.menu_content_textview);        
        leftButton = (Button) findViewById(R.id.menu_left_button);
        middleButton = (Button) findViewById(R.id.menu_middle_button);
        rightButton = (Button) findViewById(R.id.menu_right_button);             
   		
        wonderFonts = new TextStyles(this);
   	         
        wonderFonts.applyBodyTextStyle(contentTextView);        
        wonderFonts.applyHeaderTextStyle(leftButton);
        wonderFonts.applyHeaderTextStyle(middleButton);
        wonderFonts.applyHeaderTextStyle(rightButton);
        
        settings = new Settings(this);
        

        leftButton.setOnClickListener(this);
        middleButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);   
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.menu_left_button:
				showStory();
				break;
			case R.id.menu_middle_button:
				showSettings();
				break;
			case R.id.menu_right_button:
				startGameOrShowStory();
				break;
		}		
		
	}

	private void showSettings() {
		startActivity(new Intent(this, SettingsActivity.class));		
	}

	private void showStory() {
		startActivity(new Intent(this, StoryActivity.class));		
	}

	private void startGameOrShowStory() {
		if (settings.wasGameStartedAtLeastOnce()) {
			startGame();		
		} else {
			showStory();
		}
	}

	private void startGame() {
		startActivity(new Intent(this, PlayActivity.class));		
	}
		
}
