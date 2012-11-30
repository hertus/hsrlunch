package ch.hsr.hsrlunch.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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
import ch.hsr.hsrlunch.ui.SettingsActivity;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.DateHelper;
import ch.hsr.hsrlunch.util.MyHttpClient;
import ch.hsr.hsrlunch.util.ParserException;
import ch.hsr.hsrlunch.util.UpdateException;
import ch.hsr.hsrlunch.util.XMLParser;

import com.actionbarsherlock.view.MenuItem;

public class PersistenceFactory implements OfferConstants {

	private MainActivity mainActivity;

	private Week week;
	private WorkDay workday;
	private Offer offer;
	private Badge badge;

	private HttpResponse response;
	private InputStream inputStream;

	private WeekDataSource weekDataSource;
	private WorkDayDataSource workDayDataSource;
	private OfferDataSource offerDataSource;
	private BadgeDataSource badgeDataSource;

	private SparseArray<Offer> offerList;
	private SparseArray<WorkDay> workdayList;
	private SparseArray<SparseArray<Pair<String, String>>> updatedOfferList;

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
		new UpdateTask(isOfferUpdate, isBadgeUpdate).execute();
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
			} catch (UpdateException e) {
				Log.w("PersistenceFactory",
						"Error on updating: " + e.getMessage());
				return false;
			} catch (ParserException e) {
				Log.w("XMLParser", "Error on updating: " + e.getMessage());
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			mainActivity.notifyDataChanges();
			if (success) {
				mainActivity.setAndShowErrorMsg(0, R.string.info_update_succes);
			} else {
				mainActivity.setAndShowErrorMsg(2, R.string.err_update_failed);
			}
			stopProgressRotate();
			Log.d("PersistenceFactory", "End UpdateTask");
		}

	}

	private void createAndFillAllFromDB() {

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

	private void updateAllOffers() throws UpdateException, ParserException {
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
						throw new UpdateException(
								"offerlist offer item type was null");
					}
				}

				// Set OfferList to WorkdayObject and DB
				long updateWorkdayDate = DateHelper.getDateOfWorkDay(nrWorkDay)
						.getTime();
				week.getDayList().get(nrWorkDay).setDate(updateWorkdayDate);
				workDayDataSource.setWorkdayDate(nrWorkDay, updateWorkdayDate);
			} else {
				throw new UpdateException("offerlist workday item was null");
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

	private void updateBadge() {
		Log.d("Persistence", "making updateBadge()");
		DefaultHttpClient client = new MyHttpClient().getMyHttpClient();

		// client.getCredentialsProvider().setCredentials(
		// new AuthScope(null, -1),
		// new UsernamePasswordCredentials("SIFSV-80018\\ChallPUser",
		// "1q$2w$3e$4r$5t"));
		// HttpGet request = new HttpGet(
		// "https://152.96.80.18/VerrechnungsportalService.svc/json/getBadgeSaldo");

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mainActivity);
		client.getCredentialsProvider().setCredentials(
				new AuthScope(null, -1),
				new UsernamePasswordCredentials(prefs.getString(
						SettingsActivity.PREF_BADGE_USERNAME, ""), prefs
						.getString(SettingsActivity.PREF_BADGE_PASSWORD, "")));
//		client.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
//				new UsernamePasswordCredentials("hsr\\c1buechi", ""));
		HttpGet request = new HttpGet(
				"https://152.96.21.52:4450/VerrechnungsportalService.svc/JSON/getBadgeSaldo");

		ByteArrayOutputStream content = new ByteArrayOutputStream();
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}

			JSONObject object = new JSONObject(
					new String(content.toByteArray()));

			Log.d("PersistenceFactory",
					"BadgeAmount: " + object.getDouble("badgeSaldo"));

			updateBadgeEntry(object.getDouble("badgeSaldo"),
					new Date().getTime());

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//mainActivity
			//		.setAndShowErrorMsg(2, R.string.err_badge_not_parseable);
			e.printStackTrace();
		} catch (JSONException e) {
			// Handle falls der Wert nicht geparst werden kann
			//mainActivity
			//		.setAndShowErrorMsg(2, R.string.err_badge_not_parseable);
			e.printStackTrace();
		}
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
}