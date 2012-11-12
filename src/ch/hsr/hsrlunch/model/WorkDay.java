package ch.hsr.hsrlunch.model;

import android.util.SparseArray;

public class WorkDay {
	int id;
	long date;
	SparseArray<Offer> offerList;

	public WorkDay(int id, long date, SparseArray<Offer> offerList) {
		this.id = id;
		this.date = date;
		this.offerList = offerList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

}
