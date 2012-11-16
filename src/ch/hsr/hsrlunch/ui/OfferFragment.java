package ch.hsr.hsrlunch.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.R;

public class OfferFragment extends Fragment {
	private static final String MENU_DATA_EXTRA = "menuNum";
	private int menuNum;

	private TextView title;
	private TextView date;
	private TextView content;
	private TextView price;

	
	// Empty constructor, required as per Fragment docs
	public OfferFragment() {
	}
	
	public static OfferFragment newInstance(int menuNum) {
		
		final OfferFragment f = new OfferFragment();
		final Bundle args = new Bundle();
		args.putInt(MENU_DATA_EXTRA, menuNum);
		f.setArguments(args);

		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		menuNum = getArguments() != null ? getArguments().getInt(MENU_DATA_EXTRA) : -1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.offer, container, false);

		if (v != null) {
			title = (TextView) v.findViewById(R.id.title);
			date = (TextView) v.findViewById(R.id.date);
			content = (TextView) v.findViewById(R.id.content);
			price = (TextView) v.findViewById(R.id.price);
			updateValues();
		}

		return v;

	}
	   public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        getArguments().putInt(MENU_DATA_EXTRA, menuNum);
	    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateValues();

	}

	public void updateValues() {
		if (menuNum < 0 || !MainActivity.dataAvailable) {
			setEmptyText();
			return;
		} else {
			if (menuNum >= 0) {
				if (MainActivity.dataAvailable) {
					if (MainActivity.selectedDay.getOfferList().get(menuNum)
							.getContent().equals("EMPTY")) {
						setEmptyText();
					} else {
						title.setText(getResources().getStringArray(
								R.array.menu_title_entries)[menuNum]);
						date.setText(MainActivity.selectedDay.getDateString());
						content.setText(MainActivity.selectedDay.getOfferList()
								.get(menuNum).getContent());
						price.setText(MainActivity.selectedDay.getOfferList()
								.get(menuNum).getPrice());
					}

				}
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
