package org.ags.lparchive.list.adapter;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.R;
import org.ags.lparchive.model.LetsPlay;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LPAdapter extends CursorAdapter {

	public LPAdapter(Context context, Cursor c) {
		super(context, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.list_item_game, null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String game = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_ARCHIVE_GAME));
		TextView tGame = (TextView) view.findViewById(R.id.game);
		tGame.setText(game);

		String author = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_ARCHIVE_AUTHOR));
		TextView tAuthor = (TextView) view.findViewById(R.id.author);
		tAuthor.setText(author);

		String type = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_ARCHIVE_TYPE));
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		icon.setImageResource(LetsPlay.getIconResource(type));
	}
	
}