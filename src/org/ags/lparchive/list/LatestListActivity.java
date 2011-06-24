package org.ags.lparchive.list;

import org.ags.lparchive.LPAdapter;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class LatestListActivity extends LPListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		Log.w("LPA", "creating LLA");
		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		Cursor cursor = appState.getDataHelper().getRecentLetsPlay();
		setListAdapter(new LPAdapter(this, cursor));
	}
	
}
