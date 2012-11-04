package ch.hsr.hsrlunch.ui;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.os.Bundle;
import ch.hsr.hsrlunch.R;

public class SettingsActivity extends SherlockPreferenceActivity {

	public static final String PREF_FAV_MENU = "PREF_FAV_MENU";
	public static final String PREF_BADGE = "PREF_BADGE";
	

        // We use addPreferencesFromResource as we need sdk-9 compatibility
        // but build with sdk 15
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            addPreferencesFromResource(R.xml.userpreference);
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
