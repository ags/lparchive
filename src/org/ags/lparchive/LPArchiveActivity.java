package org.ags.lparchive;

import org.ags.lparchive.list.ArchiveListActivity;
import org.ags.lparchive.list.LatestListActivity;
import org.ags.lparchive.task.ArchiveFetchTask;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class LPArchiveActivity extends TabActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.main);
		
//		TabHost tabHost = getTabHost();
//		TabHost.TabSpec spec;
//		Intent intent;
//
//		intent = new Intent().setClass(this, LatestListActivity.class);
//		spec = tabHost.newTabSpec("latest").setIndicator(
//				getString(R.string.latest_tab)).setContent(intent);
//		tabHost.addTab(spec);
//
//		intent = new Intent().setClass(this, ArchiveListActivity.class);
//		spec = tabHost.newTabSpec("archive").setIndicator(
//				getString(R.string.archive_tab)).setContent(intent);
//		tabHost.addTab(spec);
		
		
		// intent = new Intent().setClass(this, Donate.class);
		// spec = tabHost.newTabSpec("settings").setIndicator(
		// getString(R.string.settings_tab)).setContent(intent);
		// tabHost.addTab(spec);
	}

	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
		// base this on a last updated field
		if(true) {
			new ArchiveFetchTask(this).execute();
		}
	}
}