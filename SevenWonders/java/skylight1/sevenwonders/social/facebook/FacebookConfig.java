package skylight1.sevenwonders.social.facebook;

public class FacebookConfig {
	
	private static String sAppId;
	
	private static Integer sDialogIconResId;
	
	public static void initFacebook(String appId, Integer dialogIconResId) {
		sAppId = appId;
		sDialogIconResId = dialogIconResId;
	}

	public static String getAppId() {
		return sAppId;
	}

	public static int getDialogIconResId() {
		return sDialogIconResId;
	}
}
