package org.ags.lparchive.list;

import org.ags.lparchive.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class LPListActivity extends ListActivity {
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.d("LPA", "selected " + id);
		Intent i = new Intent(this, ChapterListActivity.class);
		i.putExtra("lp_id", id);
		startActivity(i);
	}
}
