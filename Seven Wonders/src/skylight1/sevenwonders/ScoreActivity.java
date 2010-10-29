package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.score_activity);
		
		TextView yourScore = (TextView) findViewById(R.id.yourScore);
		
		yourScore.setText(getString(R.string.yourScore,this.getIntent().getStringExtra(SevenWondersActivity.KEY_SCORE_EXTRA)));
		
		Button playAgain = (Button) findViewById(R.id.playAgain);
		
		playAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(ScoreActivity.this, SevenWondersActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
}
