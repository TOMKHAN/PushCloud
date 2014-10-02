package hr.foi.tosulc.database;

import hr.foi.tosulc.types.Message;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageAdapter {
	public static final String TABLE = "poruke";
	public static final String KEY_ID = "id_poruke";
	private DBHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;
	private Context context;

	public MessageAdapter(Context c) {
		context = c;
	}

	public MessageAdapter openToRead() throws android.database.SQLException {

		sqLiteHelper = new DBHelper(context);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}

	public MessageAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new DBHelper(context);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		sqLiteHelper.close();
	}

	public int getCount() {
		Cursor cursor = sqLiteDatabase.rawQuery("select id_poruke from "
				+ TABLE + " where id_poruke=(select max(id_poruke) from "
				+ TABLE + ");", null);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			int broj = cursor.getInt(cursor.getColumnIndex("id_poruke"));
			cursor.close();
			return broj;
		} else {
			cursor.close();
			return 0;
		}
	}

	public void upisiPoruku(int id_poruke, String tekst, String sponsor) {
		sqLiteDatabase.execSQL("INSERT INTO poruke(id_poruke, tekst, sponsor) "
				+ "values ('" + id_poruke + "','" + tekst + "','" + sponsor
				+ "');");

	}

	public List<Message> getListaPoruka() {
		int id_poruke;
		String tekst, sponsor;
		long vrijeme;
		Date datum;
		Cursor cursor = sqLiteDatabase
				.rawQuery(
						"SELECT id_poruke, tekst, (strftime('%s', vrijeme) * 1000) as vrijeme, sponsor FROM poruke ORDER BY vrijeme DESC;",
						null);
		List<Message> rezultati = new ArrayList<Message>();
		cursor.moveToFirst();

		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			id_poruke = cursor.getInt(cursor.getColumnIndex("id_poruke"));
			tekst = cursor.getString(cursor.getColumnIndex("tekst"));
			vrijeme = cursor.getLong(cursor.getColumnIndexOrThrow("vrijeme"));
			sponsor = cursor.getString(cursor.getColumnIndex("sponsor"));
			datum = new Date(vrijeme);
			Message por = new Message(id_poruke, tekst, datum, sponsor);
			rezultati.add(por);
		}
		cursor.close();
		return rezultati;
	}

	public List<Message> getSearchResult(String sponsorSearchString) {
		int id_poruke;
		String tekst, sponsor;
		long vrijeme;
		Date datum;
		Cursor cursor = sqLiteDatabase
				.rawQuery(
						"SELECT id_poruke, tekst, (strftime('%s', vrijeme) * 1000) as vrijeme, sponsor FROM poruke WHERE sponsor LIKE '%"
								+ sponsorSearchString + "%'", null);
		List<Message> rezultati = new ArrayList<Message>();
		cursor.moveToFirst();

		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			id_poruke = cursor.getInt(cursor.getColumnIndex("id_poruke"));
			tekst = cursor.getString(cursor.getColumnIndex("tekst"));
			vrijeme = cursor.getLong(cursor.getColumnIndexOrThrow("vrijeme"));
			sponsor = cursor.getString(cursor.getColumnIndex("sponsor"));
			datum = new Date(vrijeme);
			Message por = new Message(id_poruke, tekst, datum, sponsor);
			rezultati.add(por);
		}
		cursor.close();
		return rezultati;

	}

	public void deleteAllMessages() {
		sqLiteDatabase.execSQL("DELETE FROM poruke");
	}
}
