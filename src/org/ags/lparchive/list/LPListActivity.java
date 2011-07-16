package org.ags.lparchive.list;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.model.LetsPlay;
import org.ags.lparchive.page.SimplePageActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

public abstract class LPListActivity extends ListActivity {
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		DataHelper dh = ((LPArchiveApplication)getApplicationContext()).getDataHelper();
		LetsPlay lp = dh.getLP(id);
		String type = lp.getType();
		Intent i = null;
		// video formatting is inconsistent, so load as page
		// TODO hybrid category
		if(type.equals("video")) {
			String pageUrl = LPArchiveApplication.baseURL + lp.getUrl();
			i = SimplePageActivity.newInstance(this, pageUrl);
		} else {
			i = ChapterListActivity.newInstance(this, id);
		}
		startActivity(i);
	}

}
