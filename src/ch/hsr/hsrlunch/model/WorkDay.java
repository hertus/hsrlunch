package ch.hsr.hsrlunch.model;

import java.util.Date;
import java.util.List;

public class WorkDay {

	int id;


	Date date;
	List<Offer> offerList;

	public WorkDay(int id, Date date, List<Offer> offerList) {
		super();
		this.id = id;
		this.date = date;
		this.offerList = offerList;
	}
	public Date getDate() {
		return date;
	}
	public List<Offer> getOfferList() {
		return offerList;
	}
	
	
}
