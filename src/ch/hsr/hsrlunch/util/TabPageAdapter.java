package ch.hsr.hsrlunch.util;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.ui.OfferFragment;

public class TabPageAdapter extends FragmentPagerAdapter{

	private List<String> tabTitles;
	private WorkDay day;
	FragmentManager fm;

	SparseArray<OfferFragment> fragmentList;

	public TabPageAdapter(Activity mainActivity,FragmentManager fm, WorkDay day) {
		
		super(fm);
		this.fm = fm;
		fragmentList = new SparseArray<OfferFragment>();
		tabTitles = Arrays.asList(mainActivity.getResources()
				.getStringArray(R.array.tabTitles));
		
		this.day = day;
	}

	@Override
	public int getCount() {
		return tabTitles.size();
	}

	@Override
	public Fragment getItem(int position) {
		if (fragmentList.get(position) == null) {

			OfferFragment frag = OfferFragment.newInstance(position);
			frag.setOffer(day.getOfferList().get(position));
			
			fragmentList.put(position, frag);
			return frag;
		} else
			return fragmentList.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabTitles.get(position);
	}

	/*
	 * hier werden die Fragments mit den aktuellen werten aktualisiert
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();	
		
		for (int i = 0; i < fragmentList.size(); i++) {
			OfferFragment frag = fragmentList.get(i);
			frag.setOffer(day.getOfferList().get(i));
			frag.updateValues();
		}
		
	}

	public SparseArray<OfferFragment> getFragmentList() {
		return fragmentList;
	}

	public WorkDay getDay() {
		return day;
	}

	public void setDay(WorkDay day) {
		this.day = day;
		notifyDataSetChanged();
	}
	
}
