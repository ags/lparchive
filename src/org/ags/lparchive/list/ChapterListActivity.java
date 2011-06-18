package org.ags.lparchive.list;

import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.ags.lparchive.LPChapterAdapter;
import org.ags.lparchive.LetsPlay;
import org.ags.lparchive.R;
import org.ags.lparchive.UpdateLink;
import org.ags.lparchive.fetch.LPFetchTask;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;

public class ChapterListActivity extends LPListActivity {
	private LetsPlay lp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		this.lp = (LetsPlay) extras.getSerializable("lp");
		String url = getString(R.string.base_url) + lp.getUrl();
		LPFetchTask initTask = new ChapterFetchTask(this, url,
				R.layout.list_item_update);
		initTask.execute(this);
	}

	class ChapterFetchTask extends LPFetchTask {
		private static final String CONTENT_ELEMENT = "content";
		private static final String LINK_PREFIX = "Update%20";

		public ChapterFetchTask(LPListActivity activity, String url,
				int textViewResourceId) {
			super(activity, activity.getString(R.string.fetching_wait), url,
					textViewResourceId);

		}

		@Override
		protected String doInBackground(Context... params) {
			try {
				Source source = new Source(new URL(super.url));
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
			LPChapterAdapter adapter = new LPChapterAdapter(activity,
					textViewResourceId, lp.getUpdateUrls());
			((ListActivity) activity).setListAdapter(adapter);
		}
	}
}