package ch.hsr.hsrlunch.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.util.DBConstants;

public class OfferFragment extends Fragment {
	int menuNum;

	TextView title;
	TextView date;
	TextView content;
	TextView price;
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        
	        
			final View v = inflater.inflate(R.layout.offer, container, false);

			if (v != null) {
				title = (TextView) v.findViewById(R.id.title);
				date = (TextView) v.findViewById(R.id.date);
				content = (TextView) v.findViewById(R.id.content);
				price = (TextView) v.findViewById(R.id.price);
			}

			return v;

	    }

	public OfferFragment() {
		menuNum = 0;
	}
	public OfferFragment(int position){
		menuNum = position;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateValues();

	}

	public void updateValues() {

		if (menuNum >= 0 ){
			if (MainActivity.dataAvailable) {
				if(MainActivity.selectedDay.getOfferList().get(menuNum).getContent().equals("EMPTY")){
					setEmptyText();
				}else{
				title.setText(getResources().getStringArray(R.array.menu_title_entries)[menuNum]);
				date.setText(MainActivity.selectedDay.getDate().toString());
				content.setText(MainActivity.selectedDay.getOfferList()
						.get(menuNum).getMenuText());
				price.setText(MainActivity.selectedDay.getOfferList().get(menuNum)
						.getPrice());
			}
			}else{
				setEmptyText();
			}
				
		}

	}

	private void setEmptyText() {
		content.setText(R.string.notAvailable);
		title.setText("");
		date.setText("");
		price.setText("");
	}

}
