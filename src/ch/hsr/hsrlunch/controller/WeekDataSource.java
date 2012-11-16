package ch.hsr.hsrlunch.controller;

import ch.hsr.hsrlunch.util.DBConstants;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeekDataSource implements DBConstants {
	private DBOpenHelper dbHelper;
	private SQLiteDatabase db;

	public WeekDataSource(DBOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
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
	 * @return Date in milliseconds from the Last Update as a Long
	 */
	public long getWeekLastUpdate() {
		String where = COLUMN_WEEK_ID + " = 1";
		Cursor cursor = db.query(TABLE_WEEK,
				new String[] { COLUMN_WEEK_LASTUPDATE }, where, null, null,
				null, null);
		cursor.moveToFirst();
		long lastupdate = cursor.getLong(cursor
				.getColumnIndex(COLUMN_WEEK_LASTUPDATE));
		cursor.close();
		return lastupdate;
	}

	/**
	 * @param dateInMillisec
	 *            must be Date in milliseconds
	 */
	public void setWeekLastUpdate(long dateInMillisec) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_WEEK_LASTUPDATE, dateInMillisec);
		String where = COLUMN_WEEK_ID + "= 1";
		db.update(TABLE_WEEK, values, where, null);
	}
}
