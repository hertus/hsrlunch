package ch.hsr.hsrlunch.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class WorkDay {

	int id;
	Date date;
	List<Offer> offerList;
	DateFormat df;


	public WorkDay(int id, Date date, List<Offer> offerList) {
		super();
		this.id = id;
		this.date = date;
		this.offerList = offerList;
		
		df = DateFormat.getDateInstance( DateFormat.FULL );
		   
	}
	public String getDate() {
		return df.format(date);
	}
	public List<Offer> getOfferList() {
		return offerList;
	}
	
	
}
