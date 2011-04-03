package skylight1.sevenwonders.view;

import java.util.List;

import skylight1.sevenwonders.R;
import skylight1.sevenwonders.R.id;
import skylight1.sevenwonders.R.string;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Controls switching between different pages of the back story.
 * 
 * @author johannes
 */
public class StoryPagesController implements OnClickListener, Runnable {    
    public final List<Integer> pageTextResourceIds;
    
    private int currentPage;

    private TextView contentTextView;

    private Button leftButton;
    
    private final TextStyles wonderFonts;

    private Button rightButton;

	private final OnOutOfPageSequenceBoundsListener onOutOfPageBoundsListener;

	private Handler handler;

	private final boolean isFirstTimeUse;
    
	/**
	 * Listener to be notified when one of the ends of the sequence of pages was reached.
	 *
	 */
    public interface OnOutOfPageSequenceBoundsListener {
    	public void onLeftPageSequenceOnTheLeft();
    	public void onLeftPageSequenceOnTheRight();
    }
    
    public StoryPagesController(Activity activity, OnOutOfPageSequenceBoundsListener listener,
    		List<Integer> pageTexts, boolean isFirstTimeUse) {
		onOutOfPageBoundsListener = listener;
		pageTextResourceIds = pageTexts;
		this.isFirstTimeUse = isFirstTimeUse;
        contentTextView = (TextView) activity.findViewById(R.id.story_content_textview);        
        leftButton = (Button) activity.findViewById(R.id.story_left_button);
        rightButton = (Button) activity.findViewById(R.id.story_right_button);             
   		
        wonderFonts = new TextStyles(activity);
   	         
        wonderFonts.applyBodyTextStyle(contentTextView);        
        wonderFonts.applyHeaderTextStyle(leftButton);
        wonderFonts.applyHeaderTextStyle(rightButton);          	

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);        
        
        handler = new Handler();
        goToFirstPage();     
    }

    private void goToFirstPage() {
        currentPage = -1;
        goToNextPage();        
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.story_left_button:
            goToPreviousPage();
            break;
        case R.id.story_right_button:
            goToNextPage();
            break;
        }
    }

    private void goToPreviousPage() {
        currentPage--;
        if (currentPage < 0) {
        	this.onOutOfPageBoundsListener.onLeftPageSequenceOnTheLeft();
        	currentPage = 0;
        } else {
            updatePageContents();
        }
    }

    private void updatePageContents() {
        contentTextView.setText(pageTextResourceIds.get(currentPage));
        maybeChangeButtonLabels();
    }

    private void goToNextPage() {
        currentPage++;
        final int length = pageTextResourceIds.size();
        if (currentPage == length) {
        	this.onOutOfPageBoundsListener.onLeftPageSequenceOnTheRight();
        	currentPage = length-1;
        } else {
            updatePageContents();
        }        
        
        if (isFirstTimeUse) {
        	//Prevent user from skipping through without reading..        
        	rightButton.setEnabled(false);
        	enableAfterDelay();
        } else {
        	rightButton.setEnabled(true);
        }
    }


	/**
     * Sets different labels for the buttons depending on which page we are on.
     */
    private void maybeChangeButtonLabels() {
        final int leftButtonLabel;
        final int rightButtonLabel;
        if (currentPage == 0) {
            leftButtonLabel = R.string.story_button_cancel; 
            rightButtonLabel = R.string.story_button_right;        
        } else if (currentPage == pageTextResourceIds.size()-1) {
            leftButtonLabel = R.string.story_button_left; 
            rightButtonLabel = R.string.story_button_start;
        } else {
            leftButtonLabel = R.string.story_button_left; 
            rightButtonLabel = R.string.story_button_right;
        }
        
        leftButton.setText(leftButtonLabel);
        rightButton.setText(rightButtonLabel);
    }

	@Override
	public void run() {
		rightButton.setEnabled(true);		
	}
	

    private void enableAfterDelay() {
    	handler.postDelayed(this, 2000);
	}
}
