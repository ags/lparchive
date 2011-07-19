package org.ags.lparchive.list;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPSuggestionProvider;
import org.ags.lparchive.R;
import org.ags.lparchive.LPArchiveApplication.LPTypes;
import org.ags.lparchive.list.adapter.LPAdapter;
import org.ags.lparchive.model.LetsPlay;
import org.ags.lparchive.page.SimplePageActivity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;

public class LPListActivity extends ListActivity  {
	private static final String TAG = "LPListActivity";
	private static final int MENU_ITEM_VIEW = 0;
	private static final int MENU_ITEM_TOGGLE_FAV = 1;
	
	// TODO comment explaining this 
	private static boolean isDirty;
	
	private DataHelper dh;
	private String action;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lp_list);
		isDirty = false;
		Cursor cursor = null;
		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		dh = appState.getDataHelper();
		Intent intent = getIntent();
		action = intent.getAction();
		
		// search archive by game name
		if (Intent.ACTION_SEARCH.equals(action)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	        		LPSuggestionProvider.AUTHORITY, LPSuggestionProvider.MODE);
	        // remember the users queries
	        suggestions.saveRecentQuery(query, null);
			cursor = dh.lpNameSearch(query);
		// show latest LPs
		} else if (LPArchiveApplication.LATEST_LIST_ACTION.equals(action)) {
			cursor = dh.getLatestLPs();
		// show favourite LPs
		} else if (LPArchiveApplication.FAVORITE_LIST_ACTION.equals(action)) {
			cursor = dh.getFavoriteLPs();
		// show all LPs
		} else {
			cursor = dh.getArchive();
			// enable fast scrolling - this is a long list
			getListView().setFastScrollEnabled(true);
		}
		
		// allow context menu (long click) on LPs
		registerForContextMenu(getListView());
		
		// populate the listview
		setListAdapter(new LPAdapter(this, cursor));
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "RESUME " + action);
		if(isDirty && LPArchiveApplication.FAVORITE_LIST_ACTION.equals(action)) {
			setListAdapter(new LPAdapter(this, dh.getFavoriteLPs()));
			isDirty = false;
		}
		super.onResume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		startActivity(getLPViewIntent(id));
	}

	/**
	 * Returns an Intent to view a LP based on its type.
	 * 
	 * @param id
	 *            The ID of the LP to view.
	 * @return The constructed intent.
	 */
	private Intent getLPViewIntent(long id) {
		LetsPlay lp = dh.getLP(id);
		LPTypes type = lp.getType();
		// video formatting is inconsistent, so load as page
		// TODO hybrid category
		if(type.equals(LPTypes.VIDEO)) {
			String pageUrl = LPArchiveApplication.baseURL + lp.getUrl();
			return SimplePageActivity.newInstance(this, pageUrl);
		} else {
			return ChapterListActivity.newInstance(this, id);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		String game = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_ARCHIVE_GAME));

		// construct menu header / items
		menu.setHeaderTitle(game);
		menu.add(0, MENU_ITEM_VIEW, 0, R.string.menu_viewLP).setIntent(
				getLPViewIntent(info.id));

		// Star toggling
		DataHelper dh = ((LPArchiveApplication) getApplicationContext())
				.getDataHelper();

		if (dh.isFavoriteLP(info.id)) {
			menu.add(0, MENU_ITEM_TOGGLE_FAV, 0, R.string.menu_unfavLP);
		} else {
			menu.add(0, MENU_ITEM_TOGGLE_FAV, 0, R.string.menu_favLP);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		long id = cursor.getLong(cursor.getColumnIndex(DataHelper.KEY_ID));
		Log.d(TAG, "id: " + id);
		switch(item.getItemId()) {
		case MENU_ITEM_TOGGLE_FAV:
			dh.toggleFavoriteLP(id);
			// need to refresh the list adapter
			if(LPArchiveApplication.FAVORITE_LIST_ACTION.equals(action))
				setListAdapter(new LPAdapter(this, dh.getFavoriteLPs()));
			else
				isDirty = true;
			return true;
		}
		
		return super.onContextItemSelected(item);
	}

	/**
	 * Filters the ListView to LPs from the archive matching a given tag.
	 * 
	 * @param tagIndex
	 *            The index into arrays.lp_tags for a tag.
	 */
	public void tagFilter(int tagIndex) {
		Cursor cursor = null;
		if (tagIndex == 0) {
			cursor = dh.getArchive();
		} else {
			String[] tags = getResources().getStringArray(R.array.lp_tags);
			if (tagIndex >= tags.length) {
				Log.e(TAG, "unrecognized tag");
				return;
			} else {
				cursor = dh.tagSearch(tags[tagIndex].toLowerCase());
			}
		}
		setListAdapter(new LPAdapter(this, cursor));
	}
	
}
