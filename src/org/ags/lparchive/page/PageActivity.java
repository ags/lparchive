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

/**
 * Wrapper for a WebView that permits orientation changes without reloading a
 * page. Zoom controls, a wide view port and overview mode are enabled, along
 * with a minimal options menu for refreshing/displaying application
 * preferences.
 */
public abstract class PageActivity extends Activity {
	protected WebView webview;
	
	protected abstract void loadPage();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_page);
		webview = (WebView) findViewById(R.id.lp_page);
		/*
		 * restoring state this way allows the orientation to change without
		 * refreshing the WebView.
		 */
		if (savedInstanceState != null) {
			webview.restoreState(savedInstanceState);
		} else {
			// permit zooming and zoom out so images fit screen width
			webview.getSettings().setBuiltInZoomControls(true);
			webview.getSettings().setLoadWithOverviewMode(true);
			webview.getSettings().setUseWideViewPort(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		webview.saveState(outState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_page, menu);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
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
