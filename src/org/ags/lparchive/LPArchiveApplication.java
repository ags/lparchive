package org.ags.lparchive;

import android.app.Application;
import android.content.SharedPreferences;

public class LPArchiveApplication extends Application {
	private DataHelper dataHelper;
	private SharedPreferences mPrefs;

	@Override
	public void onCreate() {
		super.onCreate();
		this.dataHelper = new DataHelper(this);
		mPrefs = getSharedPreferences("LPAPrefs", 0);
//		mPrefs.edit().clear();
//		mPrefs.edit().commit();
	}

	public DataHelper getDataHelper() {
		return this.dataHelper;
	}

	public void setRunned() {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putBoolean("firstRun", false);
		edit.commit();
	}

	public boolean getFirstRun() {
		return mPrefs.getBoolean("firstRun", true);
	}

}
