package ch.hsr.hsrlunch.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.model.Offer;

public class OfferFragment extends Fragment {
	private static final String MENU_DATA_EXTRA = "menuNum";
	private int menuNum;

	private TextView title;
	private TextView date;
	private TextView content;
	private TextView price;
	private Offer offer;

	
	// Empty constructor, required as per Fragment docs
	public OfferFragment() {
	}
	
	public static OfferFragment newInstance(int position) {
		
		
		final OfferFragment f = new OfferFragment();
//		final Bundle args = new Bundle();
//		args.putInt(MENU_DATA_EXTRA, menuNum);
//		f.setArguments(args);

		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//menuNum = getArguments() != null ? getArguments().getInt(MENU_DATA_EXTRA) : -1;
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
//	        getArguments().putInt(MENU_DATA_EXTRA, menuNum);
	    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateValues();

	}

	public void updateValues() {
				if (MainActivity.dataAvailable) {
					
					if (offer.getContent().equals("EMPTY")) {
						setEmptyText();
					} else {
						// titel noch über index holen
						title.setText(getResources().getStringArray(
								R.array.menu_title_entries)[offer.getOfferType()]);
						date.setText(offer.getContent());
						price.setText(offer.getPrice());
					}

				}
	}

	private void setEmptyText() {
		content.setText(R.string.notAvailable);
		title.setText("");
		date.setText("");
		price.setText("");
	}

	public void setOffer(Offer offer2) {
		offer= offer2;
		
	}

}
