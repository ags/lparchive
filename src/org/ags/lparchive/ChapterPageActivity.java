package org.ags.lparchive;

import org.ags.lparchive.task.PageFetchTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ChapterPageActivity extends PageActivity {
	private String page_url;
	private boolean isIntro;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			isIntro = extras.getBoolean("is_intro");
			page_url = extras.getString("page_url");
			loadPage();
		}
	}
	
	private void loadPage() {
		new LoadPageFetchTask(this, page_url, isIntro).execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_chapter, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.refresh_chapter:
	    	loadPage();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
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
			Log.d("LPA", "html: " + html);
			Log.d("LPA", "webview null: " + (webview == null));
			Log.d("LPA", "url null: " + (url == null));
			Log.d("LPA", "html null: " + (html == null));
			webview.loadDataWithBaseURL(url, html, "text/html",
					"utf-8", null);
		}
	}
}
