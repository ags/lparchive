package org.ags.lparchive.list;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPPageActivity;
import org.ags.lparchive.R;
import org.ags.lparchive.model.LetsPlay;

import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

public class LPListActivity extends ListActivity {
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		DataHelper dh = ((LPArchiveApplication)getApplicationContext()).getDataHelper();
		LetsPlay lp = dh.getLP(id);
		String type = lp.getType();
		Intent i = null;
		// video formatting is inconsistent, so load as page
		if(type.equals("video")) {
			i = new Intent(this, LPPageActivity.class);
			i.putExtra("page_url", getString(R.string.base_url) + lp.getUrl());
		} else {
			i = new Intent(this, ChapterListActivity.class);
			i.putExtra("lp_id", id);
		}
		startActivity(i);
	}
}
