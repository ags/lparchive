package org.ags.lparchive.list;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPChapterAdapter;
import org.ags.lparchive.LPPageActivity;
import org.ags.lparchive.R;
import org.ags.lparchive.task.ProgressTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ChapterListActivity extends ListActivity {
	private long lp_id;
	private String chapters_url;
	private DataHelper dh;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		this.dh = ((LPArchiveApplication)getApplicationContext()).getDataHelper();
		Bundle extras = getIntent().getExtras();
		
		this.lp_id = extras.getLong("lp_id");
		String url = dh.getLP(lp_id).getUrl();
		chapters_url = getString(R.string.base_url) + url;
		Log.d("LPA", "GOT " + dh.getChapters(lp_id).getCount());
		boolean inDb = dh.getChapters(lp_id).getCount() != 0;
		if(!inDb) {
			Log.d("LPA", "NOT IN DB");
			ProgressTask initTask = new ChapterFetchTask(this);
			initTask.execute(this);
		} else {
			Log.d("LPA", "ALREADY IN DB");
			populate();
		}
	}
	
	void populate() {
		Cursor cursor = dh.getChapters(lp_id);
		setListAdapter(new LPChapterAdapter(ChapterListActivity.this, cursor));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, LPPageActivity.class);
		String url = dh.getChapter(id).getUrl();
		i.putExtra("chapter_url", chapters_url + url);
		startActivity(i);
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
				Document doc = Jsoup.connect(chapters_url).get();
				Element e = doc.getElementById(CONTENT_ELEMENT);
				// get all links matching "Update%20[N]/"
				String links_to;
				for (Element link : e.getElementsByTag("a")) {
					links_to = link.attr("href");
					if (links_to.startsWith(LINK_PREFIX)) {
						dh.insertChapter(lp_id, links_to, link.text());
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