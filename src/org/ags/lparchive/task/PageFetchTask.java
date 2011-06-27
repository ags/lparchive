package org.ags.lparchive.task;

import java.io.IOException;

import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class PageFetchTask extends ProgressTask {

	private static final String CONTENT_ELEMENT = "content";
	private static final String TOC_TEXT = "Table of Contents";
	protected String html;
	protected String url;
	protected boolean isIntro;
	
	public PageFetchTask(Activity activity, String url, boolean isIntro) {
		super(activity, activity.getString(R.string.fetching_wait));
		this.url = url;
		this.isIntro = isIntro;
	}

	@Override
	protected String doInBackground(Context... params) {
		try {
			Log.d("LPA", "intro page: " + isIntro);			
			Log.d("LPA", "page load from " + url);
			LPArchiveApplication lpaa = ((LPArchiveApplication)
					activity.getApplicationContext());
			
			html = getPage(url, isIntro, lpaa);
			Log.d("LPA", html);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Element getPageElement(String url, boolean isIntro,
			LPArchiveApplication lpaa) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(lpaa);
			boolean darkTheme = prefs.getBoolean("darkThemePref", false);
			boolean inDb = false;
			if (inDb) {
				// get path from db
				// read in html to string
				//Document doc = Jsoup.parse(html);
			} else {
				Document doc = Jsoup.connect(url).get();

				if (isIntro) {
					// attempt to remove the table of contents
					doc.getElementsByClass("toc").remove();
					for (Element e : doc.getElementsByTag("h1")) {
						if (TOC_TEXT.equals(e.text()))
							e.remove();
					}
					// TODO if empty, add message saying so.
				}
				
				Element content = doc.getElementById(CONTENT_ELEMENT);

				if(darkTheme) {
					content.prepend("<style type=\"text/css\">* { background: #000000; " +
							"color:#FFFFFF}</style>");
				}
				return content;
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	// get a page html, from db if can
	public static String getPage(String url, boolean isIntro,
			LPArchiveApplication lpaa) {
		return getPageElement(url, isIntro, lpaa).toString();
		
	}
}
