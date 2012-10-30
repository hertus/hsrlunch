package ch.hsr.hsrlunch.util;

import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper implements DBConstants {
	private static final String DATABASE_NAME = "hsrlunch.db";
	private static final int DATABASE_VERSION = 27;

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_WEEK + " (" + COLUMN_WEEK_ID
				+ " INTEGER PRIMARY KEY, " + COLUMN_WEEK_LASTUPDATE
				+ " DATE NOT NULL)");

		db.execSQL("CREATE TABLE " + TABLE_WORKDAY + " (" + COLUMN_WORKDAY_ID
				+ " INTEGER PRIMARY KEY, " + COLUMN_WORKDAY_DATE
				+ " DATE NOT NULL," + COLUMN_WORKDAY_WEEKID
				+ " INTEGER NOT NULL REFERENCES " + TABLE_WEEK + ")");

		db.execSQL("CREATE TABLE " + TABLE_OFFER + " (" + COLUMN_OFFER_ID
				+ " INTEGER PRIMARY KEY, " + COLUMN_OFFER_TYPE
				+ " INTEGER NOT NULL, " + COLUMN_OFFER_CONTENT
				+ " TEXT NOT NULL, " + COLUMN_OFFER_PRICE + " TEXT NOT NULL, "
				+ COLUMN_OFFER_WORKDAYID + " INTEGER NOT NULL REFERENCES "
				+ TABLE_WORKDAY + ")");

		db.execSQL("CREATE TABLE " + TABLE_BADGE + " (" + COLUMN_BADGE_ID
				+ " INTEGER PRIMARY KEY, " + COLUMN_BADGE_AMOUNT
				+ " DOUBLE NOT NULL, " + COLUMN_BADGE_LASTUPDATE
				+ " DATE NOT NULL)");

		initializeData(db);
	}

	private void initializeData(SQLiteDatabase db) {
		Log.w(DBOpenHelper.class.getName(),
				"Initialize HSRLunch Table Data for first usage!");

		// There will be only 2 Weeks, so only ID = 1-2 exists for Weeks
		db.execSQL("INSERT INTO " + TABLE_WEEK + "(" + COLUMN_WEEK_ID + ", "
				+ COLUMN_WEEK_LASTUPDATE + ")VALUES(1,0);");

		// 1 Week will have 5 Workdays, so only ID = 1-5 exist for Workday
		for (int i = 1; i <= 5; i++) {
			db.execSQL("INSERT INTO " + TABLE_WORKDAY + "(" + COLUMN_WORKDAY_ID
					+ ", " + COLUMN_WORKDAY_DATE + ", " + COLUMN_WORKDAY_WEEKID
					+ ")VALUES(" + i + "," + new Date().getTime() + ",1);");
		}

		// Every Workday has 3 OfferTypes
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 5; j++) {
				db.execSQL("INSERT INTO " + TABLE_OFFER + "("
						+ COLUMN_OFFER_TYPE + ", " + COLUMN_OFFER_CONTENT
						+ ", " + COLUMN_OFFER_PRICE + ", "
						+ COLUMN_OFFER_WORKDAYID + ")VALUES(" + i
						+ ",'placeholder','placeholder'," + j + ");");
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBOpenHelper.class.getName(),
				"New HSRLunch Database Version. Upgrading database from version "
						+ oldVersion + " to " + newVersion
						+ ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFER + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKDAY + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEK + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGE + ";");

		onCreate(db);

	}
}
