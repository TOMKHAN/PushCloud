package hr.foi.tosulc.helper;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities {

	public static final String TAG = "PushCloud";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "reg_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";

	// give your server registration url here
	public static final String SERVER_URL_FOR_REGISTRATING = "http://your_server.com/PushCloud_server/database/register.php";
	public static final String SERVER_URL_FOR_UNREGISTRATING = "http://your_server.com/PushCloud_server/database/unregister.php";
	public static final String SERVER_URL_FOR_SUBSCRIBE = "http://your_server.com/PushCloud_server/database/subscribe.php";
	public static final String SERVER_URL_FOR_PICTURES = "http://your_server.com/PushCloud_server/sponsor_image/";
	public static final String SERVER_URL_FOR_ALL_SPONSORS = "http://your_server.com/PushCloud_server/services/getAllSponsors.php";
	public static final String SERVER_ALL_SPONSORS_CONTROL_NUMBER = "3ss21a";
	//this variable depends on you webhost server! Change accordingly.
	public static final String SERVER_HTML_ENCODING = "windows-1250";

	public static final int UPDATE_SPONSORS_IN_MINUTES = 2;

	// Google project id - your sender id
	public static final String SENDER_ID = "";

	public static final String DISPLAY_MESSAGE_ACTION = "hr.foi.tosulc.DISPLAY_MESSAGE";
	public static final String UPDATE_LIST_VIEW = "hr.foi.tosulc.UPDATE_LISTVIEW";

	public static final String EXTRA_MESSAGE = "message";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common hr.foi.tosulc.gcmtest.helper because
	 * it's used both by the UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
	
	public static void updateListView(Context context) {
		Intent intent = new Intent(UPDATE_LIST_VIEW);
		context.sendBroadcast(intent);
	}
}
