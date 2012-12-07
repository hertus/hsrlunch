package ch.hsr.hsrlunch.util;

import java.util.List;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import ch.hsr.hsrlunch.MainActivity;
import ch.hsr.hsrlunch.R;

public class GoogleTranslate {
	private MainActivity mainActivity;

	private final String appName = "com.google.android.apps.translate";
	private final String intentName = "com.google.android.apps.translate.translation.TranslateActivity";

	public GoogleTranslate(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public void startNewGoogleTranslateIntent(String content) {
		Locale locale = Locale.getDefault();
		String languageTo = locale.getLanguage();
		if (languageTo.equals("de")) {
			languageTo = "en";
		}
		Intent intentTranslate = new Intent();
		intentTranslate.setAction(Intent.ACTION_VIEW);
		intentTranslate.putExtra("key_text_input", content);
		intentTranslate.putExtra("key_text_output", "");
		intentTranslate.putExtra("key_language_from", "de");
		intentTranslate.putExtra("key_language_to", languageTo);
		intentTranslate.putExtra("key_suggest_translation", "");
		intentTranslate.putExtra("key_from_floating_window", false);
		intentTranslate.setComponent(new ComponentName(appName, intentName));

		if (isIntentAvailable(intentTranslate)) {
			mainActivity.startActivity(intentTranslate);
		} else {
			Uri uri = Uri.parse("market://search?q=" + appName);
			Intent intentMarket = new Intent(Intent.ACTION_VIEW, uri);
			if (isIntentAvailable(intentMarket)) {
				mainActivity.startActivity(intentMarket);
			} else {
				mainActivity.setAndShowErrorMsg(1,
						R.string.err_translate_failed);
			}
		}

	}

	private boolean isIntentAvailable(Intent intent) {

		List<ResolveInfo> list = mainActivity.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return (list.size() > 0);
	}
}
