package skylight1.sevenwonders.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;


/**
 * Activity used to load fonts and use them for textviews.
 * 
 * @author Johannes
 */

public class TextStyles {
	/** A {@link Typeface} used for smaller, longer texts. */
	private Typeface bodyTextTypeface;
	
	/** A {@link Typeface} used for larger, longer texts. */
	private Typeface headerTextTypeface;

	/**
	 * Constructor. Load font files.
	 * @param context
	 */
	public TextStyles(Context context) {
    	bodyTextTypeface = Typeface.createFromAsset(context.getAssets(),"eurof55.ttf");
    	headerTextTypeface = Typeface.createFromAsset(context.getAssets(),"euphorigenic.ttf");
    }	
	
	/**
	 * Apply the header text {@link Typeface} to a {@link TextView}.
	 * @param aTextView {@link TextView} to which to apply the body text {@link Typeface}.
	 */
	public void applyHeaderTextStyle(TextView aTextView) {
		aTextView.setTypeface(headerTextTypeface);
		aTextView.setTextColor(Color.WHITE);
		aTextView.setTextSize(40f);
	}
	
	/**
	 * Apply the header text typeface to a TextView.
	 * @param aTextView {@link TextView} to which to apply the body text {@link Typeface}.
	 */
	public void applyBodyTextStyle(TextView aTextView) {
		aTextView.setTypeface(bodyTextTypeface);
		aTextView.setTextColor(Color.WHITE);
		aTextView.setTextSize(32f);
		aTextView.setText(aTextView.getText().toString().toUpperCase());
	}
	
	/**
	 * Apply the header text {@link Typeface} to a {@link TextView}.
	 * Set a medium text size.
	 * @param aTextView {@link TextView} to which to apply the body text {@link Typeface}.
	 */
	public void applySmallTextForButtonStyle(TextView aTextView) {
		aTextView.setTypeface(headerTextTypeface);
		aTextView.setTextColor(Color.WHITE);
		aTextView.setTextSize(29f);
	}
}
