package ch.hsr.hsrlunch.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper implements DBConstants {
	private static final String DATABASE_NAME = "hsrlunch.db";
	private static final int DATABASE_VERSION = 1;

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_WEEK + " (" + COLUMN_WEEK_ID
				+ " INTEGER PRIMARY KEY," + COLUMN_WEEK_LASTUPDATE
				+ " DATE NOT NULL)");

		db.execSQL("CREATE TABLE " + TABLE_WORKDAY + " (" + COLUMN_WORKDAY_ID
				+ " INTEGER PRIMARY KEY," + COLUMN_WORKDAY_DATE
				+ " DATE NOT NULL," + COLUMN_WORKDAY_WEEKID
				+ " INTEGER NOT NULL REFERENCES " + TABLE_WEEK + ")");

		db.execSQL("CREATE TABLE " + TABLE_OFFER + " (" + COLUMN_OFFER_ID
				+ " INTEGER PRIMARY KEY, " + COLUMN_OFFER_TYPE
				+ " INTEGER NOT NULL, " + COLUMN_OFFER_CONTENT
				+ " TEXT NOT NULL, " + COLUMN_OFFER_PRICE + " TEXT NOT NULL, "
				+ COLUMN_OFFER_WORKDAYID + " INTEGER NOT NULL REFERENCES "
				+ TABLE_WORKDAY + ")");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBOpenHelper.class.getName(),
				"New HSRLunch Database Version. Upgrading database from version "
						+ oldVersion + " to " + newVersion
						+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKDAY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEK);
		onCreate(db);
	}
}
