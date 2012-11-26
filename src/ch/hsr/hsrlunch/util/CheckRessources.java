package ch.hsr.hsrlunch.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class CheckRessources {

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static boolean isOnHSRwifi(Context context) {

		if (isOnline(context)) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();

			String ssid = wifiInfo.getSSID();
			if(ssid == null)
				return false;
			Log.d("Wifi", "Connected with SSID: " + ssid);
			if (ssid.equals("HSR-Secure")) {
				Log.d("Wifi", "Connected in HSR-Secure");
				return true;
			}
		}
		return false;
	}
}
