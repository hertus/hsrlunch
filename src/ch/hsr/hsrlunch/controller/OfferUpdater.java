package ch.hsr.hsrlunch.controller;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import ch.hsr.hsrlunch.util.OfferParser;

public class OfferUpdater implements OfferConstants {
	private CountDownLatch endSignal;
	private SparseArray<SparseArray<Pair<String, String>>> returnArray;

	public OfferUpdater() {
	}

	/**
	 * @return Returns a SparseArray with a SparseArraye coming from the
	 *         OfferParser. This SparseArray contains Pairs, where FIRST =
	 *         OfferContent and SECOND = OfferPrice
	 * @see API SDK Version lower then 10 has no ThreadPoolExecutor for multiple
	 *      AsyncTasks
	 */
	@SuppressLint("NewApi")
	public SparseArray<SparseArray<Pair<String, String>>> updateAndGetOffersArray() {

		returnArray = new SparseArray<SparseArray<Pair<String, String>>>();
		endSignal = new CountDownLatch(OFFER_FRIDAY);

		// Create and Execute AsyncTask for MO-FR
		try {
			for (int i = OFFER_MONDAY; i <= OFFER_FRIDAY; i++) {
				if (Build.VERSION.SDK_INT >= 11) {
					new SiteParserTask().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, i);

				} else {
					new SiteParserTask().execute(i);
				}
			}

			Log.i(OfferUpdater.class.getName(),
					"HSRLunch - OfferParser: Begin waiting for Parser AsyncTasks");
			long timeStart = new Date().getTime();
			endSignal.await();
			long timeEnd = new Date().getTime();
			Log.i(OfferUpdater.class.getName(),
					"HSRLunch - OfferParser: End with waiting for Parser AsyncTasks, time: "
							+ (timeEnd - timeStart) / 1000 + " sec");

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return returnArray;
	}

	private synchronized void setOfferContent(int day,
			SparseArray<Pair<String, String>> offers) {
		returnArray.append(day, offers);
	}

	private class SiteParserTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... day) {

			OfferParser parser = new OfferParser();

			try {
				parser.setUrl("http://hochschule-rapperswil.sv-group.ch/de/menuplan.html?addGP%5Bweekday%5D="
						+ day[0] + "&addGP%5Bweekmod%5D=0");
				parser.parse();

			} catch (Exception e) {
				e.printStackTrace();
			}
			setOfferContent(day[0], parser.getOffers());
			endSignal.countDown();
			return null;
		}

	}

}
