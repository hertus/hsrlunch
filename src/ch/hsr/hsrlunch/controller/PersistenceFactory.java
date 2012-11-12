package ch.hsr.hsrlunch.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.text.format.DateUtils;
import android.text.method.DateTimeKeyListener;
import android.util.SparseArray;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.Week;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.util.DBOpenHelper;

public class PersistenceFactory implements OfferConstants {
	
	private final long DAY_IN_MILLISECONDS =  (24 * 60 * 60 * 1000);

	private Week week;
	private WorkDay workday;
	private Offer offer;
	private DBOpenHelper dbHelper;
	private OfferUpdater offUp;

	private WeekDataSource weekDataSource;
	private WorkDayDataSource workDayDataSource;
	private OfferDataSource offerDataSource;

	private SparseArray<WorkDay> workdayList;
	private SparseArray<Offer> offerList;

	public PersistenceFactory(DBOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
		offUp = new OfferUpdater(dbHelper);
		createAndFillAllOffers();
	}

	private void createAndFillAllOffers() {

		for (int nrWorkDay = OFFER_MONDAY; nrWorkDay <= OFFER_FRIDAY; nrWorkDay++) {
			for (int nrOfferType = OFFER_DAILY; nrOfferType <= OFFER_WEEK; nrOfferType++) {
				offerList = new SparseArray<Offer>();
				offer = new Offer(nrOfferType, offerDataSource.getOfferContent(
						nrOfferType, nrWorkDay), offerDataSource.getOfferPrice(
						nrOfferType, nrWorkDay));
				offerList.append(nrOfferType, offer);

			}
			workday = new WorkDay(nrWorkDay,
					workDayDataSource.getWorkDayDate(nrWorkDay), offerList);

			workdayList = new SparseArray<WorkDay>();
			workdayList.append(nrWorkDay, workday);
		}

		week = new Week(1, weekDataSource.getWeekLastUpdate(), workdayList);
	}

	public void updateDB() {
		offUp.updateAllOffer();
		Date date = new Date();
		DateUtils dateUtils = new DateUtils();
		GregorianCalendar gregorianCalendar = new GregorianCalendar();

		week.setLastUpdate(date.getTime());
		weekDataSource.setWeekLastUpdate(week.getLastUpdate());
		gregorianCalendar.get(Calendar.DAY_OF_WEEK);
		
		
		week.getDayList().get(OFFER_MONDAY).setDate(date.getTime());
		week.getDayList().get(OFFER_TUESDAY).setDate(date.getTime() + DAY_IN_MILLISECONDS);
		week.getDayList().get(OFFER_WEDNESDAY).setDate(date.getTime() + 2*DAY_IN_MILLISECONDS);
		week.getDayList().get(OFFER_THURSDAY).setDate(date.getTime() + 3*DAY_IN_MILLISECONDS);
		week.getDayList().get(OFFER_FRIDAY).setDate(date.getTime() + 4*DAY_IN_MILLISECONDS);

		
		
		updateAllFromDB();
	}

	public void updateAllFromDB() {

		for (int nrWordkDay = OFFER_MONDAY; nrWordkDay <= OFFER_FRIDAY; nrWordkDay++) {
			for (int nrOfferType = OFFER_DAILY; nrOfferType <= OFFER_WEEK; nrOfferType++) {
				offerList.get(nrOfferType).setContent(
						offerDataSource
								.getOfferContent(nrOfferType, nrWordkDay));
				offerList.get(nrOfferType).setPrice(
						offerDataSource.getOfferPrice(nrOfferType, nrWordkDay));

			}
			workdayList.get(nrWordkDay).setDate(
					workDayDataSource.getWorkDayDate(nrWordkDay));
		}
		week.setLastUpdate(weekDataSource.getWeekLastUpdate());
	}

	public Week getWeek() {
		return week;
	}

	public WorkDay getWorkday() {
		return workday;
	}

	public Offer getOffer() {
		return offer;
	}

}