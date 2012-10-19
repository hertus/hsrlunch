package ch.hsr.hsrlunch.controller;

import ch.hsr.hsrlunch.util.DBConstants;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OfferDataSource implements DBConstants {
	private DBOpenHelper dbHelper;
	private SQLiteDatabase db;

	public OfferDataSource(DBOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * @param offerType
	 *            [1-3] where 1=Daily, 2=Vegi and 3=Weekly
	 * @param workDay
	 *            [1-5] where 1=MO ... 5=FR
	 * @return Content of this Offer as a String
	 */
	public String getOfferContent(int offerType, int workDay) {
		String where = COLUMN_OFFER_TYPE + "=" + offerType + " AND "
				+ COLUMN_OFFER_WORKDAYID + "=" + workDay;
		Cursor cursor = db.query(TABLE_OFFER,
				new String[] { COLUMN_OFFER_CONTENT }, where, null, null, null,
				null);
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_CONTENT));
	}

	/**
	 * @param offerType
	 *            [1-3] where 1=Daily, 2=Vegi and 3=Weekly
	 * @param workDay
	 *            [1-5] where 1=MO ... 5=FR
	 * @return Price of this Offer as a String
	 */
	public String getOfferPrice(int offerType, int workDay) {
		String where = COLUMN_OFFER_TYPE + "=" + offerType + " AND "
				+ COLUMN_OFFER_WORKDAYID + "=" + workDay;
		Cursor cursor = db.query(TABLE_OFFER,
				new String[] { COLUMN_OFFER_PRICE }, where, null, null, null,
				null);
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(COLUMN_OFFER_PRICE));
	}

	/**
	 * @param content
	 * @param type
	 *            [1-3] where 1=Daily, 2=Vegi and 3=Weekly
	 * @param workday
	 *            [1-5] where 1=MO ... 5=FR
	 */
	public void setOfferContent(String content, int type, int workday) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_OFFER_CONTENT, content);
		String where = COLUMN_OFFER_TYPE + "=" + type + " AND "
				+ COLUMN_OFFER_WORKDAYID + "=" + workday;
		db.update(TABLE_OFFER, values, where, null);

	}

	/**
	 * @param price
	 * @param type
	 *            [1-3] where 1=Daily, 2=Vegi and 3=Weekly
	 * @param workday
	 *            [1-5] where 1=MO ... 5=FR
	 */
	public void setOfferPrice(String price, int type, int workday) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_OFFER_PRICE, price);
		String where = COLUMN_OFFER_TYPE + "=" + type + " AND "
				+ COLUMN_OFFER_WORKDAYID + "=" + workday;
		db.update(TABLE_OFFER, values, where, null);

	}
}
