package org.ags.lparchive.page;

import org.ags.lparchive.Preferences;
import org.ags.lparchive.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

public abstract class PageActivity extends Activity {
	protected WebView webview;
	
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
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		webview.saveState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_page, menu);
		return true;
	}
	
	protected abstract void loadPage();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.refresh_page:
	    	// reload this page
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
	
}
