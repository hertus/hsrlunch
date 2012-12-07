package ch.hsr.hsrlunch.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import ch.hsr.hsrlunch.ui.SettingsActivity;

public class BadgeParser {
	private final String HSR_BADGE_SERVER = "https://152.96.21.52:4450/VerrechnungsportalService.svc/JSON/getBadgeSaldo";
	private SharedPreferences prefs;

	private HttpResponse response;
	private InputStream inputStream;

	public BadgeParser(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	public double parseBadge() throws UpdateBadgeException {
		DefaultHttpClient client = new MyHttpClient().getMyHttpClient();

		/* TestUser */
		/*
		 * client.getCredentialsProvider().setCredentials( new AuthScope(null,
		 * -1), new UsernamePasswordCredentials("SIFSV-80018\\ChallPUser",
		 * "1q$2w$3e$4r$5t")); HttpGet request = new HttpGet(
		 * "https://152.96.80.18/VerrechnungsportalService.svc/json/getBadgeSaldo"
		 * );
		 */
		String pw = CheapEncoder.cheapDecode(prefs.getString(
				SettingsActivity.PREF_BADGE_PASSWORD, ""));

		client.getCredentialsProvider().setCredentials(
				new AuthScope(null, -1),
				new UsernamePasswordCredentials("hsr\\"
						+ prefs.getString(SettingsActivity.PREF_BADGE_USERNAME,
								""), pw));

		HttpGet request = new HttpGet(HSR_BADGE_SERVER);

		ByteArrayOutputStream content = new ByteArrayOutputStream();
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}

			JSONObject object = new JSONObject(
					new String(content.toByteArray()));

			return object.getDouble("badgeSaldo");

		} catch (ClientProtocolException e) {
			throw new UpdateBadgeException("Error in clientProtocol: "
					+ e.getMessage());
		} catch (IOException e) {
			throw new UpdateBadgeException("IOException: " + e.getMessage());
		} catch (JSONException e) {
			throw new UpdateBadgeException("JSONException: " + e.getMessage());
		}
	}

}
