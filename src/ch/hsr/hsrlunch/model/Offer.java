package ch.hsr.hsrlunch.model;

public class Offer {
	int offerType;
	String content;
	String price;

	public Offer(int offerType, String content, String price) {
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getOfferAndPrice(){
		return content + "\n" + price;
	}
}
