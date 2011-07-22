package org.ags.lparchive.page;

import org.ags.lparchive.RetCode;
import org.ags.lparchive.task.PageFetchTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Displays a bundled URL in a WebView, with the same properties as
 * {@link PageActivity}
 */
public class SimplePageActivity extends PageActivity {
	private String pageUrl;
	
	public static Intent newInstance(Context context, String pageUrl) {
		Intent i = new Intent(context, SimplePageActivity.class);
		i.putExtra("pageUrl", pageUrl);
		return i;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			pageUrl = extras.getString("pageUrl");
			loadPage();
		}
	}
	
	/** Loads a page URL into the WebView. */
	protected void loadPage() {
    	new LoadPageFetchTask(this, pageUrl).execute();
	}
	
	/** Fetches & loads a page into this activities WebView. */
	class LoadPageFetchTask extends PageFetchTask {
		public LoadPageFetchTask(Activity activity, String url) {
			super(activity, url, false);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(RetCode result) {
			super.onPostExecute(result);
			if (result.equals(RetCode.SUCCESS)) {
				webview.loadDataWithBaseURL(url, html, "text/html", "utf-8",
						null);
			}
		}
	}
}
