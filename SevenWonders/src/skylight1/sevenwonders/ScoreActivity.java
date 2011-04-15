package skylight1.sevenwonders;

import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.services.SoundTracks;
import skylight1.sevenwonders.social.facebook.FacebookConfig;
import skylight1.sevenwonders.social.facebook.FacebookScoreActivity;
import skylight1.sevenwonders.social.twitter.TwitterUpdater;
import skylight1.sevenwonders.view.StyledSpannableStringBuilder;
import skylight1.sevenwonders.view.TextStyles;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Displays the score at the end of the game.
 */
public class ScoreActivity extends Activity {

	private static final int SCORE_PER_REMAINING_SECOND = 100;

	private static final int SCORE_PER_COLLECTED_SPELL = 1000;

	static final String KEY_REMAINING_TIME_SECONDS = "KEY_REMAINING_TIME_SECONDS";

	static final String KEY_COLLECTED_SPELL_COUNT = "KEY_COLLECTED_SPELL_COUNT";
	
	static final String KEY_LEVEL_ORDINAL = "KEY_LEVEL_ORDINAL";

	static final String KEY_WON_LEVEL = "KEY_WON_LEVEL";

	private TextStyles textStyles;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_activity);
		
		final Intent callingIntent = getIntent();
		final boolean wonLevel = callingIntent.getBooleanExtra(KEY_WON_LEVEL, false);
		final int level = callingIntent.getIntExtra(KEY_LEVEL_ORDINAL, 0);
		final int oneBasedLevelNumber = level + 1;
		final int collectedSpellsCount = callingIntent.getIntExtra(
			ScoreActivity.KEY_COLLECTED_SPELL_COUNT, 0);
		
		final int remainingSeconds = callingIntent.getIntExtra(
			ScoreActivity.KEY_REMAINING_TIME_SECONDS, 0);
		
		textStyles = new TextStyles(this);
		
		// if the level was won, then unlock the next level
		final Settings settings = new Settings(this);
		if (wonLevel) {
			// add one for one-based-indexing
			settings.unlockLevel(oneBasedLevelNumber + 1);
		}

		// update the high score if necessary
		// add one for one-based-indexing
		final int previousHighScore = settings.getHighScore(oneBasedLevelNumber);
		final int calculateScore = calculateScore(collectedSpellsCount, remainingSeconds, wonLevel);
		if (previousHighScore < calculateScore) {
			settings.setHighScore(oneBasedLevelNumber, calculateScore);
		}
		
		displayEndOfLevelMessage(wonLevel, level, collectedSpellsCount, remainingSeconds);		
	}

	/**
	 * Format and display a END OF LEVEL message.
	 * @param wonLevel
	 * @param level
	 * @param collectedSpellsCount
	 * @param remainingSeconds
	 */
	private void displayEndOfLevelMessage(boolean wonLevel, int level, int collectedSpellsCount,
											int remainingSeconds) {
		
		
		final String collectedSpellCountText;
		if (collectedSpellsCount == 0) {
			collectedSpellCountText = "No spells collected :-(";
		} else {
			final String spellsText; 
			if (collectedSpellsCount == 1) {
				spellsText = "1 Spell";
			} else {
				spellsText = String.format("%2d spells", collectedSpellsCount);
			}
			
			final int sum = collectedSpellsCount * SCORE_PER_COLLECTED_SPELL;
			collectedSpellCountText = String.format("%s: %2d X %d = +%d",
				spellsText, collectedSpellsCount, SCORE_PER_COLLECTED_SPELL, sum );
		}
		
		boolean nextLevelExists = level < GameLevel.values().length - 1;
		
		// Build the message.		
		final StyledSpannableStringBuilder messageBuilder = new StyledSpannableStringBuilder();
		messageBuilder.appendScaled(getLevelEndTitle(wonLevel, level) + "\n", 1.66f);
		
		
		messageBuilder.append(collectedSpellCountText + "\n");		
		if ( wonLevel ) {
			messageBuilder.append(getRemainingTimeText(remainingSeconds) + "\n");
		}
		messageBuilder.append(getScoreString(collectedSpellsCount, remainingSeconds, wonLevel) + "\n");
		messageBuilder.append(getWinOrLoseString(wonLevel, nextLevelExists));
		
		setupButtons(level, wonLevel, nextLevelExists,messageBuilder.toString());

		// Make the message uppercase and set it on to a TextView
		TextView messageTextView = (TextView) findViewById(R.id.end__content_textview);		
		textStyles.applyBodyTextStyle(messageTextView);		
		messageTextView.setText(messageBuilder);	
	}

	private String getLevelEndTitle(boolean wonLevel, int level) {
		final int levelForDisplay = level + 1;
		final String result;
		if (wonLevel) {
			result = String.format("Level %d completed!", levelForDisplay); 
		} else {
			result = String.format("Level %d failed.", levelForDisplay);
		}
		return result;
	}

	private String getWinOrLoseString(boolean wonLevel, boolean nextLevelExists) {
		if (wonLevel && !nextLevelExists) {
			return "You won!";
		} else {
			return "";
		}		 		
	}

	private String getScoreString(final int collectedSpellCount, 
			final int remainingTimeSeconds, final boolean wonLevel) {
		int score = calculateScore(collectedSpellCount, remainingTimeSeconds, wonLevel);
		String scoreText = String.format("Level score: %02d", score);
		return scoreText;
	}

	private int calculateScore(final int aCollectedSpellCount, final int aRemainingTimeSeconds, final boolean aWonLevel) {
		int score = aCollectedSpellCount * SCORE_PER_COLLECTED_SPELL;
		// If the user won the level, as opposed to say, hit a sword, 
		// then reward them for extra time.
		if ( aWonLevel ) {
			score += aRemainingTimeSeconds * SCORE_PER_REMAINING_SECOND;
		}
		return score;
	}

	private String getRemainingTimeText(int remainingTimeSeconds) {
		final String result;
		
		if (remainingTimeSeconds > 0) {
			final int sum = remainingTimeSeconds * SCORE_PER_REMAINING_SECOND;
			result = String.format("%d secs left: %d X %d = + %d", 
				remainingTimeSeconds, remainingTimeSeconds, SCORE_PER_REMAINING_SECOND, sum);	
		} else {
			result = "Out of time!";
		}
		
		return result;
	}

	private void setupButtons(final int level, final boolean wasLevelWon, boolean nextLevelExists, String scoreMessage) {
		final String message = getString(R.string.postScoreMessage) + "\n" + scoreMessage + "http://sevenwondersgame.com";
		
		final Button playNextLevel = (Button) findViewById(R.id.end__playNextLevel);
		textStyles.applySmallTextForButtonStyle(playNextLevel);
		playNextLevel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(ScoreActivity.this, PlayActivity.class);
				// If the player failed to advance to the next level, this turns into a retry button
				// which will play the same level:
				intent.putExtra(KEY_LEVEL_ORDINAL, wasLevelWon ? level + 1 : level);
				startActivity(intent);
				finish();
			}
		});

		final Button playAgain = (Button) findViewById(R.id.end__playAgain);
		textStyles.applySmallTextForButtonStyle(playAgain);
		playAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent().setClass(ScoreActivity.this, MenuActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		final Button postToTwitter = (Button) findViewById(R.id.postToTwitter);
		textStyles.applySmallTextForButtonStyle(postToTwitter);
		postToTwitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = TwitterUpdater.getIntent(ScoreActivity.this, "KStKbvCfiPgSh2ScfomkfA", "geqI6BmQh5oKwXRIeTZ5RCSGmfapPxSJglzvtN6xf4", "skylight1.sevenwonders://oauth.callback", message);
				startActivity(intent);
				//finish();
			}
		});
		
		final Button postToFacebook = (Button) findViewById(R.id.postToFacebook);
		textStyles.applySmallTextForButtonStyle(postToFacebook);
		postToFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Instead of embedding your application secret into the application (which would be hackable)
				// Facebook uses a hash generated from your Android keystore, which you then add to your application at
				// the Facebook site.
				Intent intent = new Intent(ScoreActivity.this, FacebookScoreActivity.class);
				FacebookConfig.initFacebook("120351194706785",R.drawable.icon);
				intent.putExtra(FacebookScoreActivity.WALL_POST_PARAMS_EXTRA_KEY, new Bundle());
				intent.putExtra(FacebookScoreActivity.MY_SCORE, message);
				startActivity(intent);
				//finish();
			}
		});
		
		// If the user won the level and there is a next level, show the 
		// play next level button.
		if (wasLevelWon && nextLevelExists) {
			playNextLevel.setVisibility(View.VISIBLE);
			playAgain.setVisibility(View.INVISIBLE);
		// Otherwise turn it into a retry button and show the play again button.
		} else {
			playNextLevel.setText(R.string.retry);
			playAgain.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SoundTracks.setVolume(this);
	}

}
