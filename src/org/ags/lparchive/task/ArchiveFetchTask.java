package org.ags.lparchive.task;

import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.ags.lparchive.LPArchiveActivity;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.list.ArchiveListActivity;
import org.ags.lparchive.list.LatestListActivity;
import org.ags.lparchive.model.LetsPlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TabHost;

public class ArchiveFetchTask extends ProgressTask {
	private static final String LATEST_DIV = "latest";

	public ArchiveFetchTask(Activity activity) {
		super(activity, activity.getString(R.string.fetching_wait));
	}
		
	@Override
	protected String doInBackground(Context... params) {
		try {
			Source source = new Source(new URL(activity
					.getString(R.string.base_url)));
			List<Element> lis = source.getAllElements(HTMLElementName.TR);
			LPArchiveApplication appState = ((LPArchiveApplication) activity
					.getApplicationContext());
			LetsPlay lp;
			for (Element li : lis) {
				lp = new LetsPlay(li);

				if (!lp.getUrl().equals("")) {
//					Log.d("LPA", lp.toString());
					appState.getDataHelper().insertLetsPlay(lp);
				}
			}
			Element latest = source.getElementById(LATEST_DIV);
			if (latest != null) {
				lis = latest.getAllElements(HTMLElementName.LI);
				for (Element li : lis) {
					lp = new LetsPlay(li);
					if (!lp.getUrl().equals("")) {
						Log.d("LPA", "marking " + lp.toString() + " recent");
						appState.getDataHelper().markRecentLetsPlay(lp);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "done";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.d("LPA", "setting current tab");
		TabHost tabHost = ((LPArchiveActivity) activity).getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(activity, LatestListActivity.class);
		spec = tabHost.newTabSpec("latest").setIndicator(
				activity.getString(R.string.latest_tab)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(activity, ArchiveListActivity.class);
		spec = tabHost.newTabSpec("archive").setIndicator(
				activity.getString(R.string.archive_tab)).setContent(intent);
		tabHost.addTab(spec);
		tabHost.setCurrentTab(0);
	}

}
