package ch.hsr.hsrlunch.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import ch.hsr.hsrlunch.model.MyHttpClient;
import ch.hsr.hsrlunch.ui.SettingsActivity;
import ch.hsr.hsrlunch.util.OnBadgeResultListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class BadgeUpdater extends AsyncTask<Void,Void, Void> {

	private OnBadgeResultListener listener;
	private PersistenceFactory persistenceFactory;
	private HttpResponse response;
	private InputStream inputStream;
	private Context maincontext;

	@Override
	protected Void doInBackground(Void... arg0) {
		DefaultHttpClient client =new MyHttpClient().getMyHttpClient();
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		client.getCredentialsProvider().setCredentials(new AuthScope(null, -1),new UsernamePasswordCredentials("SIFSV-80018\\ChallPUser", "1q$2w$3e$4r$5t"));
		HttpGet request = new HttpGet("https://152.96.80.18/VerrechnungsportalService.svc/json/getBadgeSaldo");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(maincontext);
//		client.getCredentialsProvider().setCredentials(new AuthScope(null, -1),new UsernamePasswordCredentials(prefs.getString(SettingsActivity.PREF_BADGE_USERNAME, ""), prefs.getString(SettingsActivity.PREF_BADGE_PASSWORD, "")));
//		client.getCredentialsProvider().setCredentials(new AuthScope(null, -1),new UsernamePasswordCredentials("hsr\\c1buechi", ""));
//		HttpGet request = new HttpGet("https://152.96.21.52:4450/VerrechnungsportalService.svc/JSON/getBadgeSaldo");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
			
			JSONObject object = new JSONObject(new String(content.toByteArray()));
			System.out.println("BadgeVermögen: " + object.getDouble("badgeSaldo"));
			persistenceFactory.updateBadgeEntry(object.getDouble("badgeSaldo"), new Date().getTime());
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// Handle falls keine Inet-Verbindung ermöglicht werden kann!
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// Handle falls der Wert nicht geparst werden kann
			e.printStackTrace();
		}
		return null;
	}
	
    @Override
    protected void onPostExecute(Void arg0) {
      listener.onBadgeUpdate();
    }

	public void setListener(OnBadgeResultListener listener) {
		this.listener = listener;		
	}
	
	public void setContext(Context main) {
		this.maincontext = main;
	}

	public void setBackend(PersistenceFactory persistenceFactory) {
		this.persistenceFactory = persistenceFactory;		
	}

}
