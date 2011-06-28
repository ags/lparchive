package org.ags.lparchive.list;

import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.list.adapter.LPAdapter;

import android.database.Cursor;
import android.os.Bundle;

public class LatestListActivity extends LPListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		Cursor cursor = appState.getDataHelper().getRecentLetsPlay();
		setListAdapter(new LPAdapter(this, cursor));
	}
	
}
