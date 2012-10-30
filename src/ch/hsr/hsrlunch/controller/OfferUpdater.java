package ch.hsr.hsrlunch.controller;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import ch.hsr.hsrlunch.util.OfferParser;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.controller.OfferDataSource;

public class OfferUpdater implements OfferConstants {
	private OfferDataSource offerData;
	SparseArray<SparseArray<String>> offerArrayList = new SparseArray<SparseArray<String>>();

	public OfferUpdater(DBOpenHelper dbHelper) {
		offerData = new OfferDataSource(dbHelper);
	}

	@SuppressLint("NewApi")
	public void updateAllOffer() {

		Log.w(DBOpenHelper.class.getName(),
				"BEGIN WITH UPDATE ALL OFFER PARSING!");

		// Try Parallel AsyncTask connecting Websites and Parse
		for (int i = OFFER_MONDAY; i <= OFFER_FRIDAY; i++) {
			if (Build.VERSION.SDK_INT >= 11) {
				new MultipleSiteParserTask(i)
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new MultipleSiteParserTask(i).execute();
			}
		}

		Log.w(DBOpenHelper.class.getName(),
				"END WITH UPDATE ALL OFFER PARSING!");
	}

	private synchronized void setOfferContent(int day) {
		offerData.open();
		try {
			offerData.setOfferContent(offerArrayList.get(day).get(OFFER_DAILY),
					OFFER_DAILY, day);
			offerData.setOfferPrice(
					offerArrayList.get(day).get(OFFER_DAILY_PRICE),
					OFFER_DAILY, day);
			offerData.setOfferContent(offerArrayList.get(day).get(OFFER_VEGI),
					OFFER_VEGI, day);
			offerData.setOfferPrice(
					offerArrayList.get(day).get(OFFER_VEGI_PRICE), OFFER_VEGI,
					day);
			offerData.setOfferContent(offerArrayList.get(day).get(OFFER_WEEK),
					OFFER_WEEK, day);
			offerData.setOfferPrice(
					offerArrayList.get(day).get(OFFER_WEEK_PRICE), OFFER_WEEK,
					day);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			offerData.close();
		}

	}

	private class MultipleSiteParserTask extends AsyncTask<Void, Void, Void> {
		int day;

		public MultipleSiteParserTask(int day) {
			this.day = day;
		}

		@Override
		protected Void doInBackground(Void... params) {

			OfferParser parser = new OfferParser();

			try {
				parser.setUrl("http://hochschule-rapperswil.sv-group.ch/de/menuplan.html?addGP%5Bweekday%5D="
						+ day + "&addGP%5Bweekmod%5D=0");
				parser.parse();
				offerArrayList.append(day, parser.getOffers());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setOfferContent(day);
		}

	}

}
