package org.ags.lparchive.task;

import java.io.IOException;

import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.RetCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public abstract class PageFetchTask extends ProgressTask {

	private static final String CONTENT_ELEMENT = "content";
	private static final String TOC_TEXT = "Table of Contents";
	
	// TODO read in from assets
	private static final String CSS = "<style type=\"text/css\">" +
	"* { font-family: Verdana, Arial, sans-serif; }" +
	"a { color: #a62625; font-weight: bold; text-decoration: none; border-bottom: 1px dotted #a62625; }" +
	"table { margin: 0 -2.5em; }" +
	"p,  ul,  blockquote { margin: 1em 0; }" +
	"table td,  ul.toc li { padding: .9em 3em; }" +
	"h1, h2, h3 { font-weight: 700; }</style>";
	private static final String CSS_DARK = "<style type=\"text/css\">" +
	"*{background: #000000;color:#FFFFFF}</style>";
	
	protected String html;
	protected String url;
	protected boolean isIntro;
	
	public PageFetchTask(Context context, String url, boolean isIntro) {
		super(context, context.getString(R.string.fetching_wait));
		this.url = url;
		this.isIntro = isIntro;
	}

	@Override
	protected RetCode doInBackground(Void... unused) {
		try {	
			Log.d("LPA", "intro page? " + isIntro + " page load from " + url);
			LPArchiveApplication lpaa = ((LPArchiveApplication)
					context.getApplicationContext());
			
			html = getPage(url, isIntro, lpaa);
			return RetCode.SUCCESS;
		} catch (IOException e) {
			e.printStackTrace();
			return RetCode.FETCH_FAILED;
		}
	}

	@Override
	protected void onPostExecute(RetCode result) {
		super.onPostExecute(result);

		if (result.equals(RetCode.FETCH_FAILED)) {
			Toast.makeText(context, context.getString(R.string.timeout_error),
					Toast.LENGTH_LONG).show();
		}
	}

	// get a page html, from db if can
	public static String getPage(String url, boolean isIntro,
			LPArchiveApplication lpaa) throws IOException {
		Element e = getPageElement(url, isIntro, lpaa);
		return (e != null) ? e.toString() : null;
	}
	
	public static Element getPageElement(String url, boolean isIntro,
			LPArchiveApplication lpaa) throws IOException {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(lpaa);
		boolean darkTheme = prefs.getBoolean("darkThemePref", false);
		boolean inDb = false;
		if (inDb) {
			// get path from db
			// read in html to string
			// Document doc = Jsoup.parse(html);
			return null;
		} else {
			Document doc = Jsoup.connect(url).get();

			if (isIntro) {
				// attempt to remove the table of contents
				doc.getElementsByClass("toc").remove();
				for (Element e : doc.getElementsByTag("h1")) {
					if (TOC_TEXT.equals(e.text()))
						e.remove();
				}
				// TODO if empty, add message saying so (or check intros when getting chapter list)
			}

			Element content = doc.getElementById(CONTENT_ELEMENT);
			
			content.prepend(CSS);
			if (darkTheme)
				content.prepend(CSS_DARK);
			
			return content;
		}
	}

}
