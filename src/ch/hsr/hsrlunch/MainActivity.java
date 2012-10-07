package ch.hsr.hsrlunch;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.util.TabPageAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.coboltforge.slidemenu.SlideMenu;
import com.coboltforge.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SherlockFragmentActivity implements OnSlideMenuItemClickListener {
	
    public static List<Offer> offerList;
	
    ViewPager mViewPager;
    TabPageAdapter mAdapter;
	
	private SlideMenu slidemenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        	
        init();
        
        FragmentPagerAdapter mAdapter = new TabPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdapter);
        
        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        
		slidemenu = (SlideMenu) findViewById(R.id.slideMenu);
		slidemenu.init(this, R.menu.slide, this, 333);
		
//		slidemenu.setAsShown(); 		
//		slidemenu.setHeaderImage(getResources().getDrawable(R.drawable.hsrlunch));
    }
    
	@Override
	public void onSlideMenuItemClick(int itemId) {
		switch(itemId) {
		case R.id.item_one:
			Toast.makeText(this, "Montag markiert", Toast.LENGTH_SHORT).show();
			break;
		case R.id.item_two:
			Toast.makeText(this, "Dienstag markiert", Toast.LENGTH_SHORT).show();
			break;
		case R.id.item_three:
			Toast.makeText(this, "Mittwoch markiert", Toast.LENGTH_SHORT).show();
			break;
		case R.id.item_four:
			Toast.makeText(this, "Donnerstag markiert", Toast.LENGTH_SHORT).show();
			break;
		case R.id.item_five:
			Toast.makeText(this, "Freitag markiert", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		System.out.println("Option Printed!");
		switch(item.getItemId()) {
		case android.R.id.home: // this is the app icon of the actionbar
			slidemenu.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void init() {
		Offer m1 = new Offer("Tages", "Tagesmenü",
        		"Fischtäbli \nSauce Tatar \nBlattspinat \nSalzkartoffeln", 8.00, "Montag 1. Oktober");
        Offer m2 = new Offer("Vegi", "Vegimenü",
        		"Gemüseteigtaschen \nTomatensauce \nSalzkartoffeln \nBuntersalat", 8.00, "Montag 1. Oktober");
        Offer m3 = new Offer("Woche", "Wochenhit",
        		"Schweinefilet im Speckmantel \nTomatensauce \nBuntersalat", 14.50, "Montag 1. Oktober");
        offerList = new ArrayList<Offer>();
        offerList.add(m1);
        offerList.add(m2);
        offerList.add(m3);
	} 
}
