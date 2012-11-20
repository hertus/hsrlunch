package ch.hsr.hsrlunch.controller;

import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Pair;
import android.util.SparseArray;
import ch.hsr.hsrlunch.util.XMLParser;

public class OfferUpdater implements OfferConstants {
	private SparseArray<SparseArray<Pair<String, String>>> updatedArray;

	
	/**
	 * @return Returns a SparseArray with a SparseArraye coming from the
	 *         OfferParser. This SparseArray contains Pairs, where FIRST =
	 *         OfferContent and SECOND = OfferPrice
	 * @see API SDK Version lower then 10 has no ThreadPoolExecutor for multiple
	 *      AsyncTasks
	 */
	@SuppressLint("NewApi")
	public SparseArray<SparseArray<Pair<String, String>>> updateAndGetOffersArray() {

		updatedArray = new SparseArray<SparseArray<Pair<String, String>>>();
		try {
			// Wait and get Result of AsyncTask
			updatedArray = new SiteParserTask().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return updatedArray;
	}

	private class SiteParserTask
			extends
			AsyncTask<Integer, Void, SparseArray<SparseArray<Pair<String, String>>>> {

		@Override
		protected SparseArray<SparseArray<Pair<String, String>>> doInBackground(
				Integer... day) {
			SparseArray<SparseArray<Pair<String, String>>> content = new SparseArray<SparseArray<Pair<String, String>>>();
			XMLParser parser = new XMLParser();

			try {
				content = parser.parseOffers();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return content;
		}

	}

}
