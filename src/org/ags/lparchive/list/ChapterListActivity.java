package org.ags.lparchive.list;

import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.ags.lparchive.LPChapterAdapter;
import org.ags.lparchive.LPPageActivity;
import org.ags.lparchive.R;
import org.ags.lparchive.model.LetsPlay;
import org.ags.lparchive.model.UpdateLink;
import org.ags.lparchive.task.ProgressTask;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ChapterListActivity extends LPListActivity {
	private LetsPlay lp;
	private String chapters_url;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		this.lp = (LetsPlay) extras.getSerializable("lp");
		chapters_url = getString(R.string.base_url) + lp.getUrl();
		ProgressTask initTask = new ChapterFetchTask(this);
		initTask.execute(this);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		UpdateLink update = ((LPChapterAdapter)l.getAdapter()).getItem(position);
		if(update != null) {
			Intent i = new Intent(ChapterListActivity.this, LPPageActivity.class);
			i.putExtra("update_url", chapters_url + update.getHref());
			startActivity(i);
		}
	}
	
	class ChapterFetchTask extends ProgressTask {
		private static final String CONTENT_ELEMENT = "content";
		private static final String LINK_PREFIX = "Update%20";

		public ChapterFetchTask(LPListActivity activity) {
			super(activity, activity.getString(R.string.fetching_wait));

		}

		@Override
		protected String doInBackground(Context... params) {
			try {
//				Log.d("lpa", super.url);
				Source source = new Source(new URL(chapters_url));
				Element e = source.getElementById(CONTENT_ELEMENT);
				// get all links. after the ones matching "Update%20[N]/"
				List<Element> links = e.getAllElements(HTMLElementName.A);
				for (Element link : links) {
					String links_to = link.getAttributeValue("href");
					if (links_to.startsWith(LINK_PREFIX)) {
						String title = link.getContent().toString();
						ChapterListActivity.this.lp
								.addUpdateUrl(new UpdateLink(links_to, title));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
//			LPChapterAdapter adapter = new LPChapterAdapter(activity,
//					textViewResourceId, lp.getUpdateUrls());
//			((ListActivity) activity).setListAdapter(adapter);
		}
	}
}