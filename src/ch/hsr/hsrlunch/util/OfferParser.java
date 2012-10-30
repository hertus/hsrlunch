package ch.hsr.hsrlunch.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ch.hsr.hsrlunch.controller.OfferConstants;

import android.util.SparseArray;

public class OfferParser implements OfferConstants {

	final static String LINEBREAK = "BREAK_LN";

	String url;
	SparseArray<String> offerArray;

	public OfferParser() {
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public SparseArray<String> getOffers() {
		return offerArray;
	}

	public void parse() throws IOException {
		offerArray = new SparseArray<String>();
		Document doc = Jsoup.connect(url).get();

		// nach <br> parsen und diese mit einem Wert ersetzen, den wir danach
		// als Line Break verwenden
		doc = Jsoup.parse(doc.html().replaceAll("(?i)<br[^>]*>", LINEBREAK));
		Elements elem = doc.getElementsByAttributeValue("class", "offer");

		for (Element element : elem) {
			if (element.child(0) != null) {
				if (element.child(0).className().equals("offer-description")) {
					if (element.child(0).text().equals(OFFER_DAILY_TITLE)) {
						toOfferArray(element, OFFER_DAILY, OFFER_DAILY_PRICE);
					}
					if (element.child(0).text().equals(OFFER_VEGI_TITLE)) {
						toOfferArray(element, OFFER_VEGI, OFFER_VEGI_PRICE);
					}
					if (element.child(0).text().equals(OFFER_WEEK_TITLE)) {
						toOfferArray(element, OFFER_WEEK, OFFER_WEEK_PRICE);
					}
				}
			}
		}
	}

	private void toOfferArray(Element element, int offer, int price) {
		offerArray.put(offer,
				element.child(1).text().replaceAll((LINEBREAK + "[ ]"), "\n"));
		offerArray.put(price, element.child(2).text());
	}
}