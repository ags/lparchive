package org.ags.lparchive;

import android.os.Bundle;

public class DonatePageActivity extends PageActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (savedInstanceState == null) {
			webview.loadUrl(LPArchiveApplication.donateURL);
//		}
	}
}
