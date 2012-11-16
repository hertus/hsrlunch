package ch.hsr.hsrlunch.model;

import java.text.DateFormat;

import android.util.SparseArray;

public class WorkDay {
	long date;
	SparseArray<Offer> offerList;
	DateFormat dateFormat;

	public WorkDay(long date, SparseArray<Offer> offerList) {
		this.date = date;
		this.offerList = offerList;
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public SparseArray<Offer> getOfferList() {
		return offerList;
	}

	public void setOfferList(SparseArray<Offer> offerList) {
		this.offerList = offerList;
	}

	public String getDateString() {
		return dateFormat.format(date);
	}

}
