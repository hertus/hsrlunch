package ch.hsr.hsrlunch.controller;

import ch.hsr.hsrlunch.util.DBConstants;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WorkDayDataSource implements DBConstants {
	private DBOpenHelper dbHelper;
	private SQLiteDatabase db;

	public WorkDayDataSource(DBOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * @param workdayId
	 * @return Workday Date in milliseconds as a Long
	 */
	public Long getWorkDayDate(int workdayId) {
		String where = COLUMN_WORKDAY_ID + " = " + workdayId;
		Cursor cursor = db.query(TABLE_WORKDAY,
				new String[] { COLUMN_WORKDAY_DATE }, where, null, null, null,
				null);
		cursor.moveToFirst();
		long date = cursor.getLong(cursor.getColumnIndex(COLUMN_WORKDAY_DATE));
		cursor.close();
		return date;
	}

	/**
	 * @param workdayId must be a Date in milliseconds
	 * @param dateInMillisec
	 */
	public void setWorkdayDate(int workdayId, long dateInMillisec) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_WORKDAY_DATE, dateInMillisec);
		String where = COLUMN_WORKDAY_ID + "=" + workdayId;
		db.update(TABLE_WORKDAY, values, where, null);
	}
}
