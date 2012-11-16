package ch.hsr.hsrlunch.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	public final long DAY_IN_MILLISECONDS = (24 * 60 * 60 * 1000);
	public final long WEEK_IN_MILLISECONDS = (7 * 24 * 60 * 60 * 1000);

	Date date;
	Calendar cal;
	DateFormat df;

	public DateHelper() {
		cal = Calendar.getInstance();
		date = new Date();
	}

	public String getMondayFormattedString(int dateFormatValue) {
		df = DateFormat.getDateInstance(dateFormatValue);
		return df.format(getMondayOfThisWeek());
	}

	public Date getMondayOfThisWeek() {
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}
	
	/**
	 * 
	 * @param dayNr - where Monday = 1 (like in HSRlunch domain model)
	 * @return Date Object of that Weekday
	 */
	public Date getDateOfWeekDay(int dayNr){
		dayNr += 2;
		cal.set(Calendar.DAY_OF_WEEK, dayNr);
		return cal.getTime();
	}

	public long getTime() {
		return date.getTime();
	}
}
