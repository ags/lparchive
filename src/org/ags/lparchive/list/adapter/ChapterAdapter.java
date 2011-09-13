package org.ags.lparchive.list.adapter;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ChapterAdapter extends CursorAdapter {
	private static final int readColor = 0x50C0C0C0; // gray
	private int unreadColor;
	
	public ChapterAdapter(Context context, Cursor cursor) {
		super(context, cursor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.list_item_chapter, null);
		
		// get unread color here, since doing it in bindView is unpredictable
		TextView tTitle = (TextView) v.findViewById(R.id.update_title);
		unreadColor = tTitle.getTextColors().getDefaultColor();

		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_CHAPTER_TITLE));
		int read = cursor.getInt(cursor.getColumnIndex(
				DataHelper.KEY_CHAPTER_READ));
		
		TextView tTitle = (TextView) view.findViewById(R.id.update_title);
		tTitle.setText(title);
		// grey out read chapters according to preference
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(prefs.getBoolean("markReadPref", true))
			tTitle.setTextColor((read == 1) ? readColor : unreadColor);	
	}

}