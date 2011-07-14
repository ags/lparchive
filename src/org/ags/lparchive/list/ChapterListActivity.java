package org.ags.lparchive.list;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.ChapterPageActivity;
import org.ags.lparchive.Preferences;
import org.ags.lparchive.R;
import org.ags.lparchive.list.adapter.ChapterAdapter;
import org.ags.lparchive.task.DownloadLPTask;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ChapterListActivity extends ListActivity {
	private long lpId;
	private String chaptersUrl;
	private DataHelper dh;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		dh = ((LPArchiveApplication)getApplicationContext()).getDataHelper();
		Bundle extras = getIntent().getExtras();
		
		lpId = extras.getLong("lp_id");
		String url = dh.getLP(lpId).getUrl();
		chaptersUrl = LPArchiveApplication.baseURL + url;
		boolean inDb = dh.getChapters(lpId).getCount() != 0;
		if(!inDb) {
			Log.d("LPA", "NOT IN DB");
			new ChapterFetchTask(this).execute();
		} else {
			Log.d("LPA", "ALREADY IN DB");
			populate();
		}
	}
	
	/* retrieves a cursor for chapters and sets list adapter to use it */
	private void populate() {
		Cursor chapterCursor = dh.getChapters(lpId);
		setListAdapter(new ChapterAdapter(ChapterListActivity.this,
				chapterCursor));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ChapterPageActivity.class);
	
		i.putExtra("chapters_url", chaptersUrl);
		i.putExtra("position", position);
		i.putExtra("lp_id", lpId);
		
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_chapter_list, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.download_lp:
	    	new DownloadLPTask(this, lpId).execute();
	        return true;
	    case R.id.preferences:
	    	startActivity(new Intent(this, Preferences.class));
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	class ChapterFetchTask extends ProgressTask {
		private static final String CONTENT_ELEMENT = "content";
		private static final String LINK_PREFIX = "Update%20";

		public ChapterFetchTask(Context context) {
			super(context, getString(R.string.fetching_wait));
		}

		@Override
		protected String doInBackground(Void... unused) {
			try {
				Document doc = Jsoup.connect(chaptersUrl).get();
				Element e = doc.getElementById(CONTENT_ELEMENT);
				// get all links matching "Update%20[N]/"
				String links_to;
				dh.getDb().beginTransaction();
				dh.insertChapter(lpId, LPArchiveApplication.introURL, "Introduction");
				for (Element link : e.getElementsByTag("a")) {
					links_to = link.attr("href");
					if (links_to.startsWith(LINK_PREFIX)) {
						dh.insertChapter(lpId, links_to, link.text());
					}
				}
				dh.getDb().setTransactionSuccessful();
				dh.getDb().endTransaction();
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