package ch.hsr.hsrlunch;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import ch.hsr.hsrlunch.controller.WeekDataSource;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.ui.SlideMenuHSR;
import ch.hsr.hsrlunch.util.SlideMenuInterface.OnSlideMenuItemClickListener;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.TabPageAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SherlockFragmentActivity implements
		OnSlideMenuItemClickListener {

	public final static String OFFER_DAILY_TITLE = "Tagesteller";
	public final static String OFFER_VEGI_TITLE = "Vegetarisch";
	public final static String OFFER_WEEK_TITLE = "Wochen-Hit";

	private List<Offer> offerList;
	public static List<WorkDay> dayList;
	public static List<String> tabTitleList;

	public static WorkDay selectedDay;
	public static Offer selectedOffer;

	ViewPager mViewPager;
	TabPageAdapter mAdapter;
	ShareActionProvider provider;
	long WEEK_IN_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;

	private SlideMenuHSR slidemenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setHomeButtonEnabled(true);

		init();

		FragmentPagerAdapter mAdapter = new TabPageAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mAdapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);

		slidemenu = (SlideMenuHSR) findViewById(R.id.slideMenu);
		slidemenu.init(this, R.menu.slide, this, 333);
		
		/* Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectedOffer = selectedDay.getOfferList().get(position);
                provider.setShareIntent(getDefaultShareIntent());
            }
        };
        /** Setting the pageChange listner to the viewPager */
        indicator.setOnPageChangeListener(pageChangeListener);

		// slidemenu.setAsShown();
		// slidemenu.setHeaderImage(getResources().getDrawable(R.drawable.hsrlunch));
	}

	@Override
	public void onSlideMenuItemClick(int itemId) {
		switch (itemId) {
		case R.id.item_one:
			Toast.makeText(this, "Montag markiert", Toast.LENGTH_SHORT).show();
			selectedDay = dayList.get(0);
			break;
		case R.id.item_two:
			Toast.makeText(this, "Dienstag markiert", Toast.LENGTH_SHORT)
					.show();
			selectedDay = dayList.get(1);
			break;
		case R.id.item_three:
			Toast.makeText(this, "Mittwoch markiert", Toast.LENGTH_SHORT)
					.show();
			selectedDay = dayList.get(2);
			break;
		case R.id.item_four:
			Toast.makeText(this, "Donnerstag markiert", Toast.LENGTH_SHORT)
					.show();
			selectedDay = dayList.get(3);
			break;
		case R.id.item_five:
			Toast.makeText(this, "Freitag markiert", Toast.LENGTH_SHORT).show();
			selectedDay = dayList.get(4);
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		System.out.println("Option Printed!");
		switch (item.getItemId()) {
		case android.R.id.home: // this is the app icon of the actionbar
			slidemenu.show();
			break;
		}
		System.out.println(selectedDay.getDate());
		System.out.println(mAdapter);
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
		return super.onOptionsItemSelected(item);
	}

	/*
	 * statisches Füllen der Daten solange zugriff auch DB noch nicht
	 * implementiert ist
	 */
	private void init() {

		Offer m1 = new Offer(0,
				"Fischtäbli\nSauce Tatar\nBlattspinat\nSalzkartoffeln",
				"INT 8.00 EXT 10.60");
		Offer m2 = new Offer(1,
				"Gemüseteigtaschen\nTomatensauce\nSalzkartoffeln\nBuntersalat",
				"INT 8.00 EXT 10.60");
		Offer m3 = new Offer(2,
				"Schweinefilet im Speckmantel\nTomatensauce\nBuntersalat",
				"INT 14.50 EXT 15.50");
		offerList = new ArrayList<Offer>();
		offerList.add(m1);
		offerList.add(m2);
		offerList.add(m3);

		// achtung monat bei GregorianCalendar liegt zwischen 0 und 11!
		GregorianCalendar cal1 = new GregorianCalendar(2012, 9, 22);
		GregorianCalendar cal2 = new GregorianCalendar(2012, 9, 23);
		GregorianCalendar cal3 = new GregorianCalendar(2012, 9, 24);
		GregorianCalendar cal4 = new GregorianCalendar(2012, 9, 25);
		GregorianCalendar cal5 = new GregorianCalendar(2012, 9, 26);

		dayList = new ArrayList<WorkDay>();
		dayList.add(new WorkDay(0, new Date(cal1.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(1, new Date(cal2.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(2, new Date(cal3.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(3, new Date(cal4.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(4, new Date(cal5.getTimeInMillis()), offerList));

		tabTitleList = new ArrayList<String>(3);
		tabTitleList.add("tages");
		tabTitleList.add("vegi");
		tabTitleList.add("woche");

		GregorianCalendar cal = new GregorianCalendar();
		int selectedDayIndex = 0;

		/*
		 * hier wird der aktuelle wochentag gesetzt, Calender.DAY_OF_WEEK gibt
		 * für den Sontag 1 zurück am wochenende wird der Freitag gesetzt
		 */
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 1:
		case 6:
		case 7:
			selectedDayIndex = 4;
			break;
		case 2:
			selectedDayIndex = 0;
			break;
		case 3:
			selectedDayIndex = 1;
			break;
		case 4:
			selectedDayIndex = 2;
			break;
		case 5:
			selectedDayIndex = 3;
			break;
		}
		// selectedDayIndex = (+5) % 7 > 4 ? 4: cal.get(Calendar.DAY_OF_WEEK)+5
		// % 7;
		selectedDay = dayList.get(selectedDayIndex);
		selectedOffer = selectedDay.getOfferList().get(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar_menu, menu);

		// menu.add("refresh").setIcon(R.drawable.ic_menu_refresh).setShowAsAction();
		// menu.add("share").setIcon(R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		MenuItem item = menu.findItem(R.id.menu_share);
		provider = (ShareActionProvider) item
				.getActionProvider();
		provider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		
         provider.setShareIntent(getDefaultShareIntent());
		return true;
	}

	private static Intent getDefaultShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "HSR Menu @ "+selectedDay.getDate()+"-"+selectedOffer.getTitle());
        intent.putExtra(android.content.Intent.EXTRA_TEXT, selectedOffer.getOfferTxt() );
        return intent;
    }

	public boolean DBUpdateNeeded() {
		long dbage = new WeekDataSource(new DBOpenHelper(this))
				.getWeekLastUpdate();
		long actday = new Date().getTime();
		long difference = 4 * 24 * 60 * 60 * 1000; // Weil der 1.1.1970 ein
													// Donnerstag war

		if (actday - ((actday + difference) % WEEK_IN_MILLISECONDS) > dbage)
			return true; // dbage ist aus letzer Woche
		return false; // dbage ist neuer als der letzte Montag

	}

}