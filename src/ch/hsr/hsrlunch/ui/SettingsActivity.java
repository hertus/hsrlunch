package ch.hsr.hsrlunch.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import ch.hsr.hsrlunch.R;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
public class SettingsActivity extends SherlockPreferenceActivity {

	public static final String PREF_FAV_MENU = "PREF_FAV_MENU";
	public static final String PREF_BADGE = "PREF_BADGE";
	public static final String PREF_LANG = "PREF_LANG";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreference);
    	getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.xml.userpreference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
