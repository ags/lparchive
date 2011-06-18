package org.ags.lparchive;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class LPPageActivity extends Activity {
	private WebView webview;
	
	protected void onCreate(Bundle savedInstanceState, LetsPlay lp) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_page);
		this.webview = (WebView)findViewById(R.id.lp_page);
		// get the content of the relevant LP, either from web or TODO db
		
		
//		webview.loadDataWithBaseURL(base_url, data, mimeType, encoding, historyUrl)
	}
	
}
