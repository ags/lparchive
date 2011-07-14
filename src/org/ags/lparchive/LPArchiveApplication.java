package org.ags.lparchive;

import android.app.Application;
import android.content.SharedPreferences;

public class LPArchiveApplication extends Application {
	private DataHelper dataHelper;
	private SharedPreferences mPrefs;
    
    public static final String donateURL = "http://lparchive.org/donate";
    public static final String baseURL = "http://lparchive.org";
  	public static final String introURL = "/introduction";
    
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
