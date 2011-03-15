package skylight1.sevenwonders;

import skylight1.sevenwonders.levels.GameLevel;
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

	static final String KEY_LEVEL_ORDINAL = "KEY_LEVEL_ORDINAL";

	static final String KEY_WON_LEVEL = "KEY_WON_LEVEL";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_activity);

		final boolean wonLevel = getIntent().getBooleanExtra(KEY_WON_LEVEL, false);
		final int level = getIntent().getIntExtra(KEY_LEVEL_ORDINAL, 0);
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
		
		final Button playNextLevel = (Button) findViewById(R.id.playNextLevel);
		playNextLevel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Intent intent = new Intent().setClass(ScoreActivity.this, PlayActivity.class);
				intent.putExtra(KEY_LEVEL_ORDINAL, level + 1);
				startActivity(intent);
				finish();
			}
		});

		final Button playAgain = (Button) findViewById(R.id.continueButton);
		playAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Intent intent = new Intent().setClass(ScoreActivity.this, MenuActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		// If the user won the level and there is a next level, show the 
		// play next level button.
		final boolean nextLevelExists = level < GameLevel.values().length - 1;
		if (wonLevel && nextLevelExists) {
			playNextLevel.setVisibility(View.VISIBLE);
			playAgain.setVisibility(View.GONE);
		// Otherwise show the play again button.
		} else {
			playNextLevel.setVisibility(View.GONE);
			playAgain.setVisibility(View.VISIBLE);
		}
		
		// Tell the user they won the game if they won the last level 
		// and there are no more levels.
		final TextView wonGame = (TextView) findViewById(R.id.wonGame);
		wonGame.setVisibility(wonLevel && !nextLevelExists ? View.VISIBLE : View.GONE);
	}
}
