package ch.hsr.hsrlunch.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	public final long DAY_IN_MILLISECONDS = (24 * 60 * 60 * 1000);
	public final long WEEK_IN_MILLISECONDS = (7 * 24 * 60 * 60 * 1000);

	public static String getFormatedDateStringSHORT(Date date) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		return df.format(date);
	}

	public static String getFormatedDateStringMEDIUM(Date date) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(date);
	}

	public static String getFormatedDateStringLONG(Date date) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
		return df.format(date);
	}

	public static Date getMondayOfThisWeekDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	public static long getMondayOfThisWeekLong() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime().getTime();
	}

	public static boolean compareLastUpdateToMonday(long lastUpdate) {
		Calendar calLast = Calendar.getInstance();
		Calendar calMonday = Calendar.getInstance();

		calLast.setTime(new Date(lastUpdate));
		calMonday.setTime(getMondayOfThisWeekDate());

		return (calLast.get(Calendar.YEAR) == calMonday.get(Calendar.YEAR) && calLast
				.get(Calendar.DAY_OF_YEAR) == calMonday
				.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * 
	 * @param dayNr
	 *            - where Monday = 0 (business logic)
	 * @return Date Object of that Weekday
	 */
	public static Date getDateOfWorkDay(int dayNr) {
		Calendar cal = Calendar.getInstance();
		dayNr += 2;
		cal.set(Calendar.DAY_OF_WEEK, dayNr);
		return cal.getTime();
	}

	public static Date getDateOfWeekDay(int dayNr) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, dayNr);
		return cal.getTime();
	}

	public static int getDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static int getSelectedDayDayOfWeek() {
		int dayOfWeek = getDayOfWeek();
		if (dayOfWeek == 1 || dayOfWeek == 7) {
			return 4;
		} else {
			return ((dayOfWeek + 5) % 7);
		}
	}

	public long getTime() {
		return new Date().getTime();
	}
}
