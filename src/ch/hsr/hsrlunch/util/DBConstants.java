package ch.hsr.hsrlunch.util;

public interface DBConstants {
	final String TABLE_WEEK = "Week";
	final String COLUMN_WEEK_ID = "WeekID";
	final String COLUMN_WEEK_LASTUPDATE = "LastUpdate";

	final String TABLE_WORKDAY = "Workday";
	final String COLUMN_WORKDAY_ID = "WorkdayID";
	final String COLUMN_WORKDAY_DATE = "Date";
	final String COLUMN_WORKDAY_WEEKID = "WeekID";

	final String TABLE_OFFER = "Offer";
	final String COLUMN_OFFER_ID = "OfferID";
	final String COLUMN_OFFER_TYPE = "OfferType";
	final String COLUMN_OFFER_CONTENT = "Content";
	final String COLUMN_OFFER_PRICE = "Price";
	final String COLUMN_OFFER_WORKDAYID = "WorkdayID";
	
	final String TABLE_BADGE = "Badge";
	final String COLUMN_BADGE_ID = "BadgeID";
	final String COLUMN_BADGE_AMOUNT= "Amount";
	final String COLUMN_BADGE_LASTUPDATE = "LastUpdate";

	final String EMPTY = "EMPTY";
}
