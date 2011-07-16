package org.ags.lparchive.list;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPSuggestionProvider;
import org.ags.lparchive.R;
import org.ags.lparchive.list.adapter.LPAdapter;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;

public class ArchiveListActivity extends LPListActivity {
	private DataHelper dh;
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		// enable fast scrolling as the archive list may be long
		getListView().setFastScrollEnabled(true);
		
		Cursor cursor = null;
		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		dh = appState.getDataHelper();
		Intent intent = getIntent();
		// search archive by game name
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	        		LPSuggestionProvider.AUTHORITY, LPSuggestionProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
			cursor = dh.lpNameSearch(query);
		// display whole archive
		} else {
			cursor = dh.getArchive();
		}

		setListAdapter(new LPAdapter(this, cursor));
	}

//	@Override
//	public void doNegativeClick() {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void doPositiveClick(int checked) {
		Cursor cursor = null;
		if(checked == 0) {
			cursor = dh.getArchive();
		} else {
			String[] tags = getResources().getStringArray(R.array.lp_tags);
			cursor = dh.tagSearch(tags[checked].toLowerCase());
		}
		setListAdapter(new LPAdapter(this, cursor));
	}
}
