package org.ags.lparchive;

import android.app.Application;

public class LPArchiveApplication extends Application {
	private DataHelper dataHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		this.dataHelper = new DataHelper(this);
	}

	public DataHelper getDataHelper() {
		return this.dataHelper;
	}
}
