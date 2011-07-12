package org.ags.lparchive.list;

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
	private LPAdapter adapter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);

		// enable fast scrolling since the archive list may be long
		getListView().setFastScrollEnabled(true);
		Cursor cursor = null;

		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		Intent intent = getIntent();
		// search archive by game name
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	        		LPSuggestionProvider.AUTHORITY, LPSuggestionProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
			cursor = appState.getDataHelper().lpNameSearch(query);
		// display whole archive
		} else {
			cursor = appState.getDataHelper().getArchive();
		}

		adapter = new LPAdapter(this, cursor);
		setListAdapter(adapter);
	}
}
