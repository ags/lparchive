package org.ags.lparchive.page;

import org.ags.lparchive.LPArchiveApplication;

public class DonatePageActivity extends PageActivity {

	@Override
	protected void loadPage() {
		webview.loadUrl(LPArchiveApplication.donateURL);
	}

}
