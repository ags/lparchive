package org.ags.lparchive;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * The user preferences screen.
 */
public class Preferences extends PreferenceActivity {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
	}
}
