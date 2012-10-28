package ch.hsr.hsrlunch.model;

import ch.hsr.hsrlunch.MainActivity;


public class Offer {
	
	int offerType = 0;
	String content = "";
	String price = "";
	String title = "";
	
	public Offer(int offerType, String content,
			String price) {
		super();
		this.offerType = offerType;
		this.content = content;
		this.price = price;
		setTitle(offerType);
	}
	private void setTitle(int type) {
		switch(type){
		case 0: title = MainActivity.OFFER_DAILY_TITLE;
		break;
		case 1: title = MainActivity.OFFER_VEGI_TITLE;
		break;
		case 2: title = MainActivity.OFFER_WEEK_TITLE;
		break;
		}
	}
	public String getTitle(){
		return title;
	}
	public int getOfferType() {
		return offerType;
	}
	public void setOfferType(int offerType) {
		this.offerType = offerType;
		setTitle(offerType);
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPrice(){
		return price;
	}
	public String getMenuText(){
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
