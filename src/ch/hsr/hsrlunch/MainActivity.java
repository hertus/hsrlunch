package ch.hsr.hsrlunch;

import java.util.Locale;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
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
import ch.hsr.hsrlunch.ui.CreditActivity;
import ch.hsr.hsrlunch.ui.CustomMenuView;
import ch.hsr.hsrlunch.ui.OfferFragment;
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
	private static final String DAY_INDEX = "selectedDayIndex";
	private static final String OFFER_INDEX = "selectedOfferIndex";
	private static final String DATA_AVAIL = "dataAvailable";
	private final String TAG = "MainActivity";

	private static DBOpenHelper dbHelperSaveInstance;
	private static PersistenceFactory persistenceFactorySaveInstance;

	private Intent shareIntent;
	private Offer selectedOffer;
	private WorkDay selectedDay;
	private Week week;
	private Badge badge;

	private String[] offertitles;
	private String[] errorTypes;

	private ViewPager mViewPager;
	private MenuDrawerManager mMenuDrawer;
	private TabPageAdapter mTabPageAdapter;
	private ShareActionProvider provider;
	private MenuViewAdapter mvAdapter;
	private TabPageIndicator indicator;

	private LinearLayout badgeLayout;
	private LinearLayout errorMsgLayout;
	private TextView weekendInfo;
	private CustomMenuView menuView;

	private DBOpenHelper dbHelper;
	private PersistenceFactory persistenceFactory;

	private boolean dataAvailable = false;
	private boolean isOnBadge = false;
	private boolean isOnOfferUpdate = false;
	private boolean onStartUpdate = true;
	private boolean multiPane = false;

	private int favouriteMenu = 0;
	private int indexOfSelectedDay = 0;
	private int indexOfSelectedOffer = 0;
	private OfferFragment fragment1;
	private OfferFragment fragment2;
	private OfferFragment fragment3;
	private MenuItem shareMenuItem;
	private MenuItem translateMenuItem;
	private FragmentManager fm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			onStartUpdate = false;

			dbHelper = dbHelperSaveInstance;
			persistenceFactory = persistenceFactorySaveInstance;

			indexOfSelectedDay = savedInstanceState.getInt(DAY_INDEX);
			indexOfSelectedOffer = savedInstanceState.getInt(OFFER_INDEX);
			dataAvailable = savedInstanceState.getBoolean(DATA_AVAIL);

		} else {
			indexOfSelectedDay = DateHelper.getSelectedDayDayOfWeek();
			indexOfSelectedOffer = favouriteMenu;
		}

		onCreatePersistence();

		offertitles = getResources().getStringArray(R.array.menu_title_entries);
		errorTypes = getResources().getStringArray(R.array.errorTypes);

		setPreferencesVersion();
		updatePreferences();

		onCreateMenuDrawer();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");

		fm = getSupportFragmentManager();
		if (fm.findFragmentById(R.id.fragment1) != null
				&& fm.findFragmentById(R.id.fragment2) != null
				&& fm.findFragmentById(R.id.fragment3) != null
				&& fm.findFragmentById(R.id.fragment1).isAdded()
				&& fm.findFragmentById(R.id.fragment2).isAdded()
				&& fm.findFragmentById(R.id.fragment3).isAdded()) {
			multiPane = true;
			onCreateTabletFragment(savedInstanceState);
		} else {
			multiPane = false;
			onCreateViewPager(savedInstanceState);
		}

		badgeLayout = (LinearLayout) findViewById(R.id.badge);
		weekendInfo = (TextView) findViewById(R.id.weekendInfo);

		errorMsgLayout = (LinearLayout) findViewById(R.id.errorPanel);
		errorMsgLayout.setVisibility(View.GONE);
		errorMsgLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				errorMsgLayout.setVisibility(View.GONE);
			}
		});

		setSelectedFragment();
		updateShareIntent();
		updateBadgeView();
		if (DateHelper.getDayOfWeek() == 0 || DateHelper.getDayOfWeek() == 7) {
			weekendInfo.setVisibility(View.VISIBLE);
		}

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
				break;
			}
		}
		indexOfSelectedOffer = favouriteMenu;
	}

	private void onCreatePersistence() {

		if (dbHelper == null) {
			Log.d(TAG, "dbHelper was null, create new");
			dbHelper = new DBOpenHelper(this);
		}

		if (persistenceFactory == null) {
			Log.d(TAG, "persistenceFactory was null, create new");
			persistenceFactory = new PersistenceFactory(dbHelper);
		}

		week = persistenceFactory.getWeek();
		badge = persistenceFactory.getBadge();
	}

	/**
	 * Checks if DB Update for Offers if needed and update them. Checks if Badge
	 * is switched on and update it.
	 */
	private void checkDataUpdate() {
		Log.d(TAG, "Checking for Data Updates");
		// Initialize DB and check for Updates
		if (!DateHelper.compareLastUpdateToMonday(week.getLastUpdate())) {
			isOnOfferUpdate = true;
		} else {
			isOnOfferUpdate = false;
			dataAvailable = true;
			updateShareIntent();
			updateActionBarItems();

		}
		doUpdates();
	}

	private void onCreateMenuDrawer() {
		menuView = new CustomMenuView(this);
		mMenuDrawer = new MenuDrawerManager(this, MenuDrawer.MENU_DRAG_CONTENT);
		mMenuDrawer.getMenuDrawer().setMenuWidth(250);
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
					indexOfSelectedDay = position - 1;
					indexOfSelectedOffer = favouriteMenu;
					setSelectedFragment();
				} else if (position == 7) {
					Intent i = new Intent(getApplicationContext(),
							SettingsActivity.class);
					startActivityForResult(i, SHOW_PREFERENCES);
				} else {
					Intent i = new Intent(getApplicationContext(),
							CreditActivity.class);
					startActivity(i);
				}
			}
		});

		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(menuView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar_menu, menu);

		shareMenuItem = menu.findItem(R.id.menu_share);
		provider = (ShareActionProvider) shareMenuItem.getActionProvider();
		provider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		//updateShareIntent();
		provider.setShareIntent(shareIntent);

		MenuItem refresh = menu.findItem(R.id.menu_refresh);
		persistenceFactory.setMenuItem(refresh);

		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				isOnOfferUpdate = true;
				doUpdates();
				return false;
			}

		});

		translateMenuItem = menu.findItem(R.id.menu_translate);
		translateMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (dataAvailable) {
							openTranslateIntent();
						}
						return false;
					}
				});

		MenuItem settingsMenuItem = menu.findItem(R.id.menu_settings);
		settingsMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent i = new Intent(getApplicationContext(),
								SettingsActivity.class);
						startActivityForResult(i, SHOW_PREFERENCES);
						return true;
					}
				});

		if (multiPane) {
			settingsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		} else {
			settingsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}

		updateActionBarItems();

		// Check for updates after Menu is created -> Progress Bar available
		if (onStartUpdate) {
			checkDataUpdate();
		}else{
			dataAvailable = true;
			updateShareIntent();
		}

		return true;
	}

	private void updateActionBarItems() {
		if (dataAvailable) {
			shareMenuItem.setVisible(true);
			if (!Locale.getDefault().getISO3Language().equals("deu")) {
				translateMenuItem.setVisible(true);
			} else {
				translateMenuItem.setVisible(false);
			}

		} else {
			shareMenuItem.setVisible(false);
			translateMenuItem.setVisible(false);
		}
	}

	private void onCreateViewPager(Bundle savedInstanceState) {
		mTabPageAdapter = new TabPageAdapter(this, getSupportFragmentManager(),
				selectedDay);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mTabPageAdapter);
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
					indexOfSelectedOffer = position;

					updateShareIntent();
				}
			}
		};
		indicator.setOnPageChangeListener(pageChangeListener);

	}

	private void onCreateTabletFragment(Bundle savedInstanceState) {
		fragment1 = (OfferFragment) fm.findFragmentById(R.id.fragment1);
		fragment2 = (OfferFragment) fm.findFragmentById(R.id.fragment2);
		fragment3 = (OfferFragment) fm.findFragmentById(R.id.fragment3);

		if (dataAvailable) {
			selectedDay = week.getDayList().get(indexOfSelectedDay);
			updateShareIntent();
		}

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
	private void setSelectedFragment() {
		if (multiPane) {
			Log.d(TAG, "setSelectedFragment wurde aufgerufen, day="
					+ indexOfSelectedDay);
			selectedDay = week.getDayList().get(indexOfSelectedDay);
			updateTabletFragmentData();

		} else {

			Log.d(TAG, "setSelectedFragment wurde aufgerufen, day="
					+ indexOfSelectedDay + ", offer=" + indexOfSelectedOffer);
			selectedDay = week.getDayList().get(indexOfSelectedDay);
			selectedOffer = selectedDay.getOfferList()
					.get(indexOfSelectedOffer);
			updateTabPageAdapterData();
		}
	}

	private void updateTabletFragmentData() {
		if (fragment1 == null)
			fragment1 = (OfferFragment) fm.findFragmentByTag("fragment1");
		fragment1.setDayString(selectedDay.getDateStringLong());
		fragment1.setOffer(selectedDay.getOfferList().get(0));
		fragment1.updateValues();

		if (fragment2 == null)
			fragment2 = (OfferFragment) fm.findFragmentByTag("fragment2");
		fragment2.setDayString(selectedDay.getDateStringLong());
		fragment2.setOffer(selectedDay.getOfferList().get(1));
		fragment2.updateValues();

		if (fragment3 == null)
			fragment3 = (OfferFragment) fm.findFragmentByTag("fragment3");
		fragment3.setDayString(selectedDay.getDateStringLong());
		fragment3.setOffer(selectedDay.getOfferList().get(2));
		fragment3.updateValues();
	}

	private void updateTabPageAdapterData() {
		
		if (mTabPageAdapter != null) {
			mTabPageAdapter.setDay(selectedDay);
		}

		if (mViewPager != null) {
			mViewPager.setCurrentItem(indexOfSelectedOffer, true);
		}
		if (indicator != null) {
			indicator.setCurrentItem(indexOfSelectedOffer);
		}

	}

	private void updateShareIntent() {
		if (dataAvailable) {
			shareIntent.removeExtra(android.content.Intent.EXTRA_SUBJECT);
			shareIntent.removeExtra(android.content.Intent.EXTRA_TEXT);

			shareIntent.putExtra(
					android.content.Intent.EXTRA_SUBJECT,
					getString(R.string.share_intent_text) + " "
							+ selectedDay.getDateStringMedium());

			if (multiPane) {
				String offer = new String();
				for (int i = 0; i < selectedDay.getOfferList().size(); i++) {
					offer += (offertitles[i]
							+ "\n"
							+ selectedDay.getOfferList().get(i)
									.getOfferAndPrice() + "\n\n\n");
				}
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, offer);
			} else {
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						offertitles[selectedOffer.getOfferType()] + "\n\n"
								+ selectedOffer.getOfferAndPrice());
			}
		} else {
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					getString(R.string.notAvailable));
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					getString(R.string.notAvailable));

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SHOW_PREFERENCES) {
			Log.d(TAG, "Coming from Preferences");
			updatePreferences();
			updateBadgeView();
			updateTabPageAdapterData();
			checkDataUpdate();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d(TAG, "Pref changed");
		updatePreferences();
		updateBadgeView();
		updateTabPageAdapterData();
		checkDataUpdate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		persistenceFactory.stopUpdateTaskIfRunning();

		dbHelperSaveInstance = dbHelper;
		persistenceFactorySaveInstance = persistenceFactory;

		outState.putInt(OFFER_INDEX, indexOfSelectedOffer);
		outState.putInt(DAY_INDEX, indexOfSelectedDay);
		outState.putBoolean(DATA_AVAIL, dataAvailable);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		indexOfSelectedDay = savedInstanceState.getInt(DAY_INDEX);
		indexOfSelectedOffer = savedInstanceState.getInt(OFFER_INDEX);
		dataAvailable = savedInstanceState.getBoolean(DATA_AVAIL);
	}

	private void updateBadgeView() {
		if (isOnBadge) {
			badgeLayout.setVisibility(View.VISIBLE);
			TextView badgeAmount = (TextView) findViewById(R.id.amount);
			if (badge.getAmount() < 8.00) {
				badgeAmount
						.setTextColor(getResources().getColor(R.color.title));
			} else {
				badgeAmount.setTextColor(getResources().getColor(R.color.grey));
			}
			badgeAmount.setText(badge.getAmount() + " CHF");
			TextView badgeLastUpdate = (TextView) findViewById(R.id.lastUpdate);
			badgeLastUpdate.setText(badge.getLastUpdateString());

		} else {
			badgeLayout.setVisibility(View.GONE);
		}
	}

	private void doUpdates() {
		// Check if Badge is on + Check if HSR Wifi, if no HSR Wifi then check
		// Offer Update and Internet
		if (isOnBadge) {
			if (CheckRessources.isOnHSRwifi(this)) {
				Log.d(TAG, "Is in HSR-LAN, begin with badge update!");
				persistenceFactory.newUpdateTask(this, isOnOfferUpdate,
						isOnBadge);
			} else if (isOnOfferUpdate) {
				if (CheckRessources.isOnline(this)) {
					Log.d(TAG, "not in HSR-LAN, but online");
					setAndShowErrorMsg(1, R.string.err_no_hsrwifi);
					persistenceFactory.newUpdateTask(this, isOnOfferUpdate,
							false);
				} else {
					Log.d(TAG, "not online");
					setAndShowErrorMsg(2, R.string.err_no_internet);
				}
			} else {
				setAndShowErrorMsg(1, R.string.err_no_hsrwifi);
			}

			// Check if Offer Update is needed when Badge is off + Check if
			// Internet is on
		} else if (isOnOfferUpdate) {
			if (CheckRessources.isOnline(this)) {
				Log.d(TAG, "Is online, begin with offer update!");
				persistenceFactory.newUpdateTask(this, isOnOfferUpdate, false);
			} else {
				Log.d(TAG, "Is offline, can't update!");
				setAndShowErrorMsg(2, R.string.err_no_internet);
			}
		}
	}

	/**
	 * Update the Views after UpdateTask
	 */
	public void notifyDataChanges() {
		dataAvailable = true;

		updateActionBarItems();

		week = persistenceFactory.getWeek();
		badge = persistenceFactory.getBadge();

		setSelectedFragment();
		updateBadgeView();
		updateShareIntent();
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
