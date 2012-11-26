package ch.hsr.hsrlunch.model;

import java.text.DateFormat;

public class Badge {
	double amount;
	long lastUpdate;
	DateFormat dateFormat;

	public Badge(double amount, long lastUpdate) {
		this.amount = amount;
		this.lastUpdate = lastUpdate;
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getLastUpdateString() {
		return dateFormat.format(lastUpdate);
	}

}
