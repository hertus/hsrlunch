package ch.hsr.hsrlunch.util;

import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import ch.hsr.hsrlunch.controller.OfferConstants;

public class OfferParser implements OfferConstants {

	final static String LINEBREAK = "BREAK_LN";

	String url;
	SparseArray<Pair<String, String>> offerArray;

	public OfferParser() {
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public SparseArray<Pair<String, String>> getOffers() {
		return offerArray;
	}

	/**
	 * @return Returns a SparseArray with Pairs, where FIRST = OfferContent and SECOND = OfferPrice
	 * @throws IOException
	 */
	public void parse() throws IOException {

		long timeStart = new Date().getTime();

		Log.w(OfferParser.class.getName(),
				"Start with JSOUP Connections");
		
		offerArray = new SparseArray<Pair<String, String>>();
		Document doc = Jsoup.connect(url).get();


		long timeEnd = new Date().getTime();
		Log.w(OfferParser.class.getName(),
				"End with URL Get: "
						+ (timeEnd - timeStart) / 1000 + " sec");
		
		// nach <br> parsen und diese mit einem Wert ersetzen, den wir danach
		// als Line Break verwenden
		doc = Jsoup.parse(doc.html().replaceAll("(?i)<br[^>]*>", LINEBREAK));
		Elements classElements = doc.getElementsByAttributeValue("class", "offer");

		timeEnd = new Date().getTime();
		Log.w(OfferParser.class.getName(),
				"End with HTML DOC: "
						+ (timeEnd - timeStart) / 1000 + " sec");
		
		for (Element element : classElements) {
			if (element.child(0) != null) {
				if (element.child(0).className().equals("offer-description")) {
					if (element.child(0).text().equals(OFFER_DAILY_TITLE)) {
						offerArray.put(
								OFFER_DAILY,
								new Pair<String, String>(element.child(1)
										.text()
										.replaceAll((LINEBREAK + "[ ]"), "\n"),
										element.child(2).text()));
					}
					if (element.child(0).text().equals(OFFER_VEGI_TITLE)) {
						offerArray.put(
								OFFER_VEGI,
								new Pair<String, String>(element.child(1)
										.text()
										.replaceAll((LINEBREAK + "[ ]"), "\n"),
										element.child(2).text()));
					}
					if (element.child(0).text().equals(OFFER_WEEK_TITLE)) {
						offerArray.put(
								OFFER_WEEK,
								new Pair<String, String>(element.child(1)
										.text()
										.replaceAll((LINEBREAK + "[ ]"), "\n"),
										element.child(2).text()));
					}
				}
			}
		}
		

		timeEnd = new Date().getTime();
		Log.w(OfferParser.class.getName(),
				"End with JSOUP: "
						+ (timeEnd - timeStart) / 1000 + " sec");
	}
}