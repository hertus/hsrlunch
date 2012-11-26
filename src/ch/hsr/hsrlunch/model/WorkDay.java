package ch.hsr.hsrlunch.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.SparseArray;

public class WorkDay {
	long date;
	SparseArray<Offer> offerList;
	DateFormat dateFormat;

	public WorkDay(long date, SparseArray<Offer> offerList) {
		this.date = date;
		this.offerList = offerList;
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

	public String getDateStringLong() {
		dateFormat = new SimpleDateFormat("EEEE dd.MM.yyyy", Locale.getDefault());
		return dateFormat.format(date);
	}
	
	public String getDateStringMedium() {
		dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return dateFormat.format(date);
	}

}
