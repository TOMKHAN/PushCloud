package hr.foi.tosulc.database;

import static hr.foi.tosulc.helper.CommonUtilities.SERVER_URL_FOR_PICTURES;
import hr.foi.tosulc.SponsorActivity;
import hr.foi.tosulc.types.Sponsor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class SponsorsAdapter {
	public static final String TABLE = "sponsors";
	public static final String KEY_ID = "id_sponsor";
	private DBHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;
	private Context context;

	public SponsorsAdapter(Context c) {
		context = c;
	}

	public SponsorsAdapter openToRead() throws android.database.SQLException {

		sqLiteHelper = new DBHelper(context);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}

	public SponsorsAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new DBHelper(context);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		sqLiteHelper.close();
	}

	// metoda za prvi upis u bazu sponzora!
	public void insertSponsors(String sponsors) {
		String sponsorFromDB;
		String sponsorFromWebName = "";
		boolean insertSponsorInDB = false;
		Cursor cursor = sqLiteDatabase.rawQuery(
				"SELECT sponsorName FROM sponsors;", null);
		String[] sponsorList = sponsors.split(",");

		for (String sponsorFromWeb : sponsorList) {
			// only when there is no sponsors in DB
			String[] sponsorFromWebParts = sponsorFromWeb.split(";");
			sponsorFromWebName = sponsorFromWebParts[0];
			String sponsorFromWebImageUrl = sponsorFromWebParts[1];
			Log.w("PUSHHH", "Dobio url sliku: " + sponsorFromWebImageUrl);

			if (sponsorFromWebImageUrl != null || sponsorFromWebImageUrl != "") {
				if (cursor.getCount() == 0) {
					new GetImageFromWeb().execute(sponsorFromWebImageUrl);
					sqLiteDatabase.execSQL("INSERT INTO sponsors(sponsorName) "
							+ "values ('" + sponsorFromWebName + "');");
				} else {

					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {
						sponsorFromDB = cursor.getString(cursor
								.getColumnIndex("sponsorName"));
						// if sponsor is on web but not in app DB
						if (sponsorFromDB.equals(sponsorFromWebName)) {
							insertSponsorInDB = true;
							break;
						}
					}

					if (!insertSponsorInDB) {
						new GetImageFromWeb().execute(sponsorFromWebImageUrl);
						sqLiteDatabase
								.execSQL("INSERT INTO sponsors(sponsorName) "
										+ "values ('" + sponsorFromWebName
										+ "');");
					}
					insertSponsorInDB = false;
				}
			}

		}

		// if sponsor is deleted on web but still in app DB
		Cursor cursorAfterUpdate = sqLiteDatabase.rawQuery(
				"SELECT sponsorName FROM sponsors;", null);
		boolean readyToDelete;
		if (cursorAfterUpdate.getCount() > 0) {
			for (cursorAfterUpdate.moveToFirst(); !(cursorAfterUpdate
					.isAfterLast()); cursorAfterUpdate.moveToNext()) {
				readyToDelete = true;
				sponsorFromDB = cursorAfterUpdate.getString(cursorAfterUpdate
						.getColumnIndex("sponsorName"));
				for (String sponsorFromWeb : sponsorList) {
					String[] sponsorFromWebParts = sponsorFromWeb.split(";");
					if (sponsorFromDB.equals(sponsorFromWebParts[0])) {
						Log.w("Push", "Ima isti: " + sponsorFromDB
								+ ". Ne brišem ga!");
						Log.w("PUSHHH", sponsorFromWeb);
						readyToDelete = false;
						break;
					}
				}
				if (readyToDelete) {
					Log.w("Push", "OBRISAN: " + sponsorFromDB);
					sqLiteDatabase
							.execSQL("DELETE FROM sponsors WHERE sponsorName='"
									+ sponsorFromDB + "';");
					deletePictureFromSD(sponsorFromDB);
					readyToDelete = true;
				}
			}
		}
		cursor.close();
		cursorAfterUpdate.close();

	}

	public int getSponsorsCount() {
		Cursor cursorAfterUpdate = sqLiteDatabase.rawQuery(
				"SELECT sponsorName FROM sponsors;", null);
		return cursorAfterUpdate.getCount();
	}

	public void deleteAllSponsors() {
		sqLiteDatabase.delete("sponsors", null, null);
	}

	public List<Sponsor> getSponsorList() {
		int id_sponsor;
		String name;
		Cursor cursor = sqLiteDatabase.rawQuery(
				"SELECT id_sponsor,sponsorName FROM sponsors;", null);
		List<Sponsor> rezultati = new ArrayList<Sponsor>();
		cursor.moveToFirst();

		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			id_sponsor = cursor.getInt(cursor.getColumnIndex("id_sponsor"));
			name = cursor.getString(cursor.getColumnIndex("sponsorName"));
			Sponsor sponsor = new Sponsor(id_sponsor, name);
			rezultati.add(sponsor);
		}
		cursor.close();
		return rezultati;
	}

	/**
	 * Picture from web (in any format) saving in .png (for easier manipulation
	 * in application).
	 * 
	 * 
	 */
	private class GetImageFromWeb extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				String sponsorPictureFullUrl = params[0];
				sponsorPictureFullUrl = sponsorPictureFullUrl.replaceAll(
						"(\\s+)|č|ć|ž|š|đ|Č|Ć|Ž|Š|Đ", "");
				String sponsorPictureNameWithoutHttp = sponsorPictureFullUrl
						.replace(SERVER_URL_FOR_PICTURES, "");
				Log.w("PUSHCLOUD",
						"Pošalje se na server zahtjev prije enkodanja: "
								+ sponsorPictureNameWithoutHttp);
				/*
				 * String sponsorPictureNameWithoutHttpEncoded = URLEncoder
				 * .encode(sponsorPictureNameWithoutHttp, SERVER_HTML_ENCODING);
				 */
				URL imageUrl = new URL(sponsorPictureFullUrl);
				Log.w("PUSHCLOUD", "Pošalje se na server zahtjev: "
						+ sponsorPictureFullUrl);
				URLConnection ucon = imageUrl.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is, 128);

				ByteArrayBuffer baf = new ByteArrayBuffer(5000);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				FileOutputStream outStream;
				Bitmap bmp = BitmapFactory.decodeByteArray(baf.toByteArray(),
						0, baf.toByteArray().length);
				File sdCardDirectory = Environment
						.getExternalStorageDirectory();
				baf.clear();
				new File(sdCardDirectory + "/PushCloud/").mkdir();

				File image = new File(sdCardDirectory + "/PushCloud/",
						sponsorPictureNameWithoutHttp.toLowerCase(Locale
								.getDefault()));
				Log.w("PUSHCLOUD",
						"Zapisao u mkdir: "
								+ sponsorPictureNameWithoutHttp
										.toLowerCase(Locale.getDefault()));
				outStream = new FileOutputStream(image);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				outStream.flush();
				outStream.close();

			} catch (Exception e) {
				Log.d("ImageManager", "Error: " + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			SponsorActivity.refreshList();
			return;
		}
	}

	/**
	 * Deletes picture from sd card. No need to notify user.
	 * Searches for croatian symbols to delete.
	 * 
	 * @param sponsorName
	 *            + .png extension to delete
	 */
	public void deletePictureFromSD(String sponsorName) {
		File sdCardDirectory = Environment.getExternalStorageDirectory();
		File image = new File(sdCardDirectory + "/PushCloud/", sponsorName
				.replaceAll("(\\s+)|č|ć|ž|š|đ|Č|Ć|Ž|Š|Đ", "").toLowerCase(
						Locale.getDefault())
				+ ".png");
		image.delete();
	}

}
