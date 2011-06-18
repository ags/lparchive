package org.ags.lparchive;

import org.ags.lparchive.list.ArchiveListActivity;
import org.ags.lparchive.list.LatestListActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class LPArchiveActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, LatestListActivity.class);
		spec = tabHost.newTabSpec("latest").setIndicator(
				getString(R.string.latest_tab)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ArchiveListActivity.class);
		spec = tabHost.newTabSpec("archive").setIndicator(
				getString(R.string.archive_tab)).setContent(intent);
		tabHost.addTab(spec);

//		intent = new Intent().setClass(this, Donate.class);
//		spec = tabHost.newTabSpec("settings").setIndicator(
//				getString(R.string.settings_tab)).setContent(intent);
//		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
    }
}