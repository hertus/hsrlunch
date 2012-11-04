package ch.hsr.hsrlunch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.hsrlunch.controller.OfferUpdater;
import ch.hsr.hsrlunch.controller.WeekDataSource;
import ch.hsr.hsrlunch.model.Badge;
import ch.hsr.hsrlunch.model.MenuViewAdapter;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.ui.CustomMenuView;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.TabPageAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SherlockFragmentActivity{

	private List<Offer> offerList;
	public static List<WorkDay> dayList;
	public static List<String> tabTitleList;

	public static WorkDay selectedDay;
	public static Offer selectedOffer;
	Badge badge;

	ViewPager mViewPager;
	private MenuDrawerManager mMenuDrawer;
	TabPageAdapter mAdapter;
	ShareActionProvider provider;
	long WEEK_IN_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;

	private DBOpenHelper dbHelper;
	private OfferUpdater offerUpdater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mMenuDrawer = new MenuDrawerManager(this, MenuDrawer.MENU_DRAG_CONTENT);
		CustomMenuView MenuView = new CustomMenuView(this);
		MenuViewAdapter mvAdapter = new MenuViewAdapter(this);
		MenuView.setAdapter(mvAdapter);
		
		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(MenuView);
		
		
		
		

		getSupportActionBar().setHomeButtonEnabled(true);

		init();

		FragmentPagerAdapter mAdapter = new TabPageAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mAdapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);



		/* Defining a listener for pageChange */
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
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

		dbHelper = new DBOpenHelper(this);
		offerUpdater = new OfferUpdater(dbHelper);
		
		TextView badgeAmount = (TextView) findViewById(R.id.amount);
		badgeAmount.setText(badge.getAmount()+" CHF");
		TextView badgeLastUpdate = (TextView) findViewById(R.id.lastUpdate);
		badgeLastUpdate.setText(badge.getLastUpdate());
		
	}



	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		System.out.println("Option Printed!");
		switch (item.getItemId()) {
		case android.R.id.home: // this is the app icon of the actionbar
			mMenuDrawer.toggleMenu();
			break;
		}
		System.out.println(selectedDay.getDate());
		System.out.println(mAdapter);
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
		return super.onOptionsItemSelected(item);
	}

	/*
	 * statisches Füllen der Daten solange zugriff auf DB noch nicht
	 * implementiert ist
	 */
	private void init() {
		
		badge = new Badge(999.99, new Date());

		Offer m1 = new Offer(0,
				"Fischtäbli\nSauce Tatar\nBlattspinat\nSalzkartoffeln",
				"INT 8.00 EXT 10.60");
		Offer m2 = new Offer(
				1,
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
		provider = (ShareActionProvider) item.getActionProvider();
		provider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		provider.setShareIntent(getDefaultShareIntent());

		MenuItem refresh = menu.findItem(R.id.menu_refresh);
		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(getApplicationContext(), "Update",
						Toast.LENGTH_SHORT).show();
				offerUpdater.updateAllOffer();
				return false;
			}
		});

		return true;
	}

	private static Intent getDefaultShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "HSR Menu @ "
				+ selectedDay.getDate() + "-" + selectedOffer.getTitle());
		intent.putExtra(android.content.Intent.EXTRA_TEXT,
				selectedOffer.getOfferTxt());
		return intent;
	}

	public boolean DBUpdateNeeded() {
		long dbage = new WeekDataSource(dbHelper).getWeekLastUpdate();
		long actday = new Date().getTime();
		long difference = 4 * 24 * 60 * 60 * 1000; // Weil der 1.1.1970 ein
													// Donnerstag war

		if (actday - ((actday + difference) % WEEK_IN_MILLISECONDS) > dbage)
			return true; // dbage ist aus letzer Woche
		return false; // dbage ist neuer als der letzte Montag

	}

}