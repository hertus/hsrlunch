package ch.hsr.hsrlunch.controller;

import java.util.Date;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.model.Badge;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.Week;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.util.BadgeParser;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.DateHelper;
import ch.hsr.hsrlunch.util.UpdateBadgeException;
import ch.hsr.hsrlunch.util.UpdateOfferException;
import ch.hsr.hsrlunch.util.UpdateParserException;
import ch.hsr.hsrlunch.util.XMLParser;

import com.actionbarsherlock.view.MenuItem;

public class PersistenceFactory implements OfferConstants {

	private MainActivity mainActivity;
	private int errorCause;

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
	private AsyncTask<Void, Void, Boolean> updateTask;

	private MenuItem menuItem;

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

	public void newUpdateTask(MainActivity mainActivity, boolean isOfferUpdate,
			boolean isBadgeUpdate) {
		this.mainActivity = mainActivity;
		updateTask = new UpdateTask(isOfferUpdate, isBadgeUpdate).execute();
	}

	public void stopUpdateTaskIfRunning() {
		if (updateTask != null) {
			if (updateTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
				Log.d("PersistenceFactory", "Try to cancel UpdateTask");
				updateTask.cancel(true);
			}
		}
	}

	private class UpdateTask extends AsyncTask<Void, Void, Boolean> {
		private boolean isOfferUpdate;
		private boolean isBadgeUpdate;

		public UpdateTask(boolean isOfferUpdate, boolean isBadgeUpdate) {
			this.isOfferUpdate = isOfferUpdate;
			this.isBadgeUpdate = isBadgeUpdate;
		}

		@Override
		protected void onPreExecute() {
			Log.d("PersistenceFactory", "Begin UpdateTask");
			startProgressRotate();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if (isOfferUpdate) {
					updateAllOffers();
				}
				if (isBadgeUpdate) {
					updateBadge();
				}
				return true;
			} catch (UpdateOfferException e) {
				setErrorCause(0);
				Log.w("Update", "Error on updating: " + e.getMessage());
				return false;
			} catch (UpdateParserException e) {
				setErrorCause(1);
				Log.w("Parser", "Error on updating: " + e.getMessage());
				return false;
			} catch (UpdateBadgeException e) {
				setErrorCause(2);
				Log.w("Badge", "Error on updating: " + e.getMessage());
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (!success) {
				Log.d("PersistenceFactory", "End UpdateTask with error");
				cancelCleaning();
				switch (errorCause) {
				case 0:
					mainActivity.setAndShowErrorMsg(2,
							R.string.err_update_failed);
					break;
				case 1:
					mainActivity.setAndShowErrorMsg(2,
							R.string.err_update_failed);
					break;
				case 2:
					mainActivity.setAndShowErrorMsg(2,
							R.string.err_badge_not_parseable);
					break;
				default:
					mainActivity.setAndShowErrorMsg(2,
							R.string.err_update_failed);
					break;
				}
			} else {
				Log.d("PersistenceFactory", "End UpdateTask successfull");
				stopProgressRotate();

				mainActivity.notifyDataChanges();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			cancelCleaning();
		}

		private void cancelCleaning() {
			badgeDataSource.close();
			offerDataSource.close();
			workDayDataSource.close();
			weekDataSource.close();

			stopProgressRotate();
		}

	}

	public void createAndFillAllFromDB() {

		// Fill Badge
		badgeDataSource.openRead();
		this.badge = new Badge(badgeDataSource.getBadgeAmount(),
				badgeDataSource.getBadgeLastUpdate());
		badgeDataSource.close();

		// Fill Offers and WorkDay
		offerDataSource.openRead();
		workDayDataSource.openRead();
		workdayList = new SparseArray<WorkDay>();
		for (int nrWorkDay = OFFER_MONDAY; nrWorkDay <= OFFER_FRIDAY; nrWorkDay++) {

			this.offerList = new SparseArray<Offer>();
			for (int nrOfferType = OFFER_DAILY; nrOfferType <= OFFER_WEEK; nrOfferType++) {

				this.offer = new Offer(
						nrOfferType,
						offerDataSource.getOfferContent(nrOfferType, nrWorkDay),
						offerDataSource.getOfferPrice(nrOfferType, nrWorkDay));
				this.offerList.put(nrOfferType, offer);

			}

			this.workday = new WorkDay(
					workDayDataSource.getWorkDayDate(nrWorkDay), offerList);
			this.workdayList.put(nrWorkDay, workday);
		}
		workDayDataSource.close();
		offerDataSource.close();

		// Fill Week
		weekDataSource.openRead();
		this.week = new Week(weekDataSource.getWeekLastUpdate(), workdayList);
		weekDataSource.close();
	}

	private void updateAllOffers() throws UpdateOfferException,
			UpdateParserException {
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
						week.getDayList().get(nrWorkDay).getOfferList()
								.get(nrOfferType).setContent(content);
						offerDataSource.setOfferContent(content, nrOfferType,
								nrWorkDay);

						String price = updatedOfferList.get(nrWorkDay).get(
								nrOfferType).second;
						week.getDayList().get(nrWorkDay).getOfferList()
								.get(nrOfferType).setPrice(price);
						offerDataSource.setOfferPrice(price, nrOfferType,
								nrWorkDay);
					} else {
						throw new UpdateOfferException(
								"offerlist offer item type was null");
					}
				}

				// Set OfferList to WorkdayObject and DB
				long updateWorkdayDate = DateHelper.getDateOfWorkDay(nrWorkDay)
						.getTime();
				week.getDayList().get(nrWorkDay).setDate(updateWorkdayDate);
				workDayDataSource.setWorkdayDate(nrWorkDay, updateWorkdayDate);
			} else {
				throw new UpdateOfferException(
						"offerlist workday item was null");
			}
		}

		offerDataSource.close();
		workDayDataSource.close();

		// update LatUpdate in Week
		weekDataSource.openWrite();
		week.setLastUpdate(DateHelper.getMondayOfThisWeekDate().getTime());
		weekDataSource.setWeekLastUpdate(week.getLastUpdate());
		weekDataSource.close();
	}

	private void updateBadge() throws UpdateBadgeException {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mainActivity);

		BadgeParser badgeParser = new BadgeParser(prefs);
		double badgeAmount = badgeParser.parseBadge();

		updateBadgeEntry(badgeAmount, new Date().getTime());
	}

	public void updateBadgeEntry(double amount, long date) {
		this.badge.setAmount(amount);
		this.badge.setLastUpdate(date);

		badgeDataSource.openWrite();
		badgeDataSource.setBadgeAmount(amount);
		badgeDataSource.setBadgeLastUpdate(date);
		badgeDataSource.close();
	}

	private void startProgressRotate() {
		if (menuItem != null) {
			menuItem.setActionView(R.layout.progress);
		} else {
			Log.w("Update", "menuItem was null");
		}
	}

	private void stopProgressRotate() {
		if (menuItem != null) {
			menuItem.setActionView(null);
		}
	}

	public Week getWeek() {
		return week;
	}

	public Badge getBadge() {
		return badge;
	}

	public void setMenuItem(MenuItem item) {
		this.menuItem = item;
	}

	/**
	 * 
	 * @param errorCause
	 *            is 0 = Update error, 1 = Parser Error, 2 = Connection Error
	 */
	private void setErrorCause(int errorCause) {
		this.errorCause = errorCause;
	}
}