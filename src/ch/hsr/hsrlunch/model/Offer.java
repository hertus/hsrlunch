package ch.hsr.hsrlunch.model;

import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.controller.OfferConstants;

public class Offer {
	
	int offerType;	
	String title;
	String content;
	String price;
	
	public Offer(int offerType, String content,
			String price) {
		super();
		this.offerType = offerType;
		this.content = content;
		this.price = price;
		switch(this.offerType){
		case 0:
			title = MainActivity.OFFER_DAILY_TITLE;
		case 1:
			title = MainActivity.OFFER_VEGI_TITLE;
		case 2:
			title = MainActivity.OFFER_WEEK_TITLE;
		}
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
	public String getPrice(){
		return price;
	}
	public String getMenuText(){
		return content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
