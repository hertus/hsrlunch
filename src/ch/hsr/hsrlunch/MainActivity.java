package ch.hsr.hsrlunch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.hsrlunch.controller.OfferUpdater;
import ch.hsr.hsrlunch.controller.WeekDataSource;
import ch.hsr.hsrlunch.model.Badge;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.ui.CustomMenuView;
import ch.hsr.hsrlunch.ui.SettingsActivity;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.MenuViewAdapter;
import ch.hsr.hsrlunch.util.TabPageAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SherlockFragmentActivity implements OnSharedPreferenceChangeListener {
	private static final int SHOW_PREFERENCES = 1;
	private static final long WEEK_IN_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;

	public static List<WorkDay> dayList = new ArrayList<WorkDay>();
	public static boolean dataAvailable = true;
	public static WorkDay selectedDay;
	public static Offer selectedOffer;
	public static String[] offertitles;

	private List<Offer> offerList;
	private Badge badge;

	private ViewPager mViewPager;
	private MenuDrawerManager mMenuDrawer;
	private TabPageAdapter mTabPageAdapter;
	private ShareActionProvider provider;
	private MenuViewAdapter mvAdapter;
	private LinearLayout badgeLayout;
	private CustomMenuView menuView;

	private final DBOpenHelper dbHelper= new DBOpenHelper(this);
	private OfferUpdater offerUpdater;

	//Attributes for Preferences in SettingActivity
	private boolean showBadgeInfo = false;
	private int favouriteMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		onCreatePersistence();
		offertitles = getResources().getStringArray(R.array.menu_title_entries);

		 if (Build.VERSION.SDK_INT >= 14) {
			 PreferenceManager.setDefaultValues(this, R.xml.userpreference, false);
		 }else{
			 PreferenceManager.setDefaultValues(this, R.xml.userpreference_oldver, false);
		 }	
		updatePreferences();
		 
		onCreateMenuDrawer();
		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(menuView);

		getSupportActionBar().setHomeButtonEnabled(true);

		// sp�ter furtschmeissen
		init();
		
		//für test am wochenende überschrieben der Werte
		dataAvailable = true;
		setSelectedDay(5);

		onCreateViewPager();

		badgeLayout = (LinearLayout) findViewById(R.id.badge);
		updateBadgeView();

	}

	private void onCreatePersistence() {
//		dbHelper = new DBOpenHelper(this);
		offerUpdater = new OfferUpdater(dbHelper);
	}

	private void onCreateViewPager() {
		mTabPageAdapter = new TabPageAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mTabPageAdapter);
		mViewPager.setCurrentItem(favouriteMenu, true);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setCurrentItem(favouriteMenu);

		// Listener für "pageChange Event"
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				selectedOffer = selectedDay.getOfferList().get(position);
				provider.setShareIntent(getDefaultShareIntent());
			}
		};
		indicator.setOnPageChangeListener(pageChangeListener);
	}

	private void onCreateMenuDrawer() {
		menuView = new CustomMenuView(this);
		mMenuDrawer = new MenuDrawerManager(this, MenuDrawer.MENU_DRAG_CONTENT);
		mvAdapter = new MenuViewAdapter(this, mMenuDrawer);
		menuView.setAdapter(mvAdapter);
		menuView.setOnScrollChangedListener(new CustomMenuView.OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				mMenuDrawer.getMenuDrawer().invalidate();
			}
		});
		menuView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mvAdapter.setActiveEntry(position);
				mMenuDrawer.setActiveView(view, position); // falls vorig Zeit^^
				mMenuDrawer.closeMenu();
				if (position <= 6) {
					setSelectedDay(position);
				} else {
					// starte Settings-Activity
					Intent i = new Intent(getApplicationContext(),
							SettingsActivity.class);
					startActivityForResult(i, SHOW_PREFERENCES);
				}
			}
		});
	}

	private void updateBadgeView() {
		
		if (showBadgeInfo) {
			badgeLayout.setVisibility(View.VISIBLE);
			TextView badgeAmount = (TextView) findViewById(R.id.amount);
			badgeAmount.setText(badge.getAmount() + " CHF");
			TextView badgeLastUpdate = (TextView) findViewById(R.id.lastUpdate);
			badgeLastUpdate.setText(badge.getLastUpdate());
		} else {
			badgeLayout.setVisibility(View.GONE);
		}
	}
	private void updateViewPager() {
		if(mViewPager != null)
			mViewPager.setCurrentItem(favouriteMenu, true);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: // this is the app icon of the actionbar
			mMenuDrawer.toggleMenu();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * setzen des ausgewählten Tages(selectedDay) des viewPagers
	 * @param:  int position 1-5, 1 = Montag, ..., 5=Freitag
	 */
	private void setSelectedDay(int position) {
		//korrektur index für dayList durch -1
		selectedDay = dayList.get(position - 1); 
		selectedOffer = selectedDay.getOfferList().get(favouriteMenu);
		if (mTabPageAdapter != null) {
			mTabPageAdapter.notifyDataSetChanged();
			updateViewPager();
		}
	}

	/*
	 * statisches Füllen der Daten solange zugriff auf DB noch nicht
	 * implementiert ist
	 */
	private void init() {

		badge = new Badge(999.99, new Date());

		Offer m1 = new Offer(1,
				"Fischtäbli\nSauce Tatar\nBlattspinat\nSalzkartoffeln",
				"INT 8.00 EXT 10.60");
		Offer m2 = new Offer(
				2,
				"Gemüseteigtaschen\nTomatensauce\nSalzkartoffeln\nBuntersalat",
				"INT 8.00 EXT 10.60");
		Offer m3 = new Offer(3,
				"Schweinefilet im Speckmantel\nTomatensauce\nBuntersalat",
				"INT 14.50 EXT 15.50");
		offerList = new ArrayList<Offer>();
		offerList.add(m1);
		offerList.add(m2);
		offerList.add(m3);

		// achtung monat bei GregorianCalendar liegt zwischen 0 und 11!!!
		GregorianCalendar cal1 = new GregorianCalendar(2012, 9, 22);
		GregorianCalendar cal2 = new GregorianCalendar(2012, 9, 23);
		GregorianCalendar cal3 = new GregorianCalendar(2012, 9, 24);
		GregorianCalendar cal4 = new GregorianCalendar(2012, 9, 25);
		GregorianCalendar cal5 = new GregorianCalendar(2012, 9, 26);

		dayList.add(new WorkDay(0, new Date(cal1.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(1, new Date(cal2.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(2, new Date(cal3.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(3, new Date(cal4.getTimeInMillis()), offerList));
		dayList.add(new WorkDay(4, new Date(cal5.getTimeInMillis()), offerList));

		GregorianCalendar cal = new GregorianCalendar();		
		/*
		 * Samstags und Sonntags stehen keine Informationen bereit
		 */
		if(cal.get(Calendar.DAY_OF_WEEK) == 1	/*sonntag*/
				|| cal.get(Calendar.DAY_OF_WEEK) == 7 /*samstag*/ ){
			dataAvailable = false;
			setSelectedDay(1);
		}else{
			dataAvailable = true;
			setSelectedDay( (cal.get(Calendar.DAY_OF_WEEK)+6) % 7);
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar_menu, menu);

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
				offerUpdater.updateAllOffer(); // TODO: sp�ter �ndern - Factory
												// f�llt Objekte etc.
				return false;
			}
		});

		MenuItem settings = menu.findItem(R.id.menu_settings);
		settings.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent i = new Intent(getApplicationContext(),
						SettingsActivity.class);
				startActivityForResult(i, SHOW_PREFERENCES);
				return true;
			}
		});

		return true;
	}

	private static Intent getDefaultShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "HSR Menu @ "
				+ selectedDay.getDate() + "-" +offertitles[selectedOffer.getOfferType()-1 ]);
		intent.putExtra(android.content.Intent.EXTRA_TEXT,
				selectedOffer.getOfferTxt());
		return intent;
	}

	public boolean DBUpdateNeeded() {
		// TODO: check if 7 days difference + check if sunday is past
		long dbage = new WeekDataSource(dbHelper).getWeekLastUpdate();
		long actday = new Date().getTime();
		long difference = 3 * 24 * 60 * 60 * 1000; // Weil der 1.1.1970 ein Donnerstag war

		if (actday - ((actday + difference) % WEEK_IN_MILLISECONDS) > dbage)
			return true; // dbage ist aus letzer Woche
		return false; // dbage ist neuer als der letzte Montag

	}

	private void updatePreferences() {
		
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		showBadgeInfo = prefs.getBoolean(SettingsActivity.PREF_BADGE, false);		

		String temp = prefs.getString(SettingsActivity.PREF_FAV_MENU, offertitles[0]);
		
		//update index von  favourite menu 
		for(int i = 0; i<= offertitles.length; i++){
			if(temp.equals(offertitles[i])){
				favouriteMenu = i;
				break;
			}
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SHOW_PREFERENCES) {
			updatePreferences();
			//Badge Information updaten und wenn nötig anzeigen
			updateBadgeView();
			updateViewPager();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updatePreferences();
		//Badge Information updaten und wenn nötig anzeigen
		updateBadgeView();
		updateViewPager();
	}

}