package ch.hsr.hsrlunch.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.ui.OfferFragment;

public class TabPageAdapter extends FragmentPagerAdapter {
	
	SparseArray<OfferFragment> fragmentList = new SparseArray<OfferFragment>();

	public TabPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
    public int getCount() {
        return MainActivity.tabTitleList.size();
    }

    @Override
    public Fragment getItem(int position) {    	
    	if(fragmentList.get(position) == null){
    	
    	OfferFragment frag = OfferFragment.newInstance(position);
    	
    	fragmentList.put(position, frag);
    	return frag;
    	}else
    		return fragmentList.get(position);
    }
    @Override
	public CharSequence getPageTitle(int position) {
    	return MainActivity.tabTitleList.get(position).toUpperCase();
    }
    @Override
    public void notifyDataSetChanged() {
    	System.out.println("notifyDataChanged");
    	for(int i= 0; 1<= 3; i++){
    		fragmentList.get(i).updateValues();
    	}
    }

	public SparseArray<OfferFragment> getFragmentList() {
		return fragmentList;
	}
  

}
