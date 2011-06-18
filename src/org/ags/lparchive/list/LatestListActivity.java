package org.ags.lparchive.list;

import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.ags.lparchive.LPAdapter;
import org.ags.lparchive.LetsPlay;
import org.ags.lparchive.R;
import org.ags.lparchive.fetch.LPListFetchTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class LatestListActivity extends LPListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LPListFetchTask initTask = new LatestFetchTask(this,
				getString(R.string.base_url), R.layout.list_item_game);
		initTask.execute(this);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		LetsPlay lp = ((LPAdapter)l.getAdapter()).getItem(position);
		if(lp != null) {
			Intent i = new Intent(LatestListActivity.this, ChapterListActivity.class);
			i.putExtra("lp", lp);
			startActivity(i);
		}
	}
	
	class LatestFetchTask extends LPListFetchTask {
		// put in strings.xml?
		private static final String LATEST_DIV = "latest";
		
		public LatestFetchTask(LPListActivity activity, String url, int textViewResourceId) {
			super(activity, url, textViewResourceId);
		}
		
		@Override
		protected String doInBackground(Context... params) {
			try {
				Log.d("lpa", url);
				Source source = new Source(new URL(url));
				Element latest = source.getElementById(LATEST_DIV);
				if (latest != null) {
					List<Element> lis = latest.getAllElements(HTMLElementName.LI);
					String game = "", author = "", url = "";
					for (Element li : lis) {
						Element strong = li.getFirstElement(HTMLElementName.STRONG);
						if (strong != null)
							game = strong.getContent().toString();

						Element span = li.getFirstElement(HTMLElementName.SPAN);
						if (span != null) {
							author = span.getContent().toString();
							// remove "by " from author
							author = author.substring(3, author.length());
						}

						Element link = li.getFirstElement(HTMLElementName.A);
						if (link != null) {
							url = link.getAttributeValue("href");
						}

						LetsPlay lp = new LetsPlay(game, author, url);
						lps.add(lp);
					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "done";
		}

	}
}
