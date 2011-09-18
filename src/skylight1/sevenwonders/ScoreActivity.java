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

	private static final int SCORE_PER_REMAINING_SECOND = 50;

	private static final int SCORE_PER_COLLECTED_SPELL = 1000;

	private static final int SCORE_PER_COLLECTED_COIN = 500;

	static final String KEY_REMAINING_TIME_SECONDS = "KEY_REMAINING_TIME_SECONDS";

	static final String KEY_COLLECTED_SPELL_COUNT = "KEY_COLLECTED_SPELL_COUNT";
	
	static final String KEY_COLLECTED_COIN_COUNT = "KEY_COLLECTED_COIN_COUNT";

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
		final int collectedCoinCount = callingIntent.getIntExtra(
			ScoreActivity.KEY_COLLECTED_COIN_COUNT, 0);

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
		final int calculateScore = calculateScore(collectedSpellsCount, collectedCoinCount, remainingSeconds, wonLevel);
		if (previousHighScore < calculateScore) {
			settings.setHighScore(oneBasedLevelNumber, calculateScore);
		}
		
		displayEndOfLevelMessage(wonLevel, level, collectedSpellsCount, collectedCoinCount, remainingSeconds);
	}

	/**
	 * Format and display a END OF LEVEL message.
     * @param wonLevel
     * @param level
     * @param collectedSpellsCount
     * @param collectedCoinCount
     * @param remainingSeconds
     */
	private void displayEndOfLevelMessage(boolean wonLevel, int level, int collectedSpellsCount,
                                          int collectedCoinCount, int remainingSeconds) {
		
		
		final String collectedSpellCountText;
		if (collectedSpellsCount == 0) {
			collectedSpellCountText = "No spells collected";
		} else {
			final String spellsText; 
			if (collectedSpellsCount == 1) {
				spellsText = "1 spell";
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
		String twitterMessage = messageBuilder.toString();
		messageBuilder.append(collectedSpellCountText + "\n");		

		// if the level has coins, then report how many were collected
		if (GameLevel.values()[level].getNumberOfCoins() != 0) {
			final String collectedCoinCountText;
			if (collectedCoinCount == 0) {
				collectedCoinCountText = "No coins collected";
			} else {
				final String coinsText;
				if (collectedCoinCount == 1) {
					coinsText = "1 coin";
				} else {
					coinsText = String.format("%2d coins", collectedCoinCount);
				}

				final int sum = collectedCoinCount * SCORE_PER_COLLECTED_COIN;
				collectedCoinCountText = String.format("%s: %2d X %d = + %d", coinsText, collectedCoinCount, SCORE_PER_COLLECTED_COIN, sum);
			}
			messageBuilder.append(collectedCoinCountText + "\n");
		}

		if ( wonLevel ) {
			messageBuilder.append(getRemainingTimeText(remainingSeconds) + "\n");
		}
		final String scoreString = getScoreString(collectedSpellsCount, collectedCoinCount, remainingSeconds, wonLevel, level) + "\n";
		twitterMessage += scoreString;
		messageBuilder.append(scoreString);
		messageBuilder.append(getWinOrLoseString(wonLevel, nextLevelExists));
		
		setupButtons(level, wonLevel, nextLevelExists,messageBuilder.toString(),twitterMessage);

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
                                  int collectedCoinCount, final int remainingTimeSeconds, final boolean wonLevel, final int level) {
		int score = calculateScore(collectedSpellCount, collectedCoinCount, remainingTimeSeconds, wonLevel);
        String scoreText = String.format("Level score: %02d", score);
        //display if high score is improved
        final Settings settings = new Settings(this);
        int highScore = settings.getHighScore(level+1);
        int highScoreImprovement = score - highScore;
        if (highScoreImprovement > 0 ) {
            scoreText += String.format("\n new high score! (+%02d)", highScoreImprovement);
        }
		return scoreText;
	}

	private int calculateScore(final int aCollectedSpellCount, int aCollectedCoinCount, final int aRemainingTimeSeconds, final boolean aWonLevel) {
		int score = aCollectedSpellCount * SCORE_PER_COLLECTED_SPELL;
		score += aCollectedCoinCount * SCORE_PER_COLLECTED_COIN;
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
			result = String.format("%d seconds left: %d X %d = + %d", 
				remainingTimeSeconds, remainingTimeSeconds, SCORE_PER_REMAINING_SECOND, sum);	
		} else {
			result = "Out of time!";
		}
		
		return result;
	}

	private void setupButtons(final int level, final boolean wasLevelWon, boolean nextLevelExists, String scoreMessage, String twitterMessage) {
		final String message = getString(R.string.postScoreMessage) + "\n" + scoreMessage + "http://sevenwondersgame.com";
		final String tweet = getString(R.string.postScoreMessage) + "\n" + twitterMessage + "http://sevenwondersgame.com";
		
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
				Intent intent = TwitterUpdater.getIntent(ScoreActivity.this, "KStKbvCfiPgSh2ScfomkfA", "geqI6BmQh5oKwXRIeTZ5RCSGmfapPxSJglzvtN6xf4", "skylight1.sevenwonders://oauth.callback", tweet);
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
		
		// If won the last level
		if ( wasLevelWon && !nextLevelExists ) {
			// No play next level or retry level button needed, there is no next and they won the current level.
			playNextLevel.setVisibility(View.GONE);
		} else {
			// Play game again button only needed if won last level and there's nothing else to do.
			playAgain.setVisibility(View.GONE);			
		}

		// If lost the level, the play next button shows the retry text.
		if (!wasLevelWon) {
			playNextLevel.setText(R.string.retry);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SoundTracks.setVolume(this);
	}

}
