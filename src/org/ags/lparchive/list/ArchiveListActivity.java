package org.ags.lparchive.list;

import org.ags.lparchive.LPAdapter;
import org.ags.lparchive.LPArchiveApplication;

import android.database.Cursor;
import android.os.Bundle;

public class ArchiveListActivity extends LPListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		Cursor cursor = appState.getDataHelper().getArchive();
		setListAdapter(new LPAdapter(this, cursor));
	}
}
