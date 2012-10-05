package ch.hsr.hsrlunch;

import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.coboltforge.slidemenu.SlideMenu;
import com.coboltforge.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;

public class MainActivity extends SherlockFragmentActivity implements OnSlideMenuItemClickListener {
	
	private SlideMenu slidemenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        	getSupportActionBar().setHomeButtonEnabled(true);
        
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
}
