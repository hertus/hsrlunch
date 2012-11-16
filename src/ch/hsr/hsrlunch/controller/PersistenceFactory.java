package ch.hsr.hsrlunch.controller;

import android.util.Pair;
import android.util.SparseArray;
import ch.hsr.hsrlunch.model.Badge;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.Week;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.DateHelper;

public class PersistenceFactory implements OfferConstants {

	private Week week;
	private WorkDay workday;
	private Offer offer;
	private Badge badge;
	private OfferUpdater offUp;

	private WeekDataSource weekDataSource;
	private WorkDayDataSource workDayDataSource;
	private OfferDataSource offerDataSource;

	private SparseArray<Offer> offerList;
	private SparseArray<WorkDay> workdayList;
	private SparseArray<SparseArray<Pair<String, String>>> updatedOfferList;

	public PersistenceFactory(DBOpenHelper dbHelper) {
		offUp = new OfferUpdater();
		weekDataSource = new WeekDataSource(dbHelper);
		workDayDataSource = new WorkDayDataSource(dbHelper);
		offerDataSource = new OfferDataSource(dbHelper);
		createAndFillAllFromDB();
	}

	private void createAndFillAllFromDB() {

		offerDataSource.openRead();
		workDayDataSource.openRead();

		workdayList = new SparseArray<WorkDay>();
		for (int nrWorkDay = OFFER_MONDAY; nrWorkDay <= OFFER_FRIDAY; nrWorkDay++) {

			offerList = new SparseArray<Offer>();
			for (int nrOfferType = OFFER_DAILY; nrOfferType <= OFFER_WEEK; nrOfferType++) {

				offer = new Offer(nrOfferType, offerDataSource.getOfferContent(
						nrOfferType, nrWorkDay), offerDataSource.getOfferPrice(
						nrOfferType, nrWorkDay));
				offerList.put(nrOfferType, offer);

			}
			workday = new WorkDay(workDayDataSource.getWorkDayDate(nrWorkDay),
					offerList);
			workdayList.put(nrWorkDay, workday);
		}

		offerDataSource.close();
		workDayDataSource.close();

		weekDataSource.openRead();
		week = new Week(weekDataSource.getWeekLastUpdate(), workdayList);
		weekDataSource.close();

		System.out.println("Size workdaylist: " + week.getDayList().size());
		for (int i = 0; i <= week.getDayList().size(); i++) {
			System.out.println(week.getDayList().get(i).getOfferList().get(0)
					.getPrice());
			System.out.println(week.getDayList().get(i).getOfferList().get(1)
					.getPrice());
			System.out.println(week.getDayList().get(i).getOfferList().get(2)
					.getPrice());
		}
	}

	public void updateAllOffers() {
		// TODO: Create a new Task and wait for it with ProgressBar
		updatedOfferList = offUp.updateAndGetOffersArray();
		DateHelper dateHelper = new DateHelper();

		offerDataSource.openWrite();
		workDayDataSource.openWrite();

		for (int nrWorkDay = OFFER_MONDAY; nrWorkDay <= OFFER_FRIDAY; nrWorkDay++) {

			// Set parsed OfferContent in Object and DB
			for (int nrOfferType = OFFER_DAILY; nrOfferType <= OFFER_WEEK; nrOfferType++) {
				String content = updatedOfferList.get(nrWorkDay).get(
						nrOfferType).first;
				workday.getOfferList().get(nrOfferType).setContent(content);
				offerDataSource
						.setOfferContent(content, nrOfferType, nrWorkDay);

				String price = updatedOfferList.get(nrWorkDay).get(nrOfferType).second;
				workday.getOfferList().get(nrOfferType).setPrice(price);
				offerDataSource.setOfferPrice(price, nrOfferType, nrWorkDay);
			}

			// Set OfferList to WorkdayObject and DB
			long updateTime = dateHelper.getDateOfWeekDay(nrWorkDay).getTime();
			week.getDayList().get(nrWorkDay).setDate(updateTime);
			workDayDataSource.setWorkdayDate(nrWorkDay, updateTime);
		}

		offerDataSource.close();
		workDayDataSource.close();

		weekDataSource.openWrite();
		week.setLastUpdate(dateHelper.getTime());
		weekDataSource.setWeekLastUpdate(week.getLastUpdate());
		weekDataSource.close();

		offerDataSource.openRead();
		for (int i = OFFER_MONDAY; i <= OFFER_FRIDAY; i++) {
			System.out.println("price" + i + ": "
					+ offerDataSource.getOfferPrice(1, i));
		}
		offerDataSource.close();
	}

	public Week getWeek() {
		return week;
	}

	public Badge getBadge() {
		return badge;
	}
}