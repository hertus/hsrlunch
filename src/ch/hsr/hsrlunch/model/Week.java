package ch.hsr.hsrlunch.model;

import java.text.DateFormat;

import android.util.SparseArray;

public class Week {
	int id;
	long lastUpdate;
	SparseArray<WorkDay> dayList;
	DateFormat dateFormat;

	public Week(int id, long lastUpdate, SparseArray<WorkDay> dayList) {
		this.id = id;
		this.lastUpdate = lastUpdate;
		this.dayList = dayList;
		dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
