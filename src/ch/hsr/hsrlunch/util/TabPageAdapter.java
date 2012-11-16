package ch.hsr.hsrlunch.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import ch.hsr.hsrlunch.ui.OfferFragment;

public class TabPageAdapter extends FragmentPagerAdapter {

	private static final String[] TABTITLES = { "tages", "vegi", "woche" };

	SparseArray<OfferFragment> fragmentList = new SparseArray<OfferFragment>();

	public TabPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return TABTITLES.length;
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
		return TABTITLES[position].toUpperCase();
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
