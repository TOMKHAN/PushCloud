package hr.foi.tosulc;

import static hr.foi.tosulc.helper.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static hr.foi.tosulc.helper.CommonUtilities.EXTRA_MESSAGE;
import static hr.foi.tosulc.helper.CommonUtilities.PLAY_SERVICES_RESOLUTION_REQUEST;
import static hr.foi.tosulc.helper.CommonUtilities.PROPERTY_APP_VERSION;
import static hr.foi.tosulc.helper.CommonUtilities.PROPERTY_REG_ID;
import static hr.foi.tosulc.helper.CommonUtilities.SENDER_ID;
import static hr.foi.tosulc.helper.CommonUtilities.displayMessage;

import hr.foi.tosulc.helper.AlertDialogManager;
import hr.foi.tosulc.helper.CommonUtilities;
import hr.foi.tosulc.helper.ConnectionDetector;
import hr.foi.tosulc.helper.ServerUtilities;
import hr.foi.tosulc.helper.WakeLocker;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {

	public String regId = "";
	public String str;
	private String mobileRegisteredWithServer = "";
	Context context;

	GoogleCloudMessaging gcm;
	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		ImageButton btnListaPoruka = (ImageButton) findViewById(R.id.btnListaPoruka);
		btnListaPoruka.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						MessageList.class);
				startActivity(i);
			}
		});

		ImageButton btnSponsorList = (ImageButton) findViewById(R.id.btnListaSponzora);
		btnSponsorList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						SponsorActivity.class);
				startActivity(i);
			}
		});

		cd = new ConnectionDetector(getApplicationContext());
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		if (!cd.isConnectedToInternet()) {
			alert.showAlertDialog(MainActivity.this,
					getString(R.string.internet_connection_error),
					getString(R.string.please_connect_to_internet), false);
			// return;
		} else {

			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regId = getRegistrationId(context);

				if (regId.equals("")) {
					new RegisterAppWithGCM().execute();
				}
			}
		}

	}

	/**
	 * Receiving messages after async task done and showing it as Toast
	 * messages!
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			WakeLocker.acquire(getApplicationContext());

			Toast.makeText(getApplicationContext(), newMessage,
					Toast.LENGTH_SHORT).show();

			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {

		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 * 
	 * @return true if GooglePlayService is available
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				CommonUtilities.displayMessage(context, getString(R.string.device_not_compatible));
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service. If
	 * result is empty, the app needs to register.
	 * 
	 * @return registration ID
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.equals("")) {
			return "";
		}
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getPreferences(Context context) {
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * SharedPreferences
	 * 
	 * @param context
	 * @param regId
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * AsyncTask for registering device with the server off the main UI thread.
	 * 
	 */
	private class RegisterAppWithGCM extends AsyncTask<Void, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MainActivity.this,
					getString(R.string.getting_data_from_server),
					getString(R.string.wait), true);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging
							.getInstance(getApplicationContext());
				}
				regId = gcm.register(SENDER_ID);

				mobileRegisteredWithServer = ServerUtilities.register(
						getApplicationContext(), regId);
				if (mobileRegisteredWithServer.equals("registered")) {
					storeRegistrationId(getApplicationContext(), regId);
					displayMessage(context,
							context.getString(R.string.device_registered));
				} else if (mobileRegisteredWithServer.equals("already_here")) {
					CommonUtilities.displayMessage(getApplicationContext(),
							getString(R.string.already_registered));
					storeRegistrationId(getApplicationContext(), regId);
				} else if (mobileRegisteredWithServer.equals("server_problem")) {
					CommonUtilities.displayMessage(getApplicationContext(),
							getString(R.string.problems_with_server));
				}

			} catch (IOException ex) {
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
		}

	}
}
