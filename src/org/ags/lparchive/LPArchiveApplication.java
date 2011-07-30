package org.ags.lparchive;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * A browser for the Let's Play Archive (lparchive.org)
 * @author Alex Smith
 */
public class LPArchiveApplication extends Application {
	
	private DataHelper dataHelper;
	private SharedPreferences prefs;

	public static final String donateURL = "http://lparchive.org/donate";
	public static final String baseURL = "http://lparchive.org";
	public static final String introURL = "/introduction";

	public static final String LATEST_LIST_ACTION = "org.ags.lparchive.LATEST_LIST_ACTION";
	public static final String ARCHIVE_LIST_ACTION = "org.ags.lparchive.ARCHIVE_LIST_ACTION";
	public static final String FAVORITE_LIST_ACTION = "org.ags.lparchive.FAVORITE_LIST_ACTION";
	public static final String ARCHIVE_REFRESH = "org.ags.lparchive.ARCHIVE_REFRESH";
	
	public static final boolean DEBUG = true;
	
	public static enum LPTypes {
		SCREENSHOT, VIDEO, TEXT, HYBRID
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		dataHelper = new DataHelper(this);
		prefs = getSharedPreferences("LPAPrefs", 0);
	}
	
	/**
	 * @return DataHelper object
	 */
	public DataHelper getDataHelper() {
		return dataHelper;
	}

	/**
	 * Set whether the application has been launched before.
	 */
	public void setFirstRun(boolean firstRun) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean("firstRun", firstRun);
		edit.commit();
	}
	
	/**
	 * @return True if this is the first time the application has been run.
	 */
	public boolean getFirstRun() {
		return prefs.getBoolean("firstRun", true);
	}
	
	/**
	 * @return The resource id for a given LP types icon, or -1 if none match.
	 */
	public static int getIconResource(LPTypes type) {
		switch (type) {
		case TEXT:
			return R.drawable.icon_text;
		case SCREENSHOT:
			return R.drawable.icon_screenshot;
		case VIDEO:
			return R.drawable.icon_video;
		case HYBRID:
			return R.drawable.icon_hybrid;
		default:
			return -1;
		}
	}
}
