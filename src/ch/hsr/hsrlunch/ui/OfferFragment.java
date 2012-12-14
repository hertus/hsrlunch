package ch.hsr.hsrlunch.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.hsr.hsrlunch.R;
import ch.hsr.hsrlunch.model.Offer;

public class OfferFragment extends Fragment {

	private TextView title;
	private TextView date;
	private TextView content;
	private TextView price;
	private Offer offer;
	private String dayText;

	public OfferFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void updateValues() {
		if (offer != null && title != null) {
			if (offer.getContent().equals("EMPTY")) {
				setEmptyText();
			} else {

				title.setText(getResources().getStringArray(
						R.array.menu_title_entries)[offer.getOfferType()]);
				date.setText(dayText);
				content.setText(offer.getContent());
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
		offer = offer2;
	}

	public void setDayString(String dateString) {
		dayText = dateString;

	}

}
