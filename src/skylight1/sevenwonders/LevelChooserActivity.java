package skylight1.sevenwonders;

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
import android.widget.ListView;
import android.widget.TextView;

public class LevelChooserActivity extends ListActivity {

	private static class LevelScore {
		final GameLevel level;

		final int score;

		public LevelScore(GameLevel aLevel, int aScore) {
			level = aLevel;
			score = aScore;
		}
	}

	private TextStyles wonderFonts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_chooser);

		wonderFonts = new TextStyles(this);

		final TextView heading = (TextView) findViewById(R.id.heading);
		wonderFonts.applyBodyTextStyle(heading);

		List<LevelScore> listOfLevels = new ArrayList<LevelScore>();
		for (GameLevel level : GameLevel.values()) {
			listOfLevels.add(new LevelScore(level, 1000));
		}
		ArrayAdapter<LevelScore> levelScoreAdapter = new ArrayAdapter<LevelScore>(this, R.layout.level_chooser_row, listOfLevels) {
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

				wonderFonts.applyHeaderTextStyle(levelNameTextView);
				wonderFonts.applyHeaderTextStyle(scoreTextView);

				final LevelScore levelScore = getItem(aPosition);
				levelNameTextView.setText("Level " + levelScore.level.ordinal());
				scoreTextView.setText("");

				return rowView;
			}
		};

		setListAdapter(levelScoreAdapter);
	}

	@Override
	protected void onListItemClick(ListView aL, View aV, int aPosition, long aId) {
		final LevelScore levelScore = (LevelScore) getListAdapter().getItem(aPosition);
		final Intent intent = new Intent(this, PlayActivity.class);
		intent.putExtra(ScoreActivity.KEY_LEVEL_ORDINAL, levelScore.level.ordinal());
		startActivity(intent);
	}
}
