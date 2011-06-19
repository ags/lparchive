package org.ags.lparchive.list;

import java.util.List;

import org.ags.lparchive.LPAdapter;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.model.LetsPlay;
import org.ags.lparchive.task.ProgressTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ArchiveListActivity extends LPListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ArchiveSelectTask(this, "get from db").execute();
	}
		
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
//		LetsPlay lp = ((LPAdapter) l.getAdapter()).getItem(position);
//		if (lp != null) {
//			Intent i = new Intent(this, ChapterListActivity.class);
//			i.putExtra("lp", lp);
//			startActivity(i);
//		}
	}

	class ArchiveSelectTask extends ProgressTask {
		List<LetsPlay> lps;

		public ArchiveSelectTask(Activity activity, String message) {
			super(activity, message);
		}

		protected void onPostExecute(final String result) {
			super.onPostExecute(result);
			ListAdapter adapter = new LPAdapter(activity,
					R.layout.list_item_game, lps);
			ArchiveListActivity.this.setListAdapter(adapter);
		}

		@Override
		protected String doInBackground(Context... params) {
			LPArchiveApplication appState = ((LPArchiveApplication) activity
					.getApplicationContext());
			Log.d("LPA", "begin archive fetch");
			lps = appState.getDataHelper().getArchive();
			return "done";
		}

	}
}
