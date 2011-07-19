package org.ags.lparchive.list;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPSuggestionProvider;
import org.ags.lparchive.R;
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
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;

public class LPListActivity extends ListActivity  {
	private static final String TAG = "LPListActivity";
	private DataHelper dh;
	private static final int MENU_ITEM_VIEW = 0;
	private static final int MENU_ITEM_TOGGLE_FAV = 1;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lp_list);
		// enable fast scrolling for the long archive list
		getListView().setFastScrollEnabled(true);
		
		Cursor cursor = null;
		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		dh = appState.getDataHelper();
		final Intent intent = getIntent();
		final String action = intent.getAction();
		
		// search archive by game name
		if (Intent.ACTION_SEARCH.equals(action)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
	        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	        		LPSuggestionProvider.AUTHORITY, LPSuggestionProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
			cursor = dh.lpNameSearch(query);
		// display whole archive
		} else if (LPArchiveApplication.LATEST_LIST_ACTION.equals(action)) {
			cursor = dh.getLatestLPs();
		} else if (LPArchiveApplication.FAVORITE_LIST_ACTION.equals(action)) {
			Log.d("LPA", "intenting fav");
			cursor = dh.getFavoriteLPs();
		} else {
			cursor = dh.getArchive();
		}
		
		setListAdapter(new LPAdapter(this, cursor));
//		getListView().setFocusable(true);
//		getListView().setOnCreateContextMenuListener(this);
		registerForContextMenu(getListView());
	}
		
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		startActivity(getListClickIntent(id));
	}
	
	protected Intent getListClickIntent(long id) {
		DataHelper dh = ((LPArchiveApplication)getApplicationContext()).getDataHelper();
		LetsPlay lp = dh.getLP(id);
		String type = lp.getType();
		// video formatting is inconsistent, so load as page
		// TODO hybrid category
		if(type.equals("video")) {
			String pageUrl = LPArchiveApplication.baseURL + lp.getUrl();
			return SimplePageActivity.newInstance(this, pageUrl);
		} else {
			return ChapterListActivity.newInstance(this, id);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.d(TAG, "context create");
		 AdapterView.AdapterContextMenuInfo info;
	        try {
	             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	        } catch (ClassCastException e) {
	            Log.e(TAG, "bad menuInfo", e);
	            return;
	        }
	            
		Cursor cursor  = (Cursor)getListAdapter().getItem(info.position);
		// TODO MAGIC NUMBERS BAD
		String game = cursor.getString(1);
		menu.setHeaderTitle(game);
		menu.add(0, MENU_ITEM_VIEW, 0, R.string.menu_viewLP).setIntent(
				getListClickIntent(info.id));
	     // Star toggling
		DataHelper dh = ((LPArchiveApplication)getApplicationContext()).getDataHelper();
		
        if (dh.isFavoriteLP(info.id)) {
            menu.add(0, MENU_ITEM_TOGGLE_FAV, 0, R.string.menu_favLP);
        } else {
            menu.add(0, MENU_ITEM_TOGGLE_FAV, 0, R.string.menu_unfavLP);
        }
	}

	public void tagFilter(int tagMenuSelected) {
		String[] tags = getResources().getStringArray(R.array.lp_tags);
		if(tagMenuSelected >= tags.length) {
			Log.e(TAG, "unrecognized tag");
			return;
		}
		Cursor cursor = dh.tagSearch(tags[tagMenuSelected].toLowerCase());
		setListAdapter(new LPAdapter(this, cursor));
	}
	
}
