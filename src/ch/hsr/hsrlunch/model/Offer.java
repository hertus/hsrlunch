package ch.hsr.hsrlunch.model;

public class Offer {

	int offerType = 1;
	String content = "";
	String price = "";

	public Offer(int offerType, String content, String price) {
		super();
		this.offerType = offerType;
		this.content = content;
		this.price = price;
	}

	public int getOfferType() {
		return offerType;
	}

	public void setOfferType(int offerType) {
		this.offerType = offerType;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPrice() {
		return price;
	}

	public String getMenuText() {
		return content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOfferTxt() {
		return content + "\n" + price;
	}

}
