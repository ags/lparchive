package org.ags.lparchive.list;

import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPChapterAdapter;
import org.ags.lparchive.R;
import org.ags.lparchive.task.ProgressTask;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ChapterListActivity extends ListActivity {
//	private LetsPlay lp;
	private long lp_id;
	private String chapters_url;
	private LPArchiveApplication appState;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		this.appState = ((LPArchiveApplication)getApplicationContext());
		
		Bundle extras = getIntent().getExtras();
		this.lp_id = extras.getLong("lp_id");
		Log.d("LPA", "get chapters for " + lp_id);
		String url = appState.getDataHelper().getLP(lp_id).getUrl();
		chapters_url = getString(R.string.base_url) + url;
		boolean inDb = false;
		if(!inDb) {
			ProgressTask initTask = new ChapterFetchTask(this);
			initTask.execute(this);
		} else {
			populate();
		}
	}
	
	void populate() {
		Cursor cursor = appState.getDataHelper().getChapters(lp_id);
		setListAdapter(new LPChapterAdapter(ChapterListActivity.this, cursor));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
//		UpdateLink update = ((LPChapterAdapter)l.getAdapter()).getItem(position);
//		if(update != null) {
//			Intent i = new Intent(ChapterListActivity.this, LPPageActivity.class);
//			i.putExtra("update_url", chapters_url + update.getHref());
//			startActivity(i);
//		}
	}
	
	class ChapterFetchTask extends ProgressTask {
		private static final String CONTENT_ELEMENT = "content";
		private static final String LINK_PREFIX = "Update%20";

		public ChapterFetchTask(ListActivity activity) {
			super(activity, activity.getString(R.string.fetching_wait));
		}

		@Override
		protected String doInBackground(Context... params) {
			try {
				Log.d("LPA", "curl: " + chapters_url);
				Source source = new Source(new URL(chapters_url));
				Element e = source.getElementById(CONTENT_ELEMENT);
				// get all links. after the ones matching "Update%20[N]/"
				List<Element> links = e.getAllElements(HTMLElementName.A);
				for (Element link : links) {
					String links_to = link.getAttributeValue("href");
					if (links_to.startsWith(LINK_PREFIX)) {
						String title = link.getContent().toString();
						appState.getDataHelper().insertChapter(lp_id, 
								chapters_url + links_to, title);
//						ChapterListActivity.this.lp.addUpdateUrl(new UpdateLink(links_to, title));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "done";
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			populate();
		}
	}
}