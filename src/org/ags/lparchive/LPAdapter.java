package org.ags.lparchive;

import org.ags.lparchive.model.LetsPlay;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class LPAdapter extends CursorAdapter implements Filterable {
	private Context context;

	public LPAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		this.context = context;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.list_item_game, null);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String game = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_GAME));
		TextView tGame = (TextView) view.findViewById(R.id.game);
		tGame.setText(game);

		String author = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_AUTHOR));
		TextView tAuthor = (TextView) view.findViewById(R.id.author);
		tAuthor.setText(author);

		String type = cursor.getString(cursor.getColumnIndex(
				DataHelper.KEY_TYPE));
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		icon.setImageResource(LetsPlay.getIconResource(type));
	}

}