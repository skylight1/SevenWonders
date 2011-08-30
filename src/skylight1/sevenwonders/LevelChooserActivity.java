package skylight1.sevenwonders;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;

import java.util.ArrayList;
import java.util.List;

import skylight1.sevenwonders.levels.GameLevel;
import skylight1.sevenwonders.view.TextStyles;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LevelChooserActivity extends ListActivity implements AdWhirlInterface {

	private static class LevelScore {
		final GameLevel level;

		final int score;
		
		final boolean locked;

		public LevelScore(GameLevel aLevel, int aScore, final boolean aLocked) {
			level = aLevel;
			score = aScore;
			locked = aLocked;
		}
	}

	private TextStyles wonderFonts;
	final private List<LevelScore> listOfLevels = new ArrayList<LevelScore>();
	private ArrayAdapter<LevelScore> levelScoreAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_chooser);

		wonderFonts = new TextStyles(this);

		final TextView heading = (TextView) findViewById(R.id.heading);
		wonderFonts.applyBodyTextStyle(heading);

		levelScoreAdapter = new ArrayAdapter<LevelScore>(this, R.layout.level_chooser_row, listOfLevels) {
			@Override
			public View getView(int aPosition, View aConvertView, ViewGroup aParent) {
				final View rowView;
				if (aConvertView == null) {
					rowView = LevelChooserActivity.this.getLayoutInflater().inflate(R.layout.level_chooser_row, null);
				} else {
					rowView = aConvertView;
				}
				final TextView levelNameTextView = (TextView) rowView.findViewById(R.id.levelName);
				final TextView scoreTextView = (TextView) rowView.findViewById(R.id.highScore);
				final ImageView icon = (ImageView) rowView.findViewById(R.id.levelIcon);

				wonderFonts.applyHeaderTextStyle(levelNameTextView);
				wonderFonts.applyHeaderTextStyle(scoreTextView);

				final LevelScore levelScore = getItem(aPosition);

				icon.setImageResource(levelScore.level.getIconResourceId());
				levelNameTextView.setText("Level " + (levelScore.level.ordinal() + 1));
				final String scoreMessage = levelScore.locked ? "LOCKED" : String.format("%d", levelScore.score);
				scoreTextView.setText(scoreMessage);

				// grey out the row if the level is locked
				rowView.setEnabled(! levelScore.locked);
				
				return rowView;
			}
			
			@Override
			public boolean isEnabled(int aPosition) {
				// disable the row if the level is locked
				return ! getItem(aPosition).locked;
			}
		};

		setListAdapter(levelScoreAdapter);
		
        ViewGroup layout = (ViewGroup)findViewById(R.id.layout_ad);
		Adverts.insertAdBanner(this,layout);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// update the scores and locked/unlocked status
		final Settings settings = new Settings(this);
		listOfLevels.clear();
		for (GameLevel level : GameLevel.values()) {
			// add one for one-based-indexing
			final int oneBasedLevelNumber = level.ordinal() + 1;
			listOfLevels.add(new LevelScore(level, settings.getHighScore(oneBasedLevelNumber), settings.isLevelLocked(oneBasedLevelNumber)));
		}
		levelScoreAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onListItemClick(ListView aL, View aV, int aPosition, long aId) {
		final LevelScore levelScore = (LevelScore) getListAdapter().getItem(aPosition);
		final Intent intent = new Intent(this, PlayActivity.class);
		intent.putExtra(ScoreActivity.KEY_LEVEL_ORDINAL, levelScore.level.ordinal());
		startActivity(intent);
	}

	@Override
	public void adWhirlGeneric() {
	}
}
