package hr.foi.tosulc;

import static hr.foi.tosulc.helper.CommonUtilities.UPDATE_LIST_VIEW;
import hr.foi.tosulc.database.MessageAdapter;
import hr.foi.tosulc.helper.WakeLocker;
import hr.foi.tosulc.types.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MessageList extends Activity {
	public Context context;
	public List<Message> poruke = null;
	public static SimpleAdapter adapter;
	public static ListView lv;
	EditText etFilterMessageList;
	ImageButton ibClearText;
	TextView tvNoMessagesYet;
	public int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list);
		context = this;

		tvNoMessagesYet = (TextView) findViewById(R.id.tv_no_push_messages);
		etFilterMessageList = (EditText) findViewById(R.id.et_filter_message_list);
		etFilterMessageList.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String sponsorSearchString = etFilterMessageList.getText()
						.toString().toLowerCase(Locale.getDefault());
				if (sponsorSearchString.equals("")) {
					fillList(null);
				} else {
					MessageAdapter pa = new MessageAdapter(context);
					pa.openToRead();
					poruke = pa.getSearchResult(sponsorSearchString);
					pa.close();

					fillList(poruke);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		ibClearText = (ImageButton) findViewById(R.id.ib_clear_edittext);
		ibClearText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etFilterMessageList.setText("");
			}
		});

		ImageButton ib_delete_all_messages = (ImageButton) findViewById(R.id.ib_delete_all_messages);
		ib_delete_all_messages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					showDialog();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				UPDATE_LIST_VIEW));

		fillList(poruke);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		removeNotification();
	}

	private void fillList(List<Message> listaPoruka) {

		if (listaPoruka == null) {
			MessageAdapter pa = new MessageAdapter(context);
			pa.openToRead();
			poruke = pa.getListaPoruka();
			pa.close();
		}

		String[] from;
		int[] to;

		HashMap<String, String> map;
		ArrayList<HashMap<String, String>> fillMaps;

		Iterator<Message> itr;

		from = new String[] { "tekst", "vrijeme", "sponsor" };
		to = new int[] { R.id.tekst, R.id.vrijeme, R.id.sponsor_name };

		fillMaps = new ArrayList<HashMap<String, String>>();
		itr = poruke.iterator();
		i = 0;
		while (itr.hasNext()) {
			map = new HashMap<String, String>();
			Message pob = itr.next();
			map.put("tekst", pob.getTekst());
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			map.put("vrijeme", sdf.format(pob.getVrijeme()));
			map.put("sponsor", pob.getSponsor());
			fillMaps.add(map);
			i++;
		}

		if (i > 0) {
			tvNoMessagesYet.setVisibility(View.INVISIBLE);
		} else {
			tvNoMessagesYet.setVisibility(View.VISIBLE);
		}

		lv = (ListView) findViewById(R.id.lv_push_messages);
		lv.invalidateViews();

		adapter = new SpecialAdapter(this, fillMaps,
				R.layout.item_liste_poruka, from, to);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);

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
			return view;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mHandleMessageReceiver);
	}

	/**
	 * Receiving messages after async task done and showing it as Toast
	 * messages!
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			WakeLocker.acquire(getApplicationContext());
			fillList(null);
			removeNotification();
			WakeLocker.release();
		}
	};

	// alert dialog for deleteing all messages
	public void showDialog() throws Exception {
		AlertDialog.Builder builder = new AlertDialog.Builder(MessageList.this);

		builder.setMessage("Delete all messages?");

		builder.setPositiveButton("YES!",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MessageAdapter ma = new MessageAdapter(
								getApplicationContext());
						ma.openToWrite();
						ma.deleteAllMessages();
						ma.close();
						dialog.dismiss();
						fillList(null);
					}
				});

		builder.setNegativeButton("NO!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}
	
	public void removeNotification(){
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}
}
