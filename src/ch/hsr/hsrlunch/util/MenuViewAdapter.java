package ch.hsr.hsrlunch.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.simonvt.widget.MenuDrawerManager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.hsr.hsrlunch.R;

class Category {
	String mTitle;

	Category(String title) {
		mTitle = title;
	}
}

class Item {

	String mTitle;

	Item(String title) {
		mTitle = title;
	}
}

public class MenuViewAdapter extends BaseAdapter {

	private List<Object> mItems = new ArrayList<Object>();
	private Activity main;
	private MenuDrawerManager mMenuDrawer;
	private int mActivePosition;

	public MenuViewAdapter(Activity mainActivity, MenuDrawerManager mMenuDrawer) {
		this.main = mainActivity;
		this.mMenuDrawer = mMenuDrawer;

		List<String> weekdays = Arrays.asList(this.main.getResources()
				.getStringArray(R.array.weekdays));
		
		mItems.add(new Category(main.getResources().getString(R.string.tagesauswahl) ));
		for (String str : weekdays) {
			mItems.add(new Item(str));
		}
		mItems.add(new Category(main.getResources().getString(
				R.string.menu_settings)));
		mItems.add(new Item(main.getResources().getString(
				R.string.menu_settings)));

	}

	public void setActiveEntry(int mActivePosition) {
		this.mActivePosition = mActivePosition;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position) instanceof Item ? 0 : 1;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position) instanceof Item;
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Object item = getItem(position);

		if (item instanceof Category) {
			if (v == null) {
				v = main.getLayoutInflater().inflate(
						R.layout.menu_row_category, parent, false);
			}
			((TextView) v).setText(((Category) item).mTitle);

		} else {
			if (v == null) {
				v = main.getLayoutInflater().inflate(R.layout.menu_row_item,
						parent, false);
			}
			TextView tv = (TextView) v;
			tv.setText(((Item) item).mTitle);
			// tv.setCompoundDrawablesWithIntrinsicBounds(((Item)
			// item).mIconRes, 0, 0, 0);
		}

		v.setTag(R.id.mdActiveViewPosition, position);

		if (position == mActivePosition) {
			mMenuDrawer.setActiveView(v, position);
		}

		return v;
	}
}
