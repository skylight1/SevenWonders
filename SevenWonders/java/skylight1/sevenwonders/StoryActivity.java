package skylight1.sevenwonders;

import java.util.Arrays;
import java.util.List;

import skylight1.sevenwonders.view.StoryPagesController;
import skylight1.sevenwonders.view.StoryPagesController.OnOutOfPageSequenceBoundsListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StoryActivity extends Activity implements OnOutOfPageSequenceBoundsListener {
		
	/**
	 * List of strings to be shown on separate pages.
	 */
    private final static List<Integer> PAGE_TEXTS = Arrays.asList(new Integer[] { 
	    R.string.story01,
	    R.string.story02,
	    R.string.story03,
	    R.string.story04
	});

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_page);
        
        boolean isFirstTimeUse = !(new Settings(this).wasGameStartedAtLeastOnce());
        
        new StoryPagesController(this, this, PAGE_TEXTS, isFirstTimeUse);
    }

	@Override
	public void onLeftPageSequenceOnTheLeft() {			
		startActivity(new Intent(this, MenuActivity.class));
		finish();
	}

	@Override
	public void onLeftPageSequenceOnTheRight() {
		boolean isFirstTimeUse = !(new Settings(this).wasGameStartedAtLeastOnce());
		final Class<? extends Activity> nextActivityClass = isFirstTimeUse ? PlayActivity.class : LevelChooserActivity.class;
		startActivity(new Intent(this, nextActivityClass));
		finish();
	}
}