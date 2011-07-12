package org.ags.lparchive.task;

import java.io.IOException;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveActivity;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
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

	protected String doInBackground(Void... unused) {
		Document doc = null;
		try {
			Log.d(TAG, "begin document fetch");
			doc = Jsoup.connect(context.getString(R.string.base_url)).get();
			Log.d(TAG, "retrieved document");
		} catch (IOException e) {
			Log.e(TAG, "failed to retrieve document");
			e.printStackTrace();
		}
		LPArchiveApplication appState = ((LPArchiveApplication) 
				context.getApplicationContext());

		DataHelper dh = appState.getDataHelper();
		
		// parse the page for "archive" entries
		dh.getDb().beginTransaction();
		String url, game, author, type;
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
			long lp_id = dh.insertLetsPlay(game, author, url, type
					.toLowerCase());
			// TODO check for -1
			for (Element tag : e.getElementsByClass("tag")) {
				dh.addTag(lp_id, tag.text());
			}
			Log.d(TAG, "inserted LP " + lp_id);
		}
		dh.getDb().setTransactionSuccessful();
		dh.getDb().endTransaction();
		Log.d(TAG, "archive built");
		
		// parse the page for "latest" entries
		dh.getDb().beginTransaction();
		Element latest = doc.getElementById(LATEST_DIV);
		for (Element e : latest.getElementsByTag("li")) {
			game = e.getElementsByTag("strong").first().text();
			author = e.getElementsByTag("span").first().text().substring(3);
			dh.markRecentLetsPlay(game, author);
		}
		dh.getDb().setTransactionSuccessful();
		dh.getDb().endTransaction();
		Log.d(TAG, "latest built");
		
		// application has run
		appState.setFirstRun(false);
		return "done";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// TODO switch to enum
		if(!result.equals("done")) {
			Toast.makeText(context, context.getString(R.string.timeout_error), 
					Toast.LENGTH_LONG).show();
		} else {
			Log.d(TAG, "making tabs");
			((LPArchiveActivity)context).createTabs();
		}
	}
}
