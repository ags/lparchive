package org.ags.lparchive.task;

import java.io.IOException;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveActivity;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.RetCode;
import org.ags.lparchive.DataHelper.DuplicateLPException;
import org.ags.lparchive.LPArchiveApplication.LPTypes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class ArchiveFetchTask extends ProgressTask {
	private static final String LATEST_DIV = "latest";
	public static final String TAG = "ArchiveFetchTask";
	
	public ArchiveFetchTask(Activity activity) {
		super(activity, activity.getString(R.string.fetching_wait));
	}

	protected RetCode doInBackground(Void... unused) {
		Document doc = null;
		try {
			Log.d(TAG, "begin document fetch");
			doc = Jsoup.connect(LPArchiveApplication.baseURL).get();
			Log.d(TAG, "retrieved document");
		} catch (IOException e) {
			Log.e(TAG, "failed to retrieve document");
			e.printStackTrace();
			return RetCode.FETCH_FAILED;
		}
		LPArchiveApplication appState = ((LPArchiveApplication) 
				context.getApplicationContext());

		DataHelper dh = appState.getDataHelper();
		
		// parse the page for "archive" entries
		dh.getDb().beginTransaction();
		String url, game, author, type;
		// TODO move lp_id to outside loop
		for (Element e : doc.getElementsByTag("tr")) {
			Elements e_url = e.getElementsByClass("lp");
			// skip blank urls
			if (e_url.isEmpty())
				continue;
			url = e_url.first().attr("href");
			// skip the random page
			if (url.equals("/random"))
				continue;
			game = e.getElementsByTag("strong").first().text();
			author = e.getElementsByTag("span").first().text().substring(3);
			type = e.getElementsByTag("img").first().attr("alt");
			// insert
			try {
				long lp_id = dh.insertLetsPlay(game, author, url, 
						LPTypes.valueOf(type.toUpperCase()));
				if (lp_id != -1) {
					for (Element tag : e.getElementsByClass("tag"))
						dh.addTag(lp_id, tag.text());
					Log.d(TAG, "inserted LP " + lp_id);
				} else {
					Log.e(TAG, "failed to insert " + game);
				}
			} catch (DuplicateLPException ex) {
				Log.w(TAG, ex.getMessage());
			}
		}
		dh.getDb().setTransactionSuccessful();
		dh.getDb().endTransaction();
		Log.d(TAG, "archive built");
		
		// parse the page for "latest" entries
		dh.getDb().beginTransaction();
		Element latest = doc.getElementById(LATEST_DIV);
		long id;
		for (Element e : latest.getElementsByTag("li")) {
			game = e.getElementsByTag("strong").first().text();
			author = e.getElementsByTag("span").first().text().substring(3);
			id = dh.getID(game, author);
			if(id != -1)
				dh.markRecentLetsPlay(id);
		}
		dh.getDb().setTransactionSuccessful();
		dh.getDb().endTransaction();
		Log.d(TAG, "latest built");
		
		// application has run
		appState.setFirstRun(false);
		return RetCode.SUCCESS;
	}

	@Override
	protected void onPostExecute(RetCode result) {
		super.onPostExecute(result);
		switch (result) {
		case FETCH_FAILED:
			Toast.makeText(context, context.getString(R.string.timeout_error),
					Toast.LENGTH_LONG).show();
			break;
		case SUCCESS:
			Log.d(TAG, "making tabs");
			((LPArchiveActivity) context).createTabs();
			break;
		default:
			break;
		}
	}
}
