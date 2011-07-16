package org.ags.lparchive.page;

import org.ags.lparchive.RetCode;
import org.ags.lparchive.task.PageFetchTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SimplePageActivity extends PageActivity {
	private String pageUrl;
	
	public static Intent newInstance(Context context, String pageUrl) {
		Intent i = new Intent(context, SimplePageActivity.class);
		i.putExtra("pageUrl", pageUrl);
		return i;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			pageUrl = extras.getString("pageUrl");

			loadPage();
		}
	}
	
	protected void loadPage() {
    	new LoadPageFetchTask(this, pageUrl).execute();
	}
	
	class LoadPageFetchTask extends PageFetchTask {
		public LoadPageFetchTask(Activity activity, String url) {
			super(activity, url, false);
		}

		protected void onPostExecute(RetCode result) {
			super.onPostExecute(result);
			if (result.equals(RetCode.SUCCESS)) {
				webview.loadDataWithBaseURL(url, html, "text/html", "utf-8",
						null);
			}
		}
	}
}
