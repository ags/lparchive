package org.ags.lparchive;

import android.app.Application;
import android.content.SharedPreferences;

public class LPArchiveApplication extends Application {
	
	private DataHelper dataHelper;
	private SharedPreferences mPrefs;

	public static final String donateURL = "http://lparchive.org/donate";
	public static final String baseURL = "http://lparchive.org";
	public static final String introURL = "/introduction";

	public static final String LATEST_LIST_ACTION = "org.ags.lparchive.LATEST_LIST_ACTION";
	public static final String ARCHIVE_LIST_ACTION = "org.ags.lparchive.ARCHIVE_LIST_ACTION";
	public static final String FAVORITE_LIST_ACTION = "org.ags.lparchive.FAVORITE_LIST_ACTION";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		this.dataHelper = new DataHelper(this);
		mPrefs = getSharedPreferences("LPAPrefs", 0);
	}

	public DataHelper getDataHelper() {
		return this.dataHelper;
	}

	/**
	 * Set whether the application has been launched before.
	 */
	public void setFirstRun(boolean firstRun) {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putBoolean("firstRun", firstRun);
		edit.commit();
	}

	public boolean getFirstRun() {
		return mPrefs.getBoolean("firstRun", true);
	}

}
