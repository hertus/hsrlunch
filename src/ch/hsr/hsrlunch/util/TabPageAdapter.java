package ch.hsr.hsrlunch.util;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.ui.OfferFragment;

public class TabPageAdapter extends FragmentStatePagerAdapter{

	private List<String> tabTitles;
	


	SparseArray<OfferFragment> fragmentList = new SparseArray<OfferFragment>();

	public TabPageAdapter(Activity mainActivity,FragmentManager fm) {
		
		super(fm);
		tabTitles = Arrays.asList(mainActivity.getResources()
				.getStringArray(R.array.tabTitles));
	}

	@Override
	public int getCount() {
		return tabTitles.size();
	}

	@Override
	public Fragment getItem(int position) {
		if (fragmentList.get(position) == null) {

			OfferFragment frag = OfferFragment.newInstance(position);
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
		System.out.println("fragmentlistsize: " + fragmentList.size());
		for (int i = 0; i < fragmentList.size(); i++) {
			fragmentList.get(i).updateValues();
		}
	}

	public SparseArray<OfferFragment> getFragmentList() {
		return fragmentList;
	}
}
