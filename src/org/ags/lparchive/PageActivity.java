package org.ags.lparchive;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PageActivity extends Activity {
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
}
