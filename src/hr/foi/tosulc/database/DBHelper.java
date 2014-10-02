package hr.foi.tosulc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE poruke (id_poruke INTEGER NOT NULL PRIMARY KEY, tekst TEXT NOT NULL, vrijeme TIMESTAMP NOT NULL DEFAULT current_timestamp, sponsor VARCHAR(120) NOT NULL);");
		db.execSQL("CREATE TABLE sponsors (id_sponsor INTEGER NOT NULL PRIMARY KEY, sponsorName VARCHAR(120) NOT NULL);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
	}

}
