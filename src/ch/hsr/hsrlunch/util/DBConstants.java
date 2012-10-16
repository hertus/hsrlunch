package ch.hsr.hsrlunch.util;

public interface DBConstants {
	public final static String TABLE_WEEK = "Week";
	public final static String COLUMN_WEEK_ID = "WeekID";
	public final static String COLUMN_WEEK_LASTUPDATE = "LastUpdate";

	public final static String TABLE_WORKDAY = "Workday";
	public final static String COLUMN_WORKDAY_ID = "WorkdayID";
	public final static String COLUMN_WORKDAY_DATE = "Date";
	public final static String COLUMN_WORKDAY_WEEKID = "WeekID";

	public final static String TABLE_OFFER = "Offer";
	public final static String COLUMN_OFFER_ID = "OfferID";
	public final static String COLUMN_OFFER_TYPE = "OfferType";
	public final static String COLUMN_OFFER_CONTENT = "Content";
	public final static String COLUMN_OFFER_PRICE = "Price";
	public final static String COLUMN_OFFER_WORKDAYID = "WorkdayID";
}
