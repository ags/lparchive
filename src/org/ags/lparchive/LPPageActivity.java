package org.ags.lparchive;

import org.ags.lparchive.task.ProgressTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class LPPageActivity extends Activity {
	private WebView webview;
	private String chapter_url;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_page);
		webview = (WebView) findViewById(R.id.lp_page);
		// permit zooming
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);

		// get the content of the relevant LP, either from web or TODO db
		// TODO images should be stored on the sd card and refered to by uri
		Bundle extras = getIntent().getExtras();
		chapter_url = extras.getString("chapter_url");
		Log.d("LPA", "page: " + chapter_url);
		ProgressTask initTask = new ChapterFetchTask(this, chapter_url);
		initTask.execute(this);
	}

	class ChapterFetchTask extends ProgressTask {
		private static final String CONTENT_ELEMENT = "content";
		private Element data;

		public ChapterFetchTask(Activity activity, String url) {
			super(activity, activity.getString(R.string.fetching_wait));
		}

		@Override
		protected String doInBackground(Context... params) {
			try {
				Log.d("LPA", "page load from " + chapter_url);
				Document doc = Jsoup.connect(chapter_url).get();
				data = doc.getElementById(CONTENT_ELEMENT);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

//			Log.d("lpa", data.toString());
			webview.loadDataWithBaseURL(chapter_url, data.toString(),
					"text/html", "utf-8", null);
		}
	}
}
