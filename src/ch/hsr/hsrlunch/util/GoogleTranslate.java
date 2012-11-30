package ch.hsr.hsrlunch.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class GoogleTranslate {
	private Context mainActivity;

	private final String appName = "com.google.android.apps.translate";
	private final String appMarketName = "com.google.android.translate";
	private final String intentName = "com.google.android.apps.translate.translation.TranslateActivity";

	public GoogleTranslate(Context mainActivity) {
		this.mainActivity = mainActivity;
	}

	public void startNewGoogleTranslateIntent(String content) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra("key_text_input", content);
		intent.putExtra("key_text_output", "");
		intent.putExtra("key_language_from", "de");
		intent.putExtra("key_language_to", "en");
		intent.putExtra("key_suggest_translation", "");
		intent.putExtra("key_from_floating_window", false);
		intent.setComponent(new ComponentName(appName, intentName));

		List<ResolveInfo> list = mainActivity.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() > 0) {
			mainActivity.startActivity(intent);
		} else {
			Uri uri = Uri.parse("market://details?id=" + appMarketName);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			mainActivity.startActivity(it);
		}

	}
}
