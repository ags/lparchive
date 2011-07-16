package org.ags.lparchive.list.adapter;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ChapterAdapter extends CursorAdapter {

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
		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_CHAPTER_TITLE));
		TextView tTitle = (TextView) view.findViewById(R.id.update_title);
		tTitle.setText(title);
	}

}