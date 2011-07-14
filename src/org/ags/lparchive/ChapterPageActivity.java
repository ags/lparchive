package org.ags.lparchive;

import org.ags.lparchive.task.PageFetchTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ChapterPageActivity extends PageActivity {
	private String page_url;
	private boolean isIntro, atFirst, atLast;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			isIntro = extras.getBoolean("is_intro");
			atFirst = extras.getBoolean("atFirst");
			atLast = extras.getBoolean("atLast");
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
	    if(atFirst)
	    	menu.getItem(0).setEnabled(false);
	    if(atLast)
	    	menu.getItem(1).setEnabled(false);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.prev_chapter:
	    	// load previous chapter
	    	return true;
	    case R.id.next_chapter:
	    	// load next chapter
	    	return true;
	    case R.id.refresh_chapter:
	    	// reload this chapter
	    	loadPage();
	    	return true;
	    case R.id.preferences:
	    	// display preferences
	    	startActivity(new Intent(this, Preferences.class));
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
