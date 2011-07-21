package org.ags.lparchive.page;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.RetCode;
import org.ags.lparchive.model.Chapter;
import org.ags.lparchive.task.PageFetchTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ChapterPageActivity extends PageActivity {
	private Cursor cursor;
	private String chaptersUrl;
	private long lpId;
	private int position;
	private DataHelper dh;
	
	public static Intent newInstance(Context context, String chaptersUrl, long lpId, int position) {
		Intent i = new Intent(context, ChapterPageActivity.class);
        i.putExtra("chaptersUrl", chaptersUrl);
        i.putExtra("position", position);
        i.putExtra("lpId", lpId);
        return i;
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			chaptersUrl = extras.getString("chaptersUrl");
			position = extras.getInt("position");
			lpId = extras.getLong("lpId");
			
			dh = ((LPArchiveApplication) getApplicationContext())
					.getDataHelper();
			
			cursor = dh.getChapters(lpId);

			loadPage();
		}
	}
	
	protected void loadPage() {
		if (cursor.moveToPosition(position)) {
			long id = cursor.getLong(0);
			Chapter c = dh.getChapter(id);
			boolean isIntro = c.isIntro();
			String pageUrl = (isIntro) ? chaptersUrl : chaptersUrl + c.getUrl();

			new LoadPageFetchTask(this, pageUrl, isIntro).execute();
		} else {
			Log.e("LPA", "error moving to position");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_page_chapter, menu);
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// enable/disable the previous/next buttons depending on chapter
		menu.getItem(0).setEnabled(position != 0);
		menu.getItem(1).setEnabled(cursor.getCount() - 1 != position);

		return super.onPrepareOptionsMenu(menu);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.refresh_page:
	    	loadPage();
	    	return true;
	    case R.id.prev_chapter:
	    	// load previous chapter
	    	position--;
	    	loadPage();
	    	return true;
	    case R.id.next_chapter:
	    	// load next chapter
	    	position++;
	    	loadPage();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	class LoadPageFetchTask extends PageFetchTask {
		public LoadPageFetchTask(Activity activity, String url, boolean isIntro) {
			super(activity, url, isIntro);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(RetCode result) {
			super.onPostExecute(result);
			if (result.equals(RetCode.SUCCESS)) {
				webview.loadDataWithBaseURL(url, html, "text/html", "utf-8",
						null);
			}
		}
	}
}
