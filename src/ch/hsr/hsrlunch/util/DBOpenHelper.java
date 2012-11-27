package ch.hsr.hsrlunch.util;

import java.util.Date;

import ch.hsr.hsrlunch.controller.OfferConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper implements DBConstants, OfferConstants{
	private static final long INIT_DATE = 1351344627652L; // something last year
	private static final String DATABASE_NAME = "hsrlunch.db";
	private static final int DATABASE_VERSION = 40;

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_WEEK + " (" + COLUMN_WEEK_ID
				+ " INTEGER PRIMARY KEY, " + COLUMN_WEEK_LASTUPDATE
				+ " LONG NOT NULL)");

		db.execSQL("CREATE TABLE " + TABLE_WORKDAY + " (" + COLUMN_WORKDAY_ID
				+ " INTEGER PRIMARY KEY, " + COLUMN_WORKDAY_DATE
				+ " LONG NOT NULL," + COLUMN_WORKDAY_WEEKID
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
				+ " LONG NOT NULL)");

		initializeData(db);
	}

	private void initializeData(SQLiteDatabase db) {
		Log.w(DBOpenHelper.class.getName(),
				"Initialize HSRLunch Table Data for first usage!");

		// There will be only 2 Weeks, so only ID = 1-2 exists for Weeks
		db.execSQL("INSERT INTO " + TABLE_WEEK + "(" + COLUMN_WEEK_ID + ", "
				+ COLUMN_WEEK_LASTUPDATE + ")VALUES(1," + INIT_DATE + ");");

		// 1 Week will have 5 Workdays, so only ID = 1-5 exist for Workday
		for (int i = OFFER_MONDAY; i <= OFFER_FRIDAY; i++) {
			db.execSQL("INSERT INTO " + TABLE_WORKDAY + "(" + COLUMN_WORKDAY_ID
					+ ", " + COLUMN_WORKDAY_DATE + ", " + COLUMN_WORKDAY_WEEKID
					+ ")VALUES(" + i + "," + INIT_DATE + ",1);");
		}

		// Every Workday has 3 OfferTypes
		for (int i = OFFER_DAILY; i <= OFFER_WEEK; i++) {
			for (int j = OFFER_MONDAY; j <= OFFER_FRIDAY; j++) {
				db.execSQL("INSERT INTO " + TABLE_OFFER + "("
						+ COLUMN_OFFER_TYPE + ", " + COLUMN_OFFER_CONTENT
						+ ", " + COLUMN_OFFER_PRICE + ", "
						+ COLUMN_OFFER_WORKDAYID + ")VALUES(" + i + ",'"
						+ EMPTY + "','" + EMPTY + "'," + j + ");");
			}
		}

		// Badge placeholders
		double amountInit = 42.50;
		db.execSQL("INSERT INTO " + TABLE_BADGE + "(" + COLUMN_BADGE_ID + ", "
				+ COLUMN_BADGE_AMOUNT + ", " + COLUMN_BADGE_LASTUPDATE
				+ ")VALUES(1," + amountInit + "," + new Date().getTime() + ")");
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
