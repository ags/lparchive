package org.ags.lparchive.page;

import org.ags.lparchive.LPArchiveApplication;

import android.os.Bundle;

public class DonatePageActivity extends PageActivity {
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadPage();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadPage() {
		webview.loadUrl(LPArchiveApplication.donateURL);
	}

}
