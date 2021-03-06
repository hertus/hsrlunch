package ch.hsr.hsrlunch.ui;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import ch.hsr.hsrlunch.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CreditActivity extends SherlockFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.credits);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		TextView tvVersion = (TextView) findViewById(R.id.TextViewVersion);
		try {
			tvVersion
					.setText(getResources().getString(R.string.version)
							+ " : "
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		TextView rating = (TextView) findViewById(R.id.rating);
		rating.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=ch.hsr.hsrlunch"));
				startActivity(intent);
			}
		});

		TextView tvLibrary = (TextView) findViewById(R.id.library);
		String[] Arraylibary = getResources().getStringArray(R.array.libraries);
		String str = new String();
		for (int i = 0; i < Arraylibary.length; i++) {
			str += (Arraylibary[i] + "\n");
		}
		tvLibrary.setText(str);

	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
