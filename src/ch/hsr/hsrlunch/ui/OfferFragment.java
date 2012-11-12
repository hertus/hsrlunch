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
	int menuNum;

	TextView title;
	TextView date;
	TextView content;
	TextView price;

	public static OfferFragment newInstance(int menuNum) {
		final OfferFragment f = new OfferFragment();
		final Bundle args = new Bundle();
		args.putInt(MENU_DATA_EXTRA, menuNum);
		f.setArguments(args);
		return f;
	}

	// Empty constructor, required as per Fragment docs
	public OfferFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			menuNum = getArguments().getInt(MENU_DATA_EXTRA);
		} else
			menuNum = -1;
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
		}

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateValues();

	}

	public void updateValues() {
		if (menuNum >= 0 && MainActivity.dataAvailable) {
			//title.setText(MainActivity.selectedDay.getOfferList().get(menuNum)
			//		.getTitle());
			//date.setText(MainActivity.selectedDay.getDate().toString());
			//content.setText(MainActivity.selectedDay.getOfferList()
			//		.get(menuNum).getMenuText());
			price.setText(MainActivity.selectedDay.getOfferList().get(menuNum)
					.getPrice());
		}

	}

}
