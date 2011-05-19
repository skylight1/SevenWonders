package skylight1.sevenwonders.social.facebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class WallPost {

	private final Bundle params = new Bundle();
	
	public static class Attachment {

		private final JSONObject attachment = new JSONObject();

		public void setName(String name) throws JSONException {
			if (null != name) {
				attachment.put("name", name);
			}
		}

		public void setCaption(String caption) throws JSONException {
			if (null != caption) {
				attachment.put("caption", caption);
			}
		}

		public void setHref(String href) throws JSONException {
			if (null != href) {
				attachment.put("href", href);
			}
		}

		public void setMedia(AttachmentMedia... media) throws JSONException {
			if ( null == media ) {
				return;
			}
			JSONArray attachmentMedia = new JSONArray();
			for ( AttachmentMedia mediaItem : media) {
				attachmentMedia.put(mediaItem.getJSONObject());
			}
			attachment.put("media", attachmentMedia);
		}

		public void setProperties(AttachmentProperties properties) throws JSONException {
			attachment.put("properties", properties.getJSONObject());
		}

		public String toString() {
			return attachment.toString();
		}
	}

	public static class AttachmentProperties {

		JSONObject values = new JSONObject();

		public void putText(String label, String text) throws JSONException {
			if ( null == text ) {
				return;
			}
			values.put(label, text);
		}

		public void putLink(String label, String text, String href) throws JSONException {
			if ( null == text && null == href ) {
				return;
			}
			if ( null == text ) {
				text = href;
			}
			
			// Works, but doesn't show up in action links area.
			JSONObject linkValues = new JSONObject();
			linkValues.put("text", text);
			linkValues.put("href", href);
			values.put(label, linkValues);
		}

		public JSONObject getJSONObject() {
			return values;
		}
	}

	public static class AttachmentMedia {

		private final JSONObject media = new JSONObject();

		public void setType(String type) throws JSONException {
			if (null != type) {
				media.put("type", type);
			}
		}

		public void setSrc(String src) throws JSONException {
			if (null != src) {
				media.put("src", src);
			}
		}

		public void setHref(String href) throws JSONException {
			if (null != href) {
				media.put("href", href);
			}
		}

		public JSONObject getJSONObject() {
			return media;
		}
	}

	public void setUserMessagePrompt(String userMessagePrompt) {
		if (null != userMessagePrompt) {
			params.putString("user_message_prompt", userMessagePrompt);
		}
	}

	public void setMessage(String message) {
		if (null != message) {
			params.putString("message", message);
		}
	}

	public void setAttachment(Attachment attachment) {
		if (null != attachment) {
			params.putString("attachment", attachment.toString());
		}
	}

	public Bundle getParams() {
		return params;
	}
}
