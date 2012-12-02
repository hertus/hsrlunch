package ch.hsr.hsrlunch;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;
import android.app.Fragment.SavedState;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hsr.hsrlunch.controller.PersistenceFactory;
import ch.hsr.hsrlunch.model.Badge;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.Week;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.ui.CustomMenuView;
import ch.hsr.hsrlunch.ui.SettingsActivity;
import ch.hsr.hsrlunch.util.CheckRessources;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.DateHelper;
import ch.hsr.hsrlunch.util.GoogleTranslate;
import ch.hsr.hsrlunch.util.MenuViewAdapter;
import ch.hsr.hsrlunch.util.TabPageAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SherlockFragmentActivity implements
		OnSharedPreferenceChangeListener {
	private static final int SHOW_PREFERENCES = 1;

	private static final String DAY_INDEX = "selectedOfferIndex";
	private static final String OFFER_INDEX = "selectedDayIndex";

	public static WorkDay selectedDay;
	public static Offer selectedOffer;
	private static Intent shareIntent;
	private Week week;

	public static String[] offertitles;
	public String[] errorTypes;

	private static MenuItem refreshProgressItem;

	private ViewPager mViewPager;
	private MenuDrawerManager mMenuDrawer;
	private TabPageAdapter mTabPageAdapter;
	private ShareActionProvider provider;
	private MenuViewAdapter mvAdapter;
	private TabPageIndicator indicator;

	private LinearLayout badgeLayout;
	private LinearLayout errorMsgLayout;
	private CustomMenuView menuView;

	private DBOpenHelper dbHelper;
	private PersistenceFactory persistenceFactory;

	public static boolean dataAvailable = true;
	private boolean isOnBadge = false;
	private boolean isOnOfferUpdate = false;

	private int favouriteMenu;
	private int indexOfSelectedDay;
	private int indexOfSelectedOffer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		onCreatePersistence();

		offertitles = getResources().getStringArray(R.array.menu_title_entries);
		errorTypes = getResources().getStringArray(R.array.errorTypes);

		setPreferencesVersion();
		updatePreferences();

		onCreateMenuDrawer();
		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(menuView);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		onCreateViewPager(savedInstanceState);

		shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");

		badgeLayout = (LinearLayout) findViewById(R.id.badge);
		errorMsgLayout = (LinearLayout) findViewById(R.id.error);
		errorMsgLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				errorMsgLayout.setVisibility(View.GONE);
			}
		});

		onCreateDataUpdate();

	}

	private void setPreferencesVersion() {
		if (Build.VERSION.SDK_INT >= 14) {
			PreferenceManager.setDefaultValues(this, R.xml.userpreference,
					false);
		} else {
			PreferenceManager.setDefaultValues(this,
					R.xml.userpreference_oldver, false);
		}
	}

	private void updatePreferences() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		isOnBadge = prefs.getBoolean(SettingsActivity.PREF_BADGE, false);

		String temp = prefs.getString(SettingsActivity.PREF_FAV_MENU,
				offertitles[0]);

		// update index favourite menu
		for (int i = 0; i < offertitles.length; i++) {
			if (temp.equals(offertitles[i])) {
				favouriteMenu = i;
				return;
			}
		}
		indexOfSelectedOffer = favouriteMenu;
	}

	private void onCreatePersistence() {
		dbHelper = new DBOpenHelper(this);
		persistenceFactory = new PersistenceFactory(dbHelper);
		week = persistenceFactory.getWeek();
	}

	/**
	 * Checks if DB Update for Offers if needed and update them. Checks if Badge
	 * is switched on and update it.
	 */
	private void onCreateDataUpdate() {
		
		if (DateHelper.getDayOfWeek() == 1 || DateHelper.getDayOfWeek() == 7) {
			Log.d("MainActivity",
					"It's weekend! No Updates, no Data, go out and play!");
			dataAvailable = false;
			isOnOfferUpdate = false;
		} else {
			// Initialize DB and check for Updates
			if (!DateHelper.compareLastUpdateToMonday(week.getLastUpdate())) {
				isOnOfferUpdate = true;
			} else {
				isOnOfferUpdate = false;
			}
			doUpdates();
			indexOfSelectedDay = DateHelper.getSelectedDayDayOfWeek();
		}
		setSelectedFragment(indexOfSelectedDay, indexOfSelectedOffer);

		updateBadgeView();
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
				if (position >= 1 && position <= 6) {
					setSelectedFragment(position - 1,favouriteMenu);
				} else {
					// starte Settings-Activity
					Intent i = new Intent(getApplicationContext(),
							SettingsActivity.class);
					startActivityForResult(i, SHOW_PREFERENCES);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar_menu, menu);

		MenuItem item = menu.findItem(R.id.menu_share);
		provider = (ShareActionProvider) item.getActionProvider();
		provider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		updateShareIntent();
		provider.setShareIntent(shareIntent);

		MenuItem refresh = menu.findItem(R.id.menu_refresh);
		refreshProgressItem = refresh;

		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// when click on Refresh, do always a OfferUpdate
				isOnOfferUpdate = true;
				doUpdates();
				return false;
			}

		});

		//TODO Translate Menu only when Locale is not German?
		MenuItem translate = menu.findItem(R.id.menu_translate);
		translate.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				openTranslateIntent();
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

	private void onCreateViewPager(Bundle savedInstanceState) {
		mTabPageAdapter = new TabPageAdapter(this, getSupportFragmentManager(),
				selectedDay);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mTabPageAdapter);
		if (savedInstanceState != null) {
			indexOfSelectedDay = savedInstanceState.getInt(DAY_INDEX);
			indexOfSelectedOffer = savedInstanceState.getInt(OFFER_INDEX);
		}
		mViewPager.setCurrentItem(indexOfSelectedDay, true);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setCurrentItem(indexOfSelectedDay);

		// Listener für "pageChange Event"
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				super.onPageSelected(position);
				if (dataAvailable) {

					selectedOffer = selectedDay.getOfferList().get(position);

					updateShareIntent();
					// provider.setShareIntent(shareIntent);
				}
			}
		};
		indicator.setOnPageChangeListener(pageChangeListener);
		
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

	private void openTranslateIntent() {
		GoogleTranslate tr = new GoogleTranslate(this);
		tr.startNewGoogleTranslateIntent(selectedOffer.getContent());
	}

	/**
	 * Sets the selected Day for viewPager
	 * 
	 * @param int day 0-4, 0 = MO, ..., 4 = FR
	 * @param int offer 0-2
	 */
	private void setSelectedFragment(int day, int offer) {
		Log.d("MainActivity","setSelectedFragment wurde aufgerufen, day="+day+", offer="+offer);

		
		selectedDay = week.getDayList().get(day);		
		selectedOffer = selectedDay.getOfferList().get(offer);
		updateTabPageAdapterData();
	}

	private void updateTabPageAdapterData() {
	
		if (mViewPager != null) {
			mViewPager.setCurrentItem(favouriteMenu, true);
		}
		if (indicator != null) {
			indicator.setCurrentItem(favouriteMenu);
		}
		if (mTabPageAdapter != null) {
			mTabPageAdapter.setDay(selectedDay);
		}
	}

	private void updateShareIntent() {

		if (dataAvailable) {
			shareIntent.removeExtra(android.content.Intent.EXTRA_SUBJECT);
			shareIntent.removeExtra(android.content.Intent.EXTRA_TEXT);

			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"HSR SV-Menu am: " + selectedDay.getDateStringMedium());
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					offertitles[selectedOffer.getOfferType()] + "\n\n"
							+ selectedOffer.getOfferAndPrice());
		} else {
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"HSR SV-Menu nicht vorhanden");
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SHOW_PREFERENCES) {
			updatePreferences();
			updateBadgeView();
			updateTabPageAdapterData();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updatePreferences();
		updateBadgeView();
		updateTabPageAdapterData();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(OFFER_INDEX, mViewPager.getCurrentItem());
		outState.putInt(DAY_INDEX, DateHelper.getSelectedDayDayOfWeek());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		indexOfSelectedDay = savedInstanceState.getInt(DAY_INDEX);
		indexOfSelectedOffer = savedInstanceState.getInt(OFFER_INDEX);
		
	}

	private void updateBadgeView() {
		if (isOnBadge) {
			badgeLayout.setVisibility(View.VISIBLE);
			Badge badge = persistenceFactory.getBadge();
			TextView badgeAmount = (TextView) findViewById(R.id.amount);
			badgeAmount.setText(badge.getAmount() + " CHF");
			TextView badgeLastUpdate = (TextView) findViewById(R.id.lastUpdate);
			badgeLastUpdate.setText(badge.getLastUpdateString());

		} else {
			badgeLayout.setVisibility(View.GONE);
		}
	}

	private void doUpdates() {
		persistenceFactory.setMenuItem(refreshProgressItem);

		// Check if Badge is on + Check if HSR Wifi, if no HSR Wifi then check
		// Offer Update and Internet
		if (isOnBadge) {
			if (CheckRessources.isOnHSRwifi(this)) {
				Log.d("MainActivity", "Is in HSR-LAN, begin with badge update!");
				persistenceFactory.newUpdateTask(this, isOnOfferUpdate,
						isOnBadge);
			} else if (isOnOfferUpdate) {
				if (CheckRessources.isOnline(this)) {
					Log.d("MainActivity", "not in HSR-LAN, but online");
					setAndShowErrorMsg(1, R.string.err_no_hsrwifi);
					persistenceFactory.newUpdateTask(this, isOnOfferUpdate,
							false);
				} else {
					Log.d("MainActivity", "not online");
					setAndShowErrorMsg(2, R.string.err_no_internet);
				}
			} else {
				setAndShowErrorMsg(1, R.string.err_no_hsrwifi);
			}

			// Check if Offer Update is needed when Badge is off + Check if
			// Internet is on
		} else if (isOnOfferUpdate) {
			if (CheckRessources.isOnline(this)) {
				Log.d("MainActivity", "Is online, begin with offer update!");
				persistenceFactory.newUpdateTask(this, isOnOfferUpdate, false);
			} else {
				Log.d("MainActivity", "Is offline, can't update!");
				setAndShowErrorMsg(2, R.string.err_no_internet);
			}
		}
	}

	/**
	 * Update the Views after UpdateTask
	 */
	public void notifyDataChanges() {

		week = persistenceFactory.getWeek();
		setSelectedFragment(indexOfSelectedDay, indexOfSelectedOffer);

		updateBadgeView();
		updateTabPageAdapterData();
	}

	/**
	 * @param int errorType 0= Information, 1 = Warning, 2 = Error
	 * 
	 * @param int errorMsgId Resource id of String of the message that should be
	 *        displayed
	 */
	public void setAndShowErrorMsg(int errorType, int errorMsgId) {

		errorMsgLayout.setVisibility(View.VISIBLE);

		TextView errorTypeTv = (TextView) findViewById(R.id.errorType);
		TextView errorMsgTv = (TextView) findViewById(R.id.errorMsg);

		errorTypeTv.setTextColor(getResources().getColor(R.color.black));
		errorMsgTv.setTextColor(getResources().getColor(R.color.black));
		errorMsgTv.setText(errorMsgId);

		switch (errorType) {
		case 0:
			errorTypeTv.setText(errorTypes[0] + ":");
			errorMsgLayout.setBackgroundColor(getResources().getColor(
					R.color.yellow));
			break;
		case 1:
			errorTypeTv.setText(errorTypes[1] + ":");
			errorMsgLayout.setBackgroundColor(getResources().getColor(
					R.color.orange));
			break;
		case 2:
			errorTypeTv.setText(errorTypes[2] + ":");
			errorMsgLayout.setBackgroundColor(getResources().getColor(
					R.color.red));
			break;
		}
	}

}
