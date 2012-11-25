package ch.hsr.hsrlunch.controller;

import ch.hsr.hsrlunch.util.DBConstants;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BadgeDataSource implements DBConstants {
	private DBOpenHelper dbHelper;
	private SQLiteDatabase db;
	private int standardBadgeId;

	public BadgeDataSource(DBOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
		standardBadgeId = 1;
	}

	public void openWrite() {
		db = dbHelper.getWritableDatabase();
	}

	public void openRead() {
		db = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * @return amount of Badge as a double
	 */
	public Double getBadgeAmount() {
		String where = COLUMN_BADGE_ID + " = " + standardBadgeId;
		Cursor cursor = db.query(TABLE_BADGE,
				new String[] { COLUMN_BADGE_AMOUNT }, where, null, null, null,
				null);
		cursor.moveToFirst();
		double amount = cursor.getDouble(cursor
				.getColumnIndex(COLUMN_BADGE_AMOUNT));
		cursor.close();
		return amount;
	}

	/**
	 * @param amount
	 */
	public void setBadgeAmount(double amount) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_BADGE_AMOUNT, amount);
		String where = COLUMN_BADGE_ID + "=" + standardBadgeId;
		db.update(TABLE_BADGE, values, where, null);
	}

	/**
	 * @return Date in milliseconds as a Long
	 */
	public long getBadgeLastUpdate() {
		String where = COLUMN_BADGE_ID + " = " + standardBadgeId;
		Cursor cursor = db.query(TABLE_BADGE,
				new String[] { COLUMN_BADGE_LASTUPDATE }, where, null, null,
				null, null);
		cursor.moveToFirst();
		long date = cursor.getLong(cursor
				.getColumnIndex(COLUMN_BADGE_LASTUPDATE));
		cursor.close();
		return date;
	}

	/**
	 * @param date
	 *            Date in milliseconds as a Long
	 */
	public void setBadgeLastUpdate(long date) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_BADGE_LASTUPDATE, date);
		String where = COLUMN_BADGE_ID + "=" + standardBadgeId;
		db.update(TABLE_BADGE, values, where, null);
	}
}
