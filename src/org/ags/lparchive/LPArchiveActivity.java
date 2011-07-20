package org.ags.lparchive;

import org.ags.lparchive.list.LPListActivity;
import org.ags.lparchive.page.DonatePageActivity;
import org.ags.lparchive.task.ArchiveFetchTask;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class LPArchiveActivity extends TabActivity {
	private static final String TAG = "LPArchiveActivity";
	private int tagMenuSelected = 0;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
	}
	
	public void createTabs() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		tabHost.clearAllTabs();
		
		intent = new Intent(this, LPListActivity.class)
				.setAction(LPArchiveApplication.LATEST_LIST_ACTION);
		spec = tabHost.newTabSpec("latest").setIndicator(
				getString(R.string.tab_latest),
				getResources().getDrawable(R.drawable.ic_tab_latest))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent(this, LPListActivity.class)
				.setAction(LPArchiveApplication.ARCHIVE_LIST_ACTION);
		spec = tabHost.newTabSpec("archive").setIndicator(
				getString(R.string.tab_archive),
				getResources().getDrawable(R.drawable.ic_tab_archive))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent(this, LPListActivity.class)
		.setAction(LPArchiveApplication.FAVORITE_LIST_ACTION);
		spec = tabHost.newTabSpec("favs").setIndicator(
				getString(R.string.tab_favs),
				getResources().getDrawable(R.drawable.ic_tab_star))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent(this, DonatePageActivity.class);
		spec = tabHost.newTabSpec("donate").setIndicator(
				getString(R.string.tab_donate),
				getResources().getDrawable(R.drawable.ic_tab_donate))
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
		LPArchiveApplication appState = ((LPArchiveApplication) 
				getApplicationContext());
		if (appState.getFirstRun()) {
			new ArchiveFetchTask(this).execute();
		} else {
			Log.d("LPA", "already fetched!!");
			createTabs();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_main, menu);
	    return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.search_archive:
	    	getTabHost().setCurrentTab(1);
	    	getCurrentActivity().onSearchRequested();
	    	return true;
	    case R.id.tag_filter:
	    	getTabHost().setCurrentTab(1);
	    	createTagDialog().show();
	    	return true;
	    case R.id.refresh_archive:
			LPArchiveApplication appState = ((LPArchiveApplication) 
					getApplicationContext());
	    	appState.getDataHelper().clearLatest();
	    	Log.d(TAG, "latest cleared");
	    	new ArchiveFetchTask(this).execute();
	    	return true;
	    case R.id.preferences:
	    	startActivity(new Intent(this, Preferences.class));
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private AlertDialog createTagDialog() {
		final LPListActivity act = (LPListActivity) 
			getCurrentActivity();
		return new AlertDialog.Builder(act)
        .setTitle(getString(R.string.tag_dialog_title))
        .setSingleChoiceItems(R.array.lp_tags, tagMenuSelected, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tagMenuSelected = which;
				act.tagFilter(tagMenuSelected);
				dialog.dismiss();
			}
		})
        .setNegativeButton(R.string.tag_dialog_cancel, null)
        .create();
	}
}