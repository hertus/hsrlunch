package ch.hsr.hsrlunch;

import java.util.Date;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hsr.hsrlunch.controller.BadgeUpdater;
import ch.hsr.hsrlunch.controller.PersistenceFactory;
import ch.hsr.hsrlunch.model.Badge;
import ch.hsr.hsrlunch.model.Offer;
import ch.hsr.hsrlunch.model.Week;
import ch.hsr.hsrlunch.model.WorkDay;
import ch.hsr.hsrlunch.ui.CustomMenuView;
import ch.hsr.hsrlunch.ui.SettingsActivity;
import ch.hsr.hsrlunch.util.DBOpenHelper;
import ch.hsr.hsrlunch.util.DateHelper;
import ch.hsr.hsrlunch.util.MenuViewAdapter;
import ch.hsr.hsrlunch.util.OnBadgeResultListener;
import ch.hsr.hsrlunch.util.TabPageAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SherlockFragmentActivity implements
		OnSharedPreferenceChangeListener, OnBadgeResultListener {
	private static final int SHOW_PREFERENCES = 1;

	public static boolean dataAvailable = true;
	public static WorkDay selectedDay;
	public static Offer selectedOffer;
	private Week week;
	
	public static String[] offertitles;

	private static Context context;
	
	private ViewPager mViewPager;
	private MenuDrawerManager mMenuDrawer;
	private TabPageAdapter mTabPageAdapter;
	private ShareActionProvider provider;
	private MenuViewAdapter mvAdapter;
	private LinearLayout badgeLayout;
	private CustomMenuView menuView;
	private TabPageIndicator indicator;
	
	private DBOpenHelper dbHelper;
	private PersistenceFactory persistenceFactory;

	// Attributes for Preferences in SettingActivity
	private boolean showBadgeInfo = false;
	private int favouriteMenu;
	private int currentSelectedFragmentIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		// Initialize DB and check for Updates
		onCreatePersistence();

		offertitles = getResources().getStringArray(R.array.menu_title_entries);

		if (Build.VERSION.SDK_INT >= 14) {
			PreferenceManager.setDefaultValues(this, R.xml.userpreference,
					false);
		} else {
			PreferenceManager.setDefaultValues(this,
					R.xml.userpreference_oldver, false);
		}
		updatePreferences();

		onCreateMenuDrawer();

		mMenuDrawer.setContentView(R.layout.activity_main);
		mMenuDrawer.setMenuView(menuView);

		// getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// set the Day for View
		setSelectedDay(DateHelper.getSelectedDayDayOfWeek());

		onCreateViewPager();

		badgeLayout = (LinearLayout) findViewById(R.id.badge);
		updateBadgeView();

	}

	public static Context getMainContext() {
		return context;
	}

	private void onCreatePersistence() {
		dbHelper = new DBOpenHelper(this);
		persistenceFactory = new PersistenceFactory(dbHelper);
		week = persistenceFactory.getWeek();

		if (!DateHelper.compareLastUpdateToMonday(week.getLastUpdate())) {
			Log.d("MainActivity",
					"Menus update needed! LastUpdate: "
							+ DateHelper.getFormatedDateStringSHORT(new Date(
									week.getLastUpdate()))
							+ "\nDate of Monday: "
							+ DateHelper.getFormatedDateStringSHORT(DateHelper
									.getMondayOfThisWeekDate()));
			doUpdate();

		} else {
			Log.d("MainActivity",
					"No Menus update needed, last on: "
							+ DateHelper.getFormatedDateStringSHORT(new Date(
									week.getLastUpdate())));
		}
	}

	private void updatePreferences() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		showBadgeInfo = prefs.getBoolean(SettingsActivity.PREF_BADGE, false);

		String temp = prefs.getString(SettingsActivity.PREF_FAV_MENU,
				offertitles[0]);

		// update index von favourite menu
		for (int i = 0; i <= offertitles.length; i++) {
			if (temp.equals(offertitles[i])) {
				favouriteMenu = i;
				return;
			}
		}
		currentSelectedFragmentIndex = favouriteMenu;
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
					setSelectedDay(position - 1);
				} else {
					// starte Settings-Activity
					Intent i = new Intent(getApplicationContext(),
							SettingsActivity.class);
					startActivityForResult(i, SHOW_PREFERENCES);
				}
			}
		});
	}

	private void onCreateViewPager() {
		mTabPageAdapter = new TabPageAdapter(this,getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mTabPageAdapter);
		mViewPager.setCurrentItem(favouriteMenu, true);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setCurrentItem(favouriteMenu);

		// Listener für "pageChange Event"
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				System.out.println("onPageSelected-position:"+position);
				System.out.println("indicatior:" +indicator);
				System.out.println("viewPAger: "+mViewPager);
				super.onPageSelected(position);
				
				selectedOffer = selectedDay.getOfferList().get(position);
				
				provider.setShareIntent(getDefaultShareIntent());
			}
		};
		indicator.setOnPageChangeListener(pageChangeListener);
	}

	private void updateBadgeView() {
		if (showBadgeInfo) {
			badgeLayout.setVisibility(View.VISIBLE);
			// hole Informationen aus der DB:
			onBadgeUpdate();
			// initiate Update
			BadgeUpdater service = new BadgeUpdater();
			service.setBackend(persistenceFactory);
			service.setContext(this);
			service.setListener(this);
			service.execute();
		} else {
			badgeLayout.setVisibility(View.GONE);
		}
	}

	private void updateViewPager() {
		if (mViewPager != null)
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

	/**
	 * Sets the selected Day for viewPager
	 * 
	 * @param int position 0-4, 0 = MO, ..., 4 = FR
	 */
	private void setSelectedDay(int position) {
		selectedDay = week.getDayList().get(position);
		selectedOffer = selectedDay.getOfferList().get(favouriteMenu);
		if (mTabPageAdapter != null) {
			mTabPageAdapter.notifyDataSetChanged();
			updateViewPager();
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
				persistenceFactory.setMenuItem(item);
				doUpdate();
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
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"HSR SV-Menu am: " + selectedDay.getDateStringMedium());
		intent.putExtra(android.content.Intent.EXTRA_TEXT,
				offertitles[selectedOffer.getOfferType()] + "\n\n"
						+ selectedOffer.getOfferAndPrice());
		return intent;
	}

	private void doUpdate() {
		persistenceFactory.newUpdateTask();

		if (mTabPageAdapter != null) {
			mTabPageAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SHOW_PREFERENCES) {
			updatePreferences();
			// Badge Information updaten und wenn nötig anzeigen
			updateBadgeView();
			updateViewPager();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updatePreferences();
		// Badge Information updaten und wenn nötig anzeigen
		updateBadgeView();
		updateViewPager();
	}

	@Override
	public void onBadgeUpdate() {
		Badge badge = persistenceFactory.getBadge();
		TextView badgeAmount = (TextView) findViewById(R.id.amount);
		badgeAmount.setText(badge.getAmount() + " CHF");
		TextView badgeLastUpdate = (TextView) findViewById(R.id.lastUpdate);
		badgeLastUpdate.setText(badge.getLastUpdateString());
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("FragmentPosition", mViewPager.getCurrentItem());
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentSelectedFragmentIndex = savedInstanceState.getInt("FragmentPosition");
                    
        mViewPager.setCurrentItem(currentSelectedFragmentIndex);
        indicator.setCurrentItem(currentSelectedFragmentIndex);
    }

}