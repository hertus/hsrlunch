package ch.hsr.hsrlunch.ui;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import ch.hsr.hsrlunch.R;

public class SettingsActivity extends SherlockPreferenceActivity {

	public static final String PREF_FAV_MENU = "pref_fav_menu";
	public static final String PREF_BADGE = "pref_badge";
	public static final String PREF_BADGE_USERNAME = "pref_badge_username";
	public static final String PREF_BADGE_PASSWORD = "pref_badge_password";

	// We use addPreferencesFromResource as we need sdk-7 compatibility
	// but build with sdk 15

	@SuppressWarnings("deprecation")
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (Build.VERSION.SDK_INT >= 14) {
			// Display the fragment as the main content.
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new SettingsFragment())
					.commit();
		} else {
			addPreferencesFromResource(R.xml.userpreference_oldver);
		}

	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
