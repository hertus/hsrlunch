package ch.hsr.hsrlunch.ui;

import ch.hsr.hsrlunch.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class CustomMenuView extends LinearLayout
{
  public CustomMenuView(Context context) {
       super(context);
       setupView();
  }
  public CustomMenuView(Context context, AttributeSet attrs)
  {
      super(context, attrs);
      setupView();
  }
  public void setupView()
  {
//    setContentView(R.layout.custom); // Not possible
	  LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  layoutInflater.inflate(R.layout.slidemenu, this, true);
  }
}