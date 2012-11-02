package ch.hsr.hsrlunch.model;

import java.text.DateFormat;
import java.util.Date;

public class Badge {
	double amount;
	Date lastUpdate;
	DateFormat df;
	
	public Badge(double amount, Date lastUpdate) {
		super();
		this.amount = amount;
		this.lastUpdate = lastUpdate;
		df = DateFormat.getDateInstance( DateFormat.SHORT );
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getLastUpdate() {
		return df.format(lastUpdate);
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	

}
