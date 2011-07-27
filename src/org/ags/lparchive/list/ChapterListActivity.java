package org.ags.lparchive.list;

import java.io.IOException;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.Preferences;
import org.ags.lparchive.R;
import org.ags.lparchive.RetCode;
import org.ags.lparchive.list.adapter.ChapterAdapter;
import org.ags.lparchive.page.ChapterPageActivity;
import org.ags.lparchive.task.ProgressTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Lists all the chapters in a LP. Selecting one starts a ChapterPageActivity
 * with its contents.
 */
public class ChapterListActivity extends ListActivity {
	private static final String TAG = "ChapterListActivity";
	private static final int LONG_LIST = 25;
	private long lpId;
	private String lpUrl;
	private DataHelper dh;

	/**
	 * Creates an Intent to launch a ChapterListActivity for the LP with given
	 * ID.
	 * 
	 * @return The created Intent.
	 */
	public static Intent newInstance(Context context, long lpId) {
		Intent i = new Intent(context, ChapterListActivity.class);
		i.putExtra("lpId", lpId);
		return i;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list);
		dh = ((LPArchiveApplication)getApplicationContext()).getDataHelper();
		
		Bundle extras = getIntent().getExtras();
		lpId = extras.getLong("lpId");
		String url = dh.getLP(lpId).getUrl();
		lpUrl = LPArchiveApplication.baseURL + url;
		
		/* check if the chapter list is stored already. if so retrieve it,
		 otherwise fetch it from the archive site. */
		Cursor chapters = dh.getChapters(lpId);
		if(chapters.getCount() != 0) {
			Log.d(TAG, "chapter already in db");
			populate(chapters);
		} else {
			Log.d(TAG, "chapter not in db");
			new ChapterFetchTask(this).execute();
		}
	}
	
	/**
	 * Fills list with this LPs chapters.
	 */
	private void populate() {
		populate(dh.getChapters(lpId));
	}
	
	/**
	 * Fills chapter list with content from a Cursor.
	 * 
	 * @param chapters
	 *            Cursor to update with. If null, default chapters for this LP
	 *            are used.
	 */
	private void populate(Cursor chapters) {
		Cursor c = (chapters != null) ? chapters : dh.getChapters(lpId);
		// enable fast scrolling if this is a long list
		if(c.getCount() >= LONG_LIST)
			getListView().setFastScrollEnabled(true);
		setListAdapter(new ChapterAdapter(ChapterListActivity.this, c));
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = ChapterPageActivity.newInstance(this, lpUrl, lpId, position);
		startActivity(i);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_chapter_list, menu);
	    return true;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
//	    case R.id.download_lp:
//	    	new DownloadLPTask(this, lpId).execute();
//	        return true;
	    case R.id.preferences:
	    	startActivity(new Intent(this, Preferences.class));
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Downloads a chapter's index/introduction page, parses and stores it.
	 */
	class ChapterFetchTask extends ProgressTask {
		private static final String CONTENT_ELEMENT = "content";
		private static final String LINK_PREFIX = "Update%20";

		public ChapterFetchTask(Context context) {
			super(context, getString(R.string.fetching_wait));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected RetCode doInBackground(Void... unused) {
			try {
				Document doc = Jsoup.connect(lpUrl).get();
				Element e = doc.getElementById(CONTENT_ELEMENT);
				
				String links_to, title;
				
				dh.getDb().beginTransaction();
				// add an introduction chapter
				dh.insertChapter(lpId, LPArchiveApplication.introURL,
						"Introduction");
				for (Element link : e.getElementsByTag("a")) {
					links_to = link.attr("href");
					// make sure all links match "Update%20[N]/"
					title = link.text();
					/*
					 * if no update title is available, last resort is to check
					 * for an image (as in http://lparchive.org/Last-Bible/) and
					 * try to format the filename.
					 */
					if(title.equals("")) {
						Element img = link.getElementsByTag("img").first();
						if(img != null) {
							title = img.attr("src").split("\\.")[0].replace('_', ' ');
						}
					}
					if (links_to.startsWith(LINK_PREFIX)) {
						Log.d(TAG, "title: " + title);
						dh.insertChapter(lpId, links_to, title);
					}
				}
				dh.getDb().setTransactionSuccessful();
				dh.getDb().endTransaction();	
			} catch(IOException e) {
				return RetCode.FETCH_FAILED;
			} catch(SQLException e) {
				return RetCode.DB_ERROR;
			}
			return RetCode.SUCCESS;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(RetCode result) {
			super.onPostExecute(result);
			switch (result) {
			case FETCH_FAILED:
				Toast.makeText(context,
						context.getString(R.string.timeout_error),
						Toast.LENGTH_LONG).show();
				break;
			case DB_ERROR:
				Toast.makeText(context, 
						context.getString(R.string.db_error),
						Toast.LENGTH_LONG).show();
				break;
			case SUCCESS:
				populate();
				break;
			default:
				break;
			}
		}
	}
}