package skylight1.sevenwonders.view;

import skylight1.sevenwonders.PlayActivity;
import skylight1.sevenwonders.R;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

/**
 * Used to show a string text message in 2 TextViews on top of the game view.
 * This can be used to show messages to the users telling the user about the game. 
 * The messages will stay visible for a short amount of time and will then disappear.
 * If a new message is set while the old one is still visible, the old one is replaced.
 * There are two different priorities: Message and Alerts: Messages will be displayed on the bottom
 * of the screen while the more important alerts will be displayed with a larger font right in the middle
 * of the screen.
 * 
 * @author Johannes
 */
public class GameMessagesDisplay {
	private static final int HIDE_GAME_EVENT_MESSAGE = 1;
	private enum Priority {
		MESSAGE, ALERT
	}
	
	public enum GameEvent {	
		END_OF_WORLD_REACHED("A magical barrier bars your way!\nTurn around!", Priority.ALERT),
		ANHK_COLLECTED("Ankh collected!", Priority.MESSAGE),
		HIT_BY_SWORD("Struck by a Sword!", Priority.MESSAGE),
		TIME_BONUS_FOUND("Extra time!", Priority.MESSAGE), 
		RUBY_FOUND("Fly through walls for a while!", Priority.MESSAGE),
		INVINCIBLE("Invincibility is yours for a while!", Priority.MESSAGE),
		NOT_HARMED("Saved by Invincibility!", Priority.MESSAGE),
		AAARGH("Aaargh!", Priority.ALERT),
		COIN_COLLECTED("Riches are yours!", Priority.MESSAGE),
		COLLIDED_WITH_OBSTACLE("You cannot fly through the pyramids!", Priority.ALERT),
		;
		
		public final String messageText;
		private final Priority priority;

		GameEvent(String messageText, Priority priority) {
			this.messageText = messageText;
			this.priority = priority;
		}
	}

	private static final long DISPLAY_TIME = 2000;
	
	TextView gameEventMessageView;
	TextView gameEventAlertView;
	
	private Handler handler;

	private static Handler updateUiHandler = null;
	
	public GameMessagesDisplay(Handler uiHandler, Activity activity) {
		updateUiHandler = uiHandler;
		gameEventMessageView = (TextView) activity.findViewById(R.id.gameEventMessageView);
		gameEventAlertView = (TextView) activity.findViewById(R.id.gameEventAlertView);
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
					GameMessagesDisplay.this.hideMessage((View)msg.obj);
			}
		};
	}

	private TextView getViewForPriority(Priority priority) {
		if (priority == Priority.ALERT) {
			return gameEventAlertView;
		} else {
			return gameEventMessageView;
		}
	}

	public void showMessage(Message msg) {
		GameEvent event = GameEvent.values()[msg.arg1];
		TextView textView = getViewForPriority(event.priority);
		handler.removeMessages(HIDE_GAME_EVENT_MESSAGE, textView);
		textView.setText(event.messageText);
		textView.setVisibility(View.VISIBLE);
		Message hideItAgainMessage = handler.obtainMessage(HIDE_GAME_EVENT_MESSAGE, textView);
		handler.sendMessageDelayed(hideItAgainMessage, DISPLAY_TIME);
	}
	
	public void hideMessage(View view) {
		view.setVisibility(View.GONE);		
	}

	public static void postMessage(GameEvent event) {
		if (updateUiHandler != null) {
			Message msg =
				updateUiHandler.obtainMessage(PlayActivity.SHOW_GAME_EVENT_MESSAGE_MESSAGE, event.ordinal(), 0);
			updateUiHandler.sendMessage(msg);
		}
	}
}
