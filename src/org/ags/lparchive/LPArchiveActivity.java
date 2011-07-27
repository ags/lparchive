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

/**
 * The entry activity of the application. Displays latest, archive and favorite
 * data views and a donate page through a tab layout.
 */
public class LPArchiveActivity extends TabActivity {
	private int tagMenuSelected = 0; // which tag we're searching by
	private LPArchiveApplication appState;
	
	// tab positions
	public static final int LATEST_TAB = 0;
	public static final int ARCHIVE_TAB = 1;
	public static final int FAVORITES_TAB = 2;
	public static final int DONATE_TAB = 3;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		appState = ((LPArchiveApplication) getApplicationContext());
	}
	
	/**
	 * Sets up the main activity tabs and focuses the first one. 
	 */
	public void createTabs() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		// clear current tabs to avoid duplicates
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
		
		// focus on the first tab
		tabHost.setCurrentTab(LATEST_TAB);

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
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
	    switch (item.getItemId()) {
	    // trigger archive search
	    case R.id.search_archive:
	    	getTabHost().setCurrentTab(ARCHIVE_TAB);
	    	getCurrentActivity().onSearchRequested();
	    	return true;
	    // search by tag
	    case R.id.tag_filter:
	    	getTabHost().setCurrentTab(ARCHIVE_TAB);
	    	createTagDialog().show();
	    	return true;
	    // update the archive list
	    case R.id.refresh_archive:
	    	appState.getDataHelper().clearLatest();
	    	new ArchiveFetchTask(this).execute();
	    	return true;
	    // display preferences
	    case R.id.preferences:
	    	startActivity(new Intent(this, Preferences.class));
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * Creates an AlertDialog that allows filtering of the archive by LP tags.
	 * 
	 * @return The created AlertDialog
	 */
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