package org.ags.lparchive;

import org.ags.lparchive.list.ArchiveListActivity;
import org.ags.lparchive.list.LPListActivity;
import org.ags.lparchive.list.LatestListActivity;
import org.ags.lparchive.task.ArchiveFetchTask;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class LPArchiveActivity extends TabActivity {
	private int tagMenuSelected = 0;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
	}
	
	/* constructs the application tab layout */
	public void createTabs() {
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, LatestListActivity.class);
		spec = tabHost.newTabSpec("latest").setIndicator(
				getString(R.string.latest_tab),
				getResources().getDrawable(R.drawable.ic_tab_latest))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ArchiveListActivity.class);
		spec = tabHost.newTabSpec("archive").setIndicator(
				getString(R.string.archive_tab),
				getResources().getDrawable(R.drawable.ic_tab_archive))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent(this, DonatePageActivity.class);
		spec = tabHost.newTabSpec("donate").setIndicator(
				getString(R.string.donate_tab),
				getResources().getDrawable(R.drawable.ic_tab_donate))
				.setContent(intent);
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
			Log.d("LPA", "already fetched!!");
			createTabs();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_main, menu);
	    return true;
	}
	
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
	    case R.id.preferences:
	    	startActivity(new Intent(this, Preferences.class));
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private AlertDialog createTagDialog() {
		return new AlertDialog.Builder(getCurrentActivity())
        .setTitle(getString(R.string.tag_dialog_title))
        .setSingleChoiceItems(R.array.lp_tags, tagMenuSelected, null)
        .setPositiveButton(R.string.tag_dialog_confirm,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
								tagMenuSelected = ((AlertDialog) dialog)
										.getListView().getCheckedItemPosition();
                    ((LPListActivity)getCurrentActivity()).doPositiveClick(tagMenuSelected);
                }
            })
        .setNegativeButton(R.string.tag_dialog_cancel, null)
        .create();
	}
}