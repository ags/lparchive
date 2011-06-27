package org.ags.lparchive;

import org.ags.lparchive.list.ArchiveListActivity;
import org.ags.lparchive.list.LatestListActivity;
import org.ags.lparchive.task.ArchiveFetchTask;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class LPArchiveActivity extends TabActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
	}
	
	public void createTabs() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, LatestListActivity.class);
		spec = tabHost.newTabSpec("latest").setIndicator(
				getString(R.string.latest_tab), 
				getResources().getDrawable(
						android.R.drawable.ic_menu_more)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ArchiveListActivity.class);
		spec = tabHost.newTabSpec("archive").setIndicator(
				getString(R.string.archive_tab), 
				getResources().getDrawable(
						android.R.drawable.ic_menu_more)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(getString(R.string.donate_url)));
		spec = tabHost.newTabSpec("donate").setIndicator(
				getString(R.string.donate_tab), 
				getResources().getDrawable(
						android.R.drawable.ic_menu_more)).setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}
	
	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
		LPArchiveApplication appState = ((LPArchiveApplication) 
				getApplicationContext());
		if (appState.getFirstRun()) {
			new ArchiveFetchTask(this).execute();
		} else {
			Log.d("LPA", "already fetched!");
			createTabs();
		}
	}
}