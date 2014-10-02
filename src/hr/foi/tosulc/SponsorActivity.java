package hr.foi.tosulc;

import static hr.foi.tosulc.helper.CommonUtilities.SERVER_ALL_SPONSORS_CONTROL_NUMBER;
import static hr.foi.tosulc.helper.CommonUtilities.SERVER_URL_FOR_ALL_SPONSORS;
import hr.foi.tosulc.database.SponsorsAdapter;
import hr.foi.tosulc.helper.AlertDialogManager;
import hr.foi.tosulc.helper.CommonUtilities;
import hr.foi.tosulc.helper.ConnectionDetector;
import hr.foi.tosulc.helper.ServerUtilities;
import hr.foi.tosulc.types.Sponsor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.PatternSyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SponsorActivity extends Activity {
	public Context context;
	AsyncTask<Void, Void, Void> checkSponsorService;
	public String sponsorServiceStringReturnWithControl = "";
	public String sponsorServiceStringReturn = "";
	public boolean sponsorSubscriptionOK;
	private ConnectionDetector cd;
	private AlertDialogManager alert = new AlertDialogManager();
	private SharedPreferences prefs;
	public static final String PROPERTY_REG_ID = "reg_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sponsors);
		context = this;
		prefs = getPreferences(context);
		cd = new ConnectionDetector(context);

		ImageButton btnRefreshSponsors = (ImageButton) findViewById(R.id.btn_refresh_sponsors);
		btnRefreshSponsors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cd.isConnectedToInternet()) {
					new CheckSponsorService().execute();
				} else {
					CommonUtilities.displayMessage(context,
							getString(R.string.no_internet_connection));
				}

			}
		});

		if (!cd.isConnectedToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(SponsorActivity.this,
					getString(R.string.internet_connection_error),
					getString(R.string.please_connect_to_internet), false);
			fillList();
		} else {
			if (checkUpdateTimeInPreferences()) {
				new CheckSponsorService().execute();
			} else {
				fillList();
			}
		}

	}

	public static SimpleAdapter adapter;

	private void fillList() {

		List<Sponsor> sponsors = null;

		ListView lv;

		String[] from;
		int[] to;

		HashMap<String, String> map;
		ArrayList<HashMap<String, String>> fillMaps;

		Iterator<Sponsor> itr;

		SponsorsAdapter sa = new SponsorsAdapter(context);
		sa.openToRead();
		sponsors = sa.getSponsorList();
		sa.close();

		from = new String[] { "sponsorName" };
		to = new int[] { R.id.tv_sponsor_name };

		fillMaps = new ArrayList<HashMap<String, String>>();
		itr = sponsors.iterator();
		while (itr.hasNext()) {
			map = new HashMap<String, String>();
			Sponsor sponsor = itr.next();
			map.put("sponsorName", sponsor.getSponsor_name());
			fillMaps.add(map);
		}

		lv = (ListView) findViewById(R.id.lv_sponsor_list);
		lv.invalidateViews();

		adapter = new SpecialAdapter(this, fillMaps,
				R.layout.item_activity_sponsors, from, to);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);
		adapter.notifyDataSetChanged();

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(
						context,
						getString(R.string.long_click_on_list_item),
						Toast.LENGTH_SHORT).show();
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				new SubscribeToSponsor().execute(arg1);
				return true;
			}

		});

	}

	public class SpecialAdapter extends SimpleAdapter {

		public SpecialAdapter(Context context,
				List<HashMap<String, String>> items, int resource,
				String[] from, int[] to) {
			super(context, items, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			TextView tv_sponsor_name = (TextView) view
					.findViewById(R.id.tv_sponsor_name);
			boolean checkValue = checkSponsorSubscribed(tv_sponsor_name
					.getText().toString());
			ImageView iv_sponsor_check = (ImageView) view
					.findViewById(R.id.iv_sponsor_check);
			if (checkValue) {
				iv_sponsor_check.setVisibility(View.VISIBLE);
			} else {
				iv_sponsor_check.setVisibility(View.INVISIBLE);
			}
			String pathName = Environment.getExternalStorageDirectory()
					.toString()
					+ "/PushCloud/"
					+ tv_sponsor_name.getText().toString().replaceAll("(\\s+)|č|ć|ž|š|đ|Č|Ć|Ž|Š|Đ","")
							.toLowerCase(Locale.getDefault()) + ".png";
			if (checkIfFileExists(pathName)) {
				Bitmap bmp = BitmapFactory.decodeFile(pathName);
				ImageView img = (ImageView) view
						.findViewById(R.id.iv_sponsor_image);
				img.setImageBitmap(bmp);
			}
			return view;
		}// View }
	}

	/**
	 * Subscribe to sponsor. If the subscription is on, it will unsubscribe.
	 * 
	 * @author TOMKHAN
	 * 
	 */
	private class SubscribeToSponsor extends AsyncTask<View, Void, String> {
		ProgressDialog progressDialog;
		View view;
		TextView tv_sponsor_name;
		ImageView iv_sponsor_check;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SponsorActivity.this,
					getString(R.string.checking_sponsor_status),
					getString(R.string.wait), true);
		}

		@Override
		protected String doInBackground(View... params) {
			view = params[0];
			tv_sponsor_name = (TextView) view
					.findViewById(R.id.tv_sponsor_name);
			iv_sponsor_check = (ImageView) view
					.findViewById(R.id.iv_sponsor_check);
			String regId = prefs.getString(PROPERTY_REG_ID, "");
			String result = "";
			result = ServerUtilities.subsribeToSponsor(context, regId,
					tv_sponsor_name.getText().toString());
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("added")) {
				Toast.makeText(context,
						getString(R.string.subscribe_success),
						Toast.LENGTH_SHORT).show();
				saveSponsorInSharedPreferences(tv_sponsor_name.getText()
						.toString());
				iv_sponsor_check.setVisibility(View.VISIBLE);
			} else if (result.equals("deleted")) {
				Toast.makeText(context,
						getString(R.string.unsubscribe_success),
						Toast.LENGTH_SHORT).show();
				deleteSponsorFromSharedPreferences(tv_sponsor_name.getText()
						.toString());
				iv_sponsor_check.setVisibility(View.INVISIBLE);
			} else if (result.equals("no_sponsor")) {
				iv_sponsor_check.setVisibility(View.INVISIBLE);
				deleteSponsorFromSharedPreferences(tv_sponsor_name.getText()
						.toString());
				Toast.makeText(context,getString(R.string.sponsor_not_exist),
						Toast.LENGTH_SHORT).show();
			} else if (result.equals("connection_timedout")) {
				Toast.makeText(context,
						getString(R.string.server_connection_timedout),
						Toast.LENGTH_SHORT).show();
			}

			else {
				Toast.makeText(context,
						getString(R.string.unknown_error),
						Toast.LENGTH_SHORT).show();
			}
			progressDialog.dismiss();
		}
	}

	private SharedPreferences getPreferences(Context context) {
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private boolean saveSponsorInSharedPreferences(String sponsorName) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(sponsorName, sponsorName);
		if (editor.commit()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean deleteSponsorFromSharedPreferences(String sponsorName) {
		if (prefs.edit().remove(sponsorName).commit()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkSponsorSubscribed(String sponsorName) {
		boolean checkValue = prefs.contains(sponsorName);
		if (checkValue) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checking if there are sponsors on web. 3 cases: 1. there is, returns all
	 * of them. 2. there is non, returns empty string 3. webservice unavailable
	 * -> control number (server can return you error page or similar)!
	 * 
	 * 
	 */
	private class CheckSponsorService extends AsyncTask<Void, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			sponsorServiceStringReturnWithControl = "";
			sponsorServiceStringReturn = "";
			progressDialog = ProgressDialog.show(SponsorActivity.this,
					getString(R.string.checking_sponsors_web),
					getString(R.string.wait), true);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				URI uri = new URI(SERVER_URL_FOR_ALL_SPONSORS);
				HttpPost httpPost = new HttpPost(uri);
				StringEntity se = new StringEntity(HTTP.UTF_8);
				httpPost.setEntity(se);

				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is
				// established.
				int timeoutConnection = 5000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				int timeoutSocket = 5000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpClient = new DefaultHttpClient(
						httpParameters);
				BasicHttpResponse httpResponse = null;
				httpResponse = (BasicHttpResponse) httpClient.execute(httpPost);
				sponsorServiceStringReturnWithControl = EntityUtils.toString(
						httpResponse.getEntity(), HTTP.UTF_8);

			} catch (ClientProtocolException e) {
				sponsorServiceStringReturnWithControl = "connection_timedout";
				e.printStackTrace();
			} catch (URISyntaxException e) {
				sponsorServiceStringReturnWithControl = "connection_timedout";
				e.printStackTrace();
			} catch (ConnectTimeoutException e) {
				sponsorServiceStringReturnWithControl = "connection_timedout";
				e.printStackTrace();
			} catch (IOException e) {
				sponsorServiceStringReturnWithControl = "connection_timedout";
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {

			if (sponsorServiceStringReturnWithControl
					.equals("connection_timedout")) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.server_connection_timedout),
						Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
				return;
			}

			try {
				String controlNumber[] = sponsorServiceStringReturnWithControl
						.split("--");
				if (!controlNumber[0]
						.equals(SERVER_ALL_SPONSORS_CONTROL_NUMBER)) {
					sponsorServiceStringReturnWithControl = "";
				} else {
					sponsorServiceStringReturn = sponsorServiceStringReturnWithControl
							.replace(controlNumber[0] + "--", "");
				}
			} catch (PatternSyntaxException e) {
				sponsorServiceStringReturn = "";
			}

			SponsorsAdapter sa = new SponsorsAdapter(context);
			if (sponsorServiceStringReturn.equals("")) {
				sa.openToRead();
				if (sa.getSponsorsCount() > 0) {
					sa.deleteAllSponsors();
					fillList();
				}
				Toast.makeText(getApplicationContext(),
						getString(R.string.no_sponsors_available),
						Toast.LENGTH_LONG).show();

			} else {
				sa.openToWrite();
				saveUpdateTimeInPreferences();
				sa.insertSponsors(sponsorServiceStringReturn);
				fillList();
			}
			sa.close();
			progressDialog.dismiss();
		}

	}

	public static void refreshList() {
		adapter.notifyDataSetChanged();
	}

	public boolean checkIfFileExists(String pathName) {
		File myFile = new File(pathName);

		if (myFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void saveUpdateTimeInPreferences() {
		SharedPreferences sharedPreferences = getPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		long updateTime = Calendar.getInstance().getTimeInMillis();
		editor.putLong("updateTime", updateTime);
		editor.commit();
	}

	public boolean checkUpdateTimeInPreferences() {
		SharedPreferences sharedPreferences = getPreferences(context);
		long updateTime = sharedPreferences.getLong("updateTime", 0);
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (((currentTime - updateTime) > CommonUtilities.UPDATE_SPONSORS_IN_MINUTES * 60 * 1000 && updateTime != 0)
				|| updateTime == 0) {
			return true;
		} else {
			return false;
		}
	}

	public long getLastUpdateTime() {
		SharedPreferences sharedPreferences = getPreferences(context);
		return sharedPreferences.getLong("updateTime", 0);
	}

}
