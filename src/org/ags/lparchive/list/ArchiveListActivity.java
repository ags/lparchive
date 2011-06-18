package org.ags.lparchive.list;

import java.net.URL;
import java.util.ArrayList;
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
import android.view.View;
import android.widget.ListView;

public class ArchiveListActivity extends LPListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LPListFetchTask initTask = new ArchiveFetchTask(this,
				getString(R.string.base_url), R.layout.list_item_game);
		initTask.execute(this);
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		LetsPlay lp = ((LPAdapter)l.getAdapter()).getItem(position);
		if(lp != null) {
			Intent i = new Intent(ArchiveListActivity.this, ChapterListActivity.class);
			i.putExtra("lp", lp);
			startActivity(i);
		}
	}


	class ArchiveFetchTask extends LPListFetchTask {

		public ArchiveFetchTask(LPListActivity activity, String url, int textViewResourceId) {
			super(activity, url, textViewResourceId);
		}

		@Override
		protected String doInBackground(Context... params) {
			try {
				Source source = new Source(new URL(url));
				List<Element> lis = source.getAllElements(HTMLElementName.TR);
				for (Element li : lis) {
					String game = "", author = "", url = "";
					List<String> tags = new ArrayList<String>();
					Element strong = li.getFirstElement(HTMLElementName.STRONG);
					if (strong != null)
						game = strong.getContent().toString();

					Element span = li.getFirstElement(HTMLElementName.SPAN);
					if (span != null) {
						author = span.getContent().toString();
						// remove "by " from author
						author = author.substring(3, author.length());
					}

					List<Element> links = li.getAllElements(HTMLElementName.A);
					for (Element link : links) {
						String tag = link.getAttributeValue("tag");
						if (tag != null) {
							tags.add(tag);
						} else {
							url = link.getAttributeValue("href");
						}
					}
					
					if(!url.equals("")) {
						LetsPlay lp = new LetsPlay(game, author, url, tags);
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
