package ch.hsr.hsrlunch.util;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.ui.OfferFragment;

public class TabPageAdapter extends FragmentStatePagerAdapter{

	private List<String> tabTitles;
	


	SparseArray<OfferFragment> fragmentList;

	public TabPageAdapter(Activity mainActivity,FragmentManager fm) {
		
		super(fm);
		fragmentList = new SparseArray<OfferFragment>();
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
	public void destroyItem(View collection, int position, Object o) {
	
		
	    OfferFragment fragment = (OfferFragment)o;
		((ViewPager) collection).removeViewAt(position);
	    fragmentList.remove(position);
	    fragment = null;
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
		
		System.out.println("fragmentlistsize: " + fragmentList.size());
		for (int i = 0; i < fragmentList.size(); i++) {
			fragmentList.get(i).updateValues();
		}
		//super.notifyDataSetChanged();	
	}

	public SparseArray<OfferFragment> getFragmentList() {
		return fragmentList;
	}
}
