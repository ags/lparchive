package org.ags.lparchive;

import org.ags.lparchive.task.PageFetchTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class LPPageActivity extends Activity {
	private WebView webview;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_page);
		webview = (WebView) findViewById(R.id.lp_page);
		// permit zooming and zoom out so images fit screen width
		if (savedInstanceState != null) {
			webview.restoreState(savedInstanceState);
		} else {
			webview.getSettings().setBuiltInZoomControls(true);
			webview.getSettings().setLoadWithOverviewMode(true);
			webview.getSettings().setUseWideViewPort(true);
			// get the content of the relevant LP, either from web or TODO db
			// TODO images should be stored on the sd card and refered to by uri
			Bundle extras = getIntent().getExtras();
			String page_url = extras.getString("page_url");
			Log.d("LPA", "page: " + page_url);
			boolean isIntro = extras.getBoolean("is_intro");
			new LoadPageFetchTask(this, page_url, isIntro).execute();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		webview.saveState(outState);
	}

	class LoadPageFetchTask extends PageFetchTask {
		public LoadPageFetchTask(Activity activity, String url, boolean isIntro) {
			super(activity, url, isIntro);
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
//			Log.d("LPA", html);
			webview.loadDataWithBaseURL(url, html, "text/html",
					"utf-8", null);
		}
	}
}
