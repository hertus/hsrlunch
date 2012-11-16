package ch.hsr.hsrlunch.model;

import java.text.DateFormat;

import android.util.SparseArray;

public class Week {
	long lastUpdate;
	SparseArray<WorkDay> dayList;
	DateFormat dateFormat;

	public Week(long lastUpdate, SparseArray<WorkDay> dayList) {
		this.lastUpdate = lastUpdate;
		this.dayList = dayList;
		dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public SparseArray<WorkDay> getDayList() {
		return dayList;
	}

	public void setDayList(SparseArray<WorkDay> dayList) {
		this.dayList = dayList;
	}

	public String getLastUpdateString() {
		return dateFormat.format(lastUpdate);
	}
}
