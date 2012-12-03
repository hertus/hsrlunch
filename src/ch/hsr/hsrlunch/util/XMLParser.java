package ch.hsr.hsrlunch.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import ch.hsr.hsrlunch.controller.OfferConstants;

public class XMLParser implements OfferConstants {

	private final String branch = "7700";
	private final String auth = "j1schmid_hsr.ch@aejcg45QlzKGtirbKJE79dQrH3QWkvxanEGwQojFcKmK5mw4ZyZTqaQet5wf";
	private final String baseurl = "http://micro.sv-group.com/typo3conf/ext/netv_svg_menu/menu_xmlexp/branchstate.xml.php?branch="
			+ branch + "&authstring=" + auth;

	private SparseArray<SparseArray<Pair<String, String>>> offerList;

	/**
	 * @return Returns a SparseArray within an SparseArray, containing a Pair in
	 *         where is .first = OffertContent and .second = OfferPrice if
	 *         Orderprice = empty then String is "EMPTY". SparsArray from Day 0
	 *         - 4 and from Offers 0 - 3. Offer 0 = DAILY, 1 = VEGI, 2 = WEEK
	 * @throws UpdateParserException
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws UnsupportedEncodingException
	 */
	public SparseArray<SparseArray<Pair<String, String>>> parseOffers()
			throws UpdateParserException {
		Log.d("System Encoding", System.getProperty("file.encoding").toString());

		String menuUrl = getMenuUrl();
		String xml = getXMLfromURL(menuUrl);
		Document doc = getDomElement(xml);

		if (doc != null) {
			offerList = parseOfferContents(doc);
		} else {
			throw new UpdateParserException("couldn't get DOM Document");
		}
		return offerList;
	}

	private SparseArray<SparseArray<Pair<String, String>>> parseOfferContents(
			Document domDoc) throws UpdateParserException {

		String offerContent;
		String priceInt;
		String priceExt;

		offerList = new SparseArray<SparseArray<Pair<String, String>>>();
		NodeList dayList = domDoc.getElementsByTagName("day");

		if (dayList != null) {
			// Iterate all Days
			for (int i = 0; i < dayList.getLength(); i++) {

				if (dayList.item(i) != null) {
					Element dayElement = (Element) dayList.item(i);
					int dayId = Integer.parseInt(dayElement.getAttributes()
							.item(0).getTextContent());

					// Change dayIDs to Business Logic
					switch (dayId) {
					case 1:
						dayId = OFFER_MONDAY;
						break;
					case 2:
						dayId = OFFER_TUESDAY;
						break;
					case 3:
						dayId = OFFER_WEDNESDAY;
						break;
					case 4:
						dayId = OFFER_THURSDAY;
						break;
					case 5:
						dayId = OFFER_FRIDAY;
						break;

					default:
						break;
					}

					// Prepare Array
					offerList.put(dayId,
							new SparseArray<Pair<String, String>>());

					NodeList menuList = dayElement.getElementsByTagName("menu");

					// Iterate all Menus in Days
					for (int j = 0; j < menuList.getLength(); j++) {
						offerContent = "";
						priceInt = "";
						priceExt = "";

						Element menuElement = (Element) menuList.item(j);
						int menuId = Integer.parseInt(menuElement
								.getAttributes().getNamedItem("id")
								.getTextContent());

						// Set menuIDs to Business Logic
						switch (menuId) {
						case 1:
							menuId = OFFER_DAILY;
							break;
						case 2:
							menuId = OFFER_VEGI;
							break;
						case 4:
							menuId = OFFER_WEEK;
							break;
						default:
							break;
						}

						NodeList descList = menuElement
								.getElementsByTagName("description");
						if (descList.item(0) != null) {
							offerContent = descList.item(0).getTextContent()
									.replaceAll("\n\n\n\n", "\n\n").replaceAll("\n\n\n", "\n\n");
						}

						NodeList priceList = menuElement
								.getElementsByTagName("value");
						if (priceList.item(0).getTextContent() != null) {
							priceInt = priceList.item(0).getTextContent();
						}
						if (priceList.item(1).getTextContent() != null) {
							priceExt = priceList.item(1).getTextContent();
						}

						// Set Prices when there is no Price online
						if (priceInt.equals("") || priceExt.equals("")) {
							switch (menuId) {
							case OFFER_DAILY:
								offerList.get(dayId).put(
										menuId,
										new Pair<String, String>(offerContent,
												PRICE_STANDARD));
								break;

							case OFFER_VEGI:
								offerList.get(dayId).put(
										menuId,
										new Pair<String, String>(offerContent,
												PRICE_STANDARD));
								break;

							case OFFER_WEEK:
								offerList.get(dayId).put(
										menuId,
										new Pair<String, String>(offerContent,
												PRICE_STANDARD_WEEK));
								break;

							default:
								break;
							}

						} else {
							offerList.get(dayId).put(
									menuId,
									new Pair<String, String>(offerContent,
											"INT " + priceInt + " / EXT "
													+ priceExt));
						}
					}

				} else {
					throw new UpdateParserException(
							"parsing item in dayList was null");
				}
			}
		} else {
			throw new UpdateParserException("parsing dayList was null");
		}
		return offerList;
	}

	private String getMenuUrl() throws UpdateParserException {
		String xml = getXMLfromURL(baseurl);
		Document doc = getDomElement(xml);
		if (doc != null) {
			NodeList nodeList = doc.getElementsByTagName("exporturl");
			if (nodeList.item(0) != null) {
				return nodeList.item(0).getTextContent();
			} else {
				throw new UpdateParserException(
						"nodeList from exporturl was null - no exporturl available?");
			}
		} else {
			throw new UpdateParserException("no DOM Document");
		}
	}

	public String getXMLfromURL(String url) throws UpdateParserException {
		String xml = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			throw new UpdateParserException("UnsupportedEncoding: "
					+ e.getMessage());
		} catch (ClientProtocolException e) {
			throw new UpdateParserException("ClientProtocoll Error: "
					+ e.getMessage());
		} catch (IOException e) {
			throw new UpdateParserException("IOException: " + e.getMessage());
		}
		return xml;
	}

	public Document getDomElement(String xml) throws UpdateParserException {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			if (xml != null) {
				dbf.setCoalescing(true);
				DocumentBuilder db = dbf.newDocumentBuilder();

				InputSource is = new InputSource(new StringReader(xml));
				is.setEncoding("UTF-8");

				doc = db.parse(is);
			} else {
				throw new UpdateParserException(
						"XML was null - could not get Data from HTTP");
			}
		} catch (ParserConfigurationException e) {
			throw new UpdateParserException("Error in ParserConfiguration: "
					+ e.getMessage());
		} catch (SAXException e) {
			throw new UpdateParserException("Error in SAX: " + e.getMessage());
		} catch (IOException e) {
			throw new UpdateParserException("IOException: " + e.getMessage());
		}
		return doc;
	}
}