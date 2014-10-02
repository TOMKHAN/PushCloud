package hr.foi.tosulc.helper;

import hr.foi.tosulc.MessageList;
import hr.foi.tosulc.R;
import hr.foi.tosulc.database.MessageAdapter;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {

	public Context context;
	public static final int NOTIFICATION_ID = 1;
	NotificationCompat.Builder builder;

	public GCMIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		context = this;
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				generateNotification(context,
						"Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				generateNotification(context, "Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				//Log.i(TAG, "Received message");
				String message = intent.getExtras().getString("gcmMessage");
				String sponsor = intent.getExtras().getString("sponsorName");

				storeInDatabase(message, sponsor);
				generateNotification(context, message);
				
				CommonUtilities.updateListView(context);
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void generateNotification(Context context, String msg) {
		int icon = R.drawable.pushcloud_icon;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, msg, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, MessageList.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, msg, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

		/*
		 * s googla programski kod mNotificationManager = (NotificationManager)
		 * this.getSystemService(Context.NOTIFICATION_SERVICE);
		 * 
		 * PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new
		 * Intent(this, MainActivity.class), 0);
		 * 
		 * NotificationCompat.Builder mBuilder = new
		 * NotificationCompat.Builder(this)
		 * .setSmallIcon(R.drawable.ic_launcher)
		 * .setContentTitle("GCM Notification") .setStyle(new
		 * NotificationCompat.BigTextStyle() .bigText(msg))
		 * .setContentText(msg);
		 * 
		 * mBuilder.setContentIntent(contentIntent);
		 * mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		 */
	}

	public void storeInDatabase(String message, String sponsor) {
		MessageAdapter pa = new MessageAdapter(getApplicationContext());
		pa.openToRead();
		int broj_poruka = pa.getCount() + 1;
		pa.close();
		pa.openToWrite();
		pa.upisiPoruku(broj_poruka, message, sponsor);
		pa.close();
	}

}
