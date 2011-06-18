package org.ags.lparchive.fetch;

import java.util.ArrayList;
import java.util.List;

import org.ags.lparchive.LPAdapter;
import org.ags.lparchive.LetsPlay;
import org.ags.lparchive.R;
import org.ags.lparchive.list.LPListActivity;

import android.app.ListActivity;
import android.content.Context;
import android.widget.ListAdapter;

public abstract class LPListFetchTask extends LPFetchTask {
	protected List<LetsPlay> lps;

	public LPListFetchTask(LPListActivity activity, String url,
			int textViewResourceId) {
		super(activity, activity.getString(R.string.fetching_wait), url,
				textViewResourceId);
		lps = new ArrayList<LetsPlay>();
	}

	@Override
	abstract protected String doInBackground(Context... params);

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ListAdapter adapter = new LPAdapter(activity, textViewResourceId, lps);
		((ListActivity) activity).setListAdapter(adapter);
	}
}
