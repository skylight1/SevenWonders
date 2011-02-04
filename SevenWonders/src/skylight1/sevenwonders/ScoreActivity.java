package skylight1.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends Activity {

	private static final int SCORE_PER_REMAINING_SECOND = 100;

	private static final int SCORE_PER_COLLECTED_SPELL = 1000;

	static final String KEY_REMAINING_TIME_SECONDS = "KEY_REMAINING_TIME_SECONDS";

	static final String KEY_COLLECTED_SPELL_COUNT = "KEY_COLLECTED_SPELL_COUNT";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_activity);

		final int collectedSpellCount = getIntent().getIntExtra(
			ScoreActivity.KEY_COLLECTED_SPELL_COUNT, 0);
		final int yourcollectedspellcount2 = R.id.yourCollectedSpellCount;
		final TextView yourCollectedSpellCount = (TextView) findViewById(yourcollectedspellcount2);
		final String collectedSpellCountText = collectedSpellCount + " spells X "
				+ SCORE_PER_COLLECTED_SPELL;
		yourCollectedSpellCount.setText(collectedSpellCountText);
		
		final TextView yourRemainingTimeSeconds = (TextView) findViewById(R.id.yourRemainingTimeSeconds);
		final int remainingTimeSeconds = getIntent().getIntExtra(
				ScoreActivity.KEY_REMAINING_TIME_SECONDS, 0);
		final String yourRemainingTimeSecondsText = String.format("%d s X %d", 
			remainingTimeSeconds, SCORE_PER_REMAINING_SECOND);
		yourRemainingTimeSeconds.setText(yourRemainingTimeSecondsText);

		final TextView yourScore = (TextView) findViewById(R.id.yourScore);
		final int score = collectedSpellCount * SCORE_PER_COLLECTED_SPELL 
			+ remainingTimeSeconds * SCORE_PER_REMAINING_SECOND;
		yourScore.setText(Integer.toString(score));

		final Button playAgain = (Button) findViewById(R.id.playAgain);
		playAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(ScoreActivity.this, PlayActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
