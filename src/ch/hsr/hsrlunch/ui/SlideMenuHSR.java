package ch.hsr.hsrlunch.ui;


import java.lang.reflect.Method;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.util.SlideMenuInterface;
import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SlideMenuHSR extends LinearLayout {
	
	// keys for saving/restoring instance state
	private final static String KEY_MENUSHOWN = "menuWasShown";
	private final static String KEY_STATUSBARHEIGHT = "statusBarHeight";
	private final static String KEY_SUPERSTATE = "superState";
	
	private static boolean menuShown = false;
	private int statusHeight;
	private static View menu;
	private static ViewGroup content;
	private static FrameLayout parent;
	private static int menuSize;
	private Activity act;
	private TranslateAnimation slideRightAnim;
	private TranslateAnimation slideMenuLeftAnim;
	private TranslateAnimation slideContentLeftAnim;
	
	private ArrayList<SlideMenuItem> menuItemList;
	private SlideMenuInterface.OnSlideMenuItemClickListener callback;
	
	/**
	 * Constructor used by the inflation apparatus.
	 * To be able to use the SlideMenu, call the {@link #init init()} method.
	 * @param context
	 */
	public SlideMenuHSR(Context context) {
		super(context);
	}
	
	/**
	 * Constructor used by the inflation apparatus.
	 * To be able to use the SlideMenu, call the {@link #init init()} method.
	 * @param attrs
	 */
	public SlideMenuHSR(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	/** 
	 * Constructs a SlideMenu with the given menu XML. 
	 * @param act The calling activity.
	 * @param menuResource Menu resource identifier.
	 * @param cb Callback to be invoked on menu item click.
	 * @param slideDuration Slide in/out duration in milliseconds.
	 */
	public SlideMenuHSR(Activity act, int menuResource, SlideMenuInterface.OnSlideMenuItemClickListener cb, int slideDuration) {
		super(act);
		init(act, menuResource, cb, slideDuration);
	}
	
	/** 
	 * Constructs an empty SlideMenu.
	 * @param act The calling activity.
	 * @param cb Callback to be invoked on menu item click.
	 * @param slideDuration Slide in/out duration in milliseconds.
	 */
	public SlideMenuHSR(Activity act, SlideMenuInterface.OnSlideMenuItemClickListener cb, int slideDuration) {
		this(act, 0, cb, slideDuration);
	}
	
	/** 
	 * If inflated from XML, initializes the SlideMenu.
	 * @param act The calling activity.
	 * @param menuResource Menu resource identifier, can be 0 for an empty SlideMenu.
	 * @param cb Callback to be invoked on menu item click.
	 * @param slideDuration Slide in/out duration in milliseconds.
	 */
	public void init(Activity act, int menuResource, SlideMenuInterface.OnSlideMenuItemClickListener cb, int slideDuration) {
		
		this.act = act;
		this.callback = cb;
	
		// set size
		menuSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, act.getResources().getDisplayMetrics());
		
		// create animations accordingly
		slideRightAnim = new TranslateAnimation(-menuSize, 0, 0, 0);
		slideRightAnim.setDuration(slideDuration);
		slideRightAnim.setFillAfter(true);
		slideMenuLeftAnim = new TranslateAnimation(0, -menuSize, 0, 0);
		slideMenuLeftAnim.setDuration(slideDuration);
		slideMenuLeftAnim.setFillAfter(true);
		slideContentLeftAnim = new TranslateAnimation(menuSize, 0, 0, 0);
		slideContentLeftAnim.setDuration(slideDuration);
		slideContentLeftAnim.setFillAfter(true);

		// and get our menu
		parseXml(menuResource);
	}
	
	
	/**
	 * Dynamically adds a menu item.
	 * @param item
	 */
	public void addMenuItem(SlideMenuItem item) {
		menuItemList.add(item);
	}
	
	
	/**
	 * Empties the SlideMenu.
	 */
	public void clearMenuItems() {
		menuItemList.clear();
	}
	
	

	/**
	 * Slide the menu in.
	 */
	public void show() {
		this.show(true);
	}

	/**
	 * Set the menu to shown status without displaying any slide animation. 
	 */
	public void setAsShown() {
		this.show(false);
	}
	
	private void show(boolean animate) {
		
		/*
		 *  We have to adopt to status bar height in most cases,
		 *  but not if there is a support actionbar!
		 */
		try {
			Method getSupportActionBar = act.getClass().getMethod("getSupportActionBar", (Class[])null);
			Object sab = getSupportActionBar.invoke(act, (Object[])null);
			sab.toString(); // check for null

			if (android.os.Build.VERSION.SDK_INT >= 11) {
				// over api level 11? add the margin
				applyStatusbarOffset();
			}
		}
		catch(Exception es) {
			// there is no support action bar!
			applyStatusbarOffset();
		}

		
		// modify content layout params
		try {
			content = ((LinearLayout) act.findViewById(android.R.id.content).getParent());
		}
		catch(ClassCastException e) {
			/*
			 * When there is no title bar (android:theme="@android:style/Theme.NoTitleBar"),
			 * the android.R.id.content FrameLayout is directly attached to the DecorView,
			 * without the intermediate LinearLayout that holds the titlebar plus content.
			 */
			content = (FrameLayout) act.findViewById(android.R.id.content);
		}
		FrameLayout.LayoutParams parm = new FrameLayout.LayoutParams(-1, -1, 3);
		parm.setMargins(menuSize, 0, -menuSize, 0);
		content.setLayoutParams(parm);
		
		// animation for smooth slide-out
		if(animate)
			content.startAnimation(slideRightAnim);
		
		// add the slide menu to parent
		parent = (FrameLayout) content.getParent();
		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menu = inflater.inflate(R.layout.slidemenu, null);
		FrameLayout.LayoutParams lays = new FrameLayout.LayoutParams(-1, -1, 3);
		lays.setMargins(0, statusHeight, 0, 0);
		menu.setLayoutParams(lays);
		parent.addView(menu);
		
		// connect the menu's listview
		ListView list = (ListView) act.findViewById(R.id.menu_listview);
		SlideMenuItem[] items = menuItemList.toArray(new SlideMenuItem[menuItemList.size()]);
		SlideMenuAdapter adap = new SlideMenuAdapter(act, items);
		list.setAdapter(adap);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if(callback != null)					
					callback.onSlideMenuItemClick(menuItemList.get(position).id);
				
				hide();
			}
		});
		
		// slide menu in
		if(animate)
			menu.startAnimation(slideRightAnim);
		
		
		menu.findViewById(R.id.overlay).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SlideMenuHSR.this.hide();
			}
		});
		enableDisableViewGroup(content, false);

		menuShown = true;
	}
	
	
	
	/**
	 * Slide the menu out.
	 */
	public void hide() {
		menu.startAnimation(slideMenuLeftAnim);
		parent.removeView(menu);

		content.startAnimation(slideContentLeftAnim);

		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) content.getLayoutParams();
		parm.setMargins(0, 0, 0, 0);
		content.setLayoutParams(parm);
		enableDisableViewGroup(content, true);

		menuShown = false;
	}

	
	private void applyStatusbarOffset() {
		Rect r = new Rect();
		Window window = act.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(r);
		statusHeight = r.top;
	}
	
	
	//originally: http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
	//modified for the needs here
	private void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			if(view.isFocusable())
				view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				enableDisableViewGroup((ViewGroup) view, enabled);
			} else if (view instanceof ListView) {
				if(view.isFocusable())
					view.setEnabled(enabled);
				ListView listView = (ListView) view;
				int listChildCount = listView.getChildCount();
				for (int j = 0; j < listChildCount; j++) {
					if(view.isFocusable())
						listView.getChildAt(j).setEnabled(false);
				}
			}
		}
	}
	
	// originally: https://github.com/darvds/RibbonMenu
	// credit where credits due!
	private void parseXml(int menu){
		
		menuItemList = new ArrayList<SlideMenuItem>();
		
		try{
			XmlResourceParser xpp = act.getResources().getXml(menu);
			
			xpp.next();
			int eventType = xpp.getEventType();
			
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				
				if(eventType == XmlPullParser.START_TAG){
					
					String elemName = xpp.getName();
					
					if(elemName.equals("item")){
											
						
						String textId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "title");
//						String iconId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "icon");
						String resId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "id");
						
						SlideMenuItem item = new SlideMenuItem();
						item.id = Integer.valueOf(resId.replace("@", ""));
//						item.icon = act.getResources().getDrawable(Integer.valueOf(iconId.replace("@", "")));
						item.label = resourceIdToString(textId);
						
						menuItemList.add(item);
					}
					
				}
				
				eventType = xpp.next();
				
			}
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	private String resourceIdToString(String text){
		if(!text.contains("@")){
			return text;
		} else {
			String id = text.replace("@", "");
			return act.getResources().getString(Integer.valueOf(id));
			
		}
	}
	
	
	@Override 
	protected void onRestoreInstanceState(Parcelable state)	{
		try{
			
			if (state instanceof Bundle) {
				Bundle bundle = (Bundle) state;
				
				statusHeight = bundle.getInt(KEY_STATUSBARHEIGHT);

				if(bundle.getBoolean(KEY_MENUSHOWN))
					show(false); // show without animation
				
				super.onRestoreInstanceState(bundle.getParcelable(KEY_SUPERSTATE));
				
				return;
			}
			
			super.onRestoreInstanceState(state);
			
		}
		catch(NullPointerException e) { 
			// in case the menu was not declared via XML but added from code
		}
	}
	
	

	@Override 
	protected Parcelable onSaveInstanceState()	{
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_SUPERSTATE, super.onSaveInstanceState());
		bundle.putBoolean(KEY_MENUSHOWN, menuShown);
		bundle.putInt(KEY_STATUSBARHEIGHT, statusHeight);

		return bundle;
	}
	
	
	
	
	
	public static class SlideMenuItem {
		public int id;
//		public Drawable icon;
		public String label;
	}
	
	// a simple adapter
	private static class SlideMenuAdapter extends ArrayAdapter<SlideMenuItem> {
		Activity act;
		SlideMenuItem[] items;
		class MenuItemHolder {
			public TextView label;
			public ImageView icon;
		}
		
		public SlideMenuAdapter(Activity act, SlideMenuItem[] items) {
			super(act, R.id.menu_label, items);
			this.act = act;
			this.items = items;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = act.getLayoutInflater();
				rowView = inflater.inflate(R.layout.slidemenu_listitem, null);
				MenuItemHolder viewHolder = new MenuItemHolder();
				viewHolder.label = (TextView) rowView.findViewById(R.id.menu_label);
//				viewHolder.icon = (ImageView) rowView.findViewById(R.id.menu_icon);
				rowView.setTag(viewHolder);
			}

			MenuItemHolder holder = (MenuItemHolder) rowView.getTag();
			String s = items[position].label;
			holder.label.setText(s);
//			holder.icon.setImageDrawable(items[position].icon);

			return rowView;
		}
	}



}
