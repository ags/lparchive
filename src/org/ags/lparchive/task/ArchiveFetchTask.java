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
import android.content.Context;
import android.util.Log;

public class ArchiveFetchTask extends ProgressTask {
	private static final String LATEST_DIV = "latest";

	public ArchiveFetchTask(Activity activity) {
		super(activity, activity.getString(R.string.fetching_wait));
	}
	
	protected String doInBackground(Context... params) {
		LPArchiveApplication appState = ((LPArchiveApplication) 
				activity.getApplicationContext());

		DataHelper dh = appState.getDataHelper();
		try {
			Log.d("LPA", "get doc");
			Document doc = Jsoup.connect(activity.getString(R.string.base_url)).get();
			Log.d("LPA", "got doc");
			dh.getDb().beginTransaction();
			String url, game, author, type;
			for(Element e : doc.getElementsByTag("tr")) {
				Elements e_url = e.getElementsByClass("lp");
				if(e_url.isEmpty())
					continue;
				//url = game = author = type = "";
				url = e_url.first().attr("href");
				if(url.equals("/random"))
					continue;
				game = e.getElementsByTag("strong").first().text();
				author = e.getElementsByTag("span").first().text().substring(3);
				type = e.getElementsByTag("img").first().attr("alt");
				// insert
				long lp_id = dh.insertLetsPlay(game, author, url, type.toLowerCase());
				// TODO check for -1
				for(Element tag : e.getElementsByClass("tag")) {
					dh.addTag(lp_id, tag.text());	
				}
			}
			dh.getDb().setTransactionSuccessful();
			dh.getDb().endTransaction();
			
			dh.getDb().beginTransaction();
			Element latest = doc.getElementById(LATEST_DIV);
			for(Element e : latest.getElementsByTag("li")) {
				game = e.getElementsByTag("strong").first().text();
				author = e.getElementsByTag("span").first().text().substring(3);
				dh.markRecentLetsPlay(game, author);
			}
			dh.getDb().setTransactionSuccessful();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dh.getDb().endTransaction();
		}
		appState.setRunned();
		return "done";
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.d("LPA", "AFT post exec, creating tabs");
		((LPArchiveActivity)activity).createTabs();
	}

}
