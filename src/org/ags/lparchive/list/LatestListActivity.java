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

public class LatestListActivity extends LPListActivity {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		
		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		Cursor cursor = null;
		
		Intent intent = getIntent();
		// search archive by game name
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	        		LPSuggestionProvider.AUTHORITY, LPSuggestionProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
			cursor = appState.getDataHelper().lpLatestNameSearch(query);
		// display whole archive
		} else {
			cursor = appState.getDataHelper().getRecentLetsPlay();
		}
		
		setListAdapter(new LPAdapter(this, cursor));
	}
	
}
