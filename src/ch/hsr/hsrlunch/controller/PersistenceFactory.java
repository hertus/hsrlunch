package ch.hsr.hsrlunch.controller;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.widget.Toast;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.model.Badge;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.Week;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.DateHelper;
import ch.hsr.hsrlunch.util.XMLParser;

import com.actionbarsherlock.view.MenuItem;

public class PersistenceFactory implements OfferConstants {

	private Week week;
	private WorkDay workday;
	private Offer offer;
	private Badge badge;

	private WeekDataSource weekDataSource;
	private WorkDayDataSource workDayDataSource;
	private OfferDataSource offerDataSource;
	private BadgeDataSource badgeDataSource;

	private SparseArray<Offer> offerList;
	private SparseArray<WorkDay> workdayList;
	private SparseArray<SparseArray<Pair<String, String>>> updatedOfferList;

	private MenuItem item;

	/**
	 * PersistenceFactory opens DB Connection on initialize and gets the
	 * available Data from SQLite to Domain Objects. Get all Offers and more
	 * through the WEEK Object
	 * 
	 * @param dbHelper
	 */
	public PersistenceFactory(DBOpenHelper dbHelper) {
		weekDataSource = new WeekDataSource(dbHelper);
		workDayDataSource = new WorkDayDataSource(dbHelper);
		offerDataSource = new OfferDataSource(dbHelper);
		badgeDataSource = new BadgeDataSource(dbHelper);

		createAndFillAllFromDB();
	}

	public void newUpdateTask() {
		new UpdateTask().execute();
	}

	private class UpdateTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			if (item != null) {
				item.setActionView(R.layout.progress);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				updateAllOffers();
				return true;
			} catch (Exception e) {
				Log.e("ch.hsr.hsrlunch", "error on updating updateAllOffers()",
						e);
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean success) {
			Toast toast = Toast.makeText(MainActivity.getMainContext(),
					"Menus up to date!", Toast.LENGTH_SHORT);
			toast.show();
			if (item != null) {
				item.setActionView(null);
			}
		}
	}

	private void createAndFillAllFromDB() {

		// Fill Badge
		badgeDataSource.openRead();
		badge = new Badge(badgeDataSource.getBadgeAmount(),
				badgeDataSource.getBadgeLastUpdate());
		badgeDataSource.close();

		// Fill Offers and WorkDay
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
		workDayDataSource.close();
		offerDataSource.close();

		// Fill Week
		weekDataSource.openRead();
		week = new Week(weekDataSource.getWeekLastUpdate(), workdayList);
		weekDataSource.close();
	}

	public void updateAllOffers() {
		Log.d("PersistenceFactory", "Begin with updateAllOffers()");

		XMLParser parser = new XMLParser();
		updatedOfferList = parser.parseOffers();

		// Update Offer and Workday
		offerDataSource.openWrite();
		workDayDataSource.openWrite();
		for (int nrWorkDay = OFFER_MONDAY; nrWorkDay <= OFFER_FRIDAY; nrWorkDay++) {
			if (updatedOfferList.get(nrWorkDay) != null) {

				// Set parsed OfferContent in Object and DB
				for (int nrOfferType = OFFER_DAILY; nrOfferType <= OFFER_WEEK; nrOfferType++) {
					if (updatedOfferList.get(nrWorkDay).get(nrOfferType).first != null) {
						String content = updatedOfferList.get(nrWorkDay).get(
								nrOfferType).first;
						workday.getOfferList().get(nrOfferType)
								.setContent(content);
						offerDataSource.setOfferContent(content, nrOfferType,
								nrWorkDay);

						String price = updatedOfferList.get(nrWorkDay).get(
								nrOfferType).second;
						workday.getOfferList().get(nrOfferType).setPrice(price);
						offerDataSource.setOfferPrice(price, nrOfferType,
								nrWorkDay);
					} else {
						Log.d("PersistenceFactory",
								"offerlist offer item type " + nrOfferType
										+ " was null");
					}
				}

				// Set OfferList to WorkdayObject and DB
				long updateWorkdayDate = DateHelper.getDateOfWorkDay(nrWorkDay)
						.getTime();
				week.getDayList().get(nrWorkDay).setDate(updateWorkdayDate);
				workDayDataSource.setWorkdayDate(nrWorkDay, updateWorkdayDate);
			} else {
				Log.d("PersistenceFactory", "offerlist workday item "
						+ nrWorkDay + " was null");
			}
		}

		offerDataSource.close();
		workDayDataSource.close();

		// update LatUpdate in Week
		weekDataSource.openWrite();
		week.setLastUpdate(DateHelper.getMondayOfThisWeekDate().getTime());
		weekDataSource.setWeekLastUpdate(week.getLastUpdate());
		weekDataSource.close();

		Log.d("PersistenceFactory", "End with updateAllOffers()");
	}

	public void updateBadgeEntry(double amount, long date) {
		this.badge.setAmount(amount);
		this.badge.setLastUpdate(date);

		badgeDataSource.openWrite();
		badgeDataSource.setBadgeAmount(amount);
		badgeDataSource.setBadgeLastUpdate(date);
		badgeDataSource.close();
	}

	public Week getWeek() {
		return week;
	}

	public Badge getBadge() {
		return badge;
	}

	public void setMenuItem(MenuItem item) {
		this.item = item;
	}
}