package ch.hsr.hsrlunch.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.ui.OfferFragment;

public class TabPageAdapter extends FragmentPagerAdapter {

	public TabPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
    public int getCount() {
        return MainActivity.tabTitleList.size();
    }

    @Override
    public Fragment getItem(int position) {
    	
        return OfferFragment.newInstance(position);
    }
    @Override
	public CharSequence getPageTitle(int position) {
    	return MainActivity.tabTitleList.get(position).toUpperCase();
    }

}
