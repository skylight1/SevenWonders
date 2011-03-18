package skylight1.sevenwonders;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class StyledSpannableStringBuilder extends SpannableStringBuilder {



	public StyledSpannableStringBuilder appendBold(CharSequence text) {
		return appendWithStyle(new StyleSpan(Typeface.BOLD), text);
	}
	
	
	public StyledSpannableStringBuilder appendWithStyle(CharacterStyle c, CharSequence text) {
		super.append(text);
		int startPos = length() - text.length();
		setSpan(c, startPos, length(), 0);
		return this;
	}
	
	public StyledSpannableStringBuilder appendScaled(CharSequence text, float scaleFactor) {
		return appendWithStyle(new RelativeSizeSpan(scaleFactor), text);
	}
}