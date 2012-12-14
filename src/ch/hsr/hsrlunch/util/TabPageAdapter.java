package ch.hsr.hsrlunch.util;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.ui.OfferFragment;

public class TabPageAdapter extends FragmentPagerAdapter {

	private List<String> tabTitles;
	private WorkDay day;
	FragmentManager fm;

	public TabPageAdapter(Activity mainActivity, FragmentManager fm, WorkDay day) {

		super(fm);
		this.fm = fm;
		tabTitles = Arrays.asList(mainActivity.getResources().getStringArray(
				R.array.tabTitles));

		this.day = day;
	}

	@Override
	public int getCount() {
		return tabTitles.size();
	}

	@Override
	public Fragment getItem(int position) {

		OfferFragment frag = new OfferFragment();
		frag.setDayString(day.getDateStringLong());
		frag.setOffer(day.getOfferList().get(position));
		return frag;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabTitles.get(position);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

		for (int i = 0; i < getCount(); i++) {
			OfferFragment f = (OfferFragment) fm
					.findFragmentByTag(getFragmentTag(i));
			if (f != null) {
				f.setDayString(day.getDateStringLong());
				f.setOffer(day.getOfferList().get(i));
				f.updateValues();
			}
		}
	}

	/**
	 * TabPageAdaper und ViewPager erstellen folgende Fragment-Tags
	 * android:switcher:"viewpagerID"+pos diese Tags benoetigt man um das
	 * Fragment zu identifizieren
	 * 
	 * @param pos
	 * @return tag of Fragment
	 */
	private String getFragmentTag(int pos) {
		return "android:switcher:" + R.id.viewpager + ":" + pos;
	}

	public void setDay(WorkDay day) {
		this.day = day;
		notifyDataSetChanged();
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

}
