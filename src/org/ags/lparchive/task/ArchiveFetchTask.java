package org.ags.lparchive.task;

import java.io.IOException;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.DataHelper.DuplicateLPException;
import org.ags.lparchive.LPArchiveActivity;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPArchiveApplication.LPTypes;
import org.ags.lparchive.R;
import org.ags.lparchive.RetCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Parses lparchive.org and stores attributes of all LPs found. Any stored
 * 'latest' LPs are flushed and replaced with any new ones found. Re-running
 * the task should skip any existing LPs.
 */
public class ArchiveFetchTask extends ProgressTask {
	public static final String TAG = "ArchiveFetchTask";
	private static final String LATEST_DIV = "latest";
	// author in page is 'by xxxx' - remove first 3 characters
	private static final int AUTHOR_START = 3;
	private static final int JSON_START = 8;
	
	public ArchiveFetchTask(Context context) {
		super(context, context.getString(R.string.archive_fetch));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RetCode doInBackground(Void... unused) {
		Document doc = null;
		JSONArray lpJson = null;
		try {
			Log.d(TAG, "begin document fetch");
			doc = Jsoup.connect(LPArchiveApplication.baseURL).get();
			Log.d(TAG, "retrieved document");
			for (Element e : doc.getElementsByTag("script")) {
				if (e.html().startsWith("tocdata")) {
					String json = e.html().substring(JSON_START);
					lpJson = new JSONArray(json);
				}
			}

		} catch (IOException e) {
			Log.e(TAG, "failed to retrieve document");
			e.printStackTrace();
			return RetCode.FETCH_FAILED;
		} catch (JSONException e) {
			Log.e(TAG, "failed to retrieve document");
			e.printStackTrace();
			return RetCode.FETCH_FAILED;
		}
		
		LPArchiveApplication appState = ((LPArchiveApplication) 
				context.getApplicationContext());
		DataHelper dh = appState.getDataHelper();
		
		// manually setup db for bulk insertion to speed things up
		dh.getDb().beginTransaction();
		String url, game, author, type;
		long id = -1;
		// parse the page for "archive" entries
		for (int i = 0; i < lpJson.length(); i++) {
			try {
				JSONObject lp = lpJson.getJSONObject(i);
				game = lp.getString("t");
				type = tyToType(lp.getString("ty"));
				url = lp.getString("u");
				author = lp.getString("a");
				
				id = dh.insertLetsPlay(game, author, url, 
						LPTypes.valueOf(type));
				// if we succeeded creating an LP, extract & add any tags
				if (id != -1) {
					JSONArray tags = lp.getJSONArray("tg");
					for(int j = 0; j < tags.length(); j++) {
						dh.addTag(id, tags.getString(j));
					}
					Log.d(TAG, "inserted LP " + id);
				} else {
					Log.e(TAG, "failed to insert " + game);
				}
			} catch (JSONException e) {
				Log.w(TAG, e.getMessage());
			} catch (DuplicateLPException ex) {
//				Log.w(TAG, ex.getMessage());
			}
		}

		// commit  all insertions
		dh.getDb().setTransactionSuccessful();
		dh.getDb().endTransaction();
		
		// parse the page for "latest" entries	
		dh.getDb().beginTransaction();
		Element latest = doc.getElementById(LATEST_DIV);
		
		/* extract game/author pairs, lookup in DB. if there's an entry, mark it
		 * as a 'latest' LP */
		for (Element e : latest.getElementsByTag("li")) {
			game = e.getElementsByTag("strong").first().text();
			author = e.getElementsByTag("span").first().text().substring(
					AUTHOR_START);
			id = dh.getID(game, author);
			if(id != -1) {
				dh.markLatestLP(id);
			}
		}
		dh.getDb().setTransactionSuccessful();
		dh.getDb().endTransaction();
		
		// application has run TODO not really first run
		appState.setFirstRun(false);
		return RetCode.SUCCESS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostExecute(RetCode result) {
		super.onPostExecute(result);
		switch (result) {
		case FETCH_FAILED:
			Toast.makeText(context, context.getString(R.string.timeout_error),
					Toast.LENGTH_LONG).show();
			break;
		case SUCCESS:
			// setup tab view
			((LPArchiveActivity) context).createTabs();
			// announce archive data may have changed
			Intent i = new Intent(LPArchiveApplication.ARCHIVE_REFRESH);
			context.sendBroadcast(i);
			break;
		default:
			break;
		}
	}
	
	private String tyToType(String ty) {
		switch (Integer.valueOf(ty)) {
		case 0:
			return "TEXT";
		case 1:
			return "SCREENSHOT";
		case 2:
			return "VIDEO";
		case 3:
			return "HYBRID";
		default:
			return "UNKNOWN";
		}		
	}
}
