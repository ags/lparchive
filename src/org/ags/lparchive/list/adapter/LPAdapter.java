package org.ags.lparchive.list.adapter;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.model.LetsPlay;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
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
	
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
        	Log.d("LPA", "doing it here");
        	return getFilterQueryProvider().runQuery(constraint); 
        }

        StringBuilder buffer = null;
        String[] args = null;
        DataHelper dh = ((LPArchiveApplication)context.getApplicationContext()).getDataHelper();
        if (constraint != null) {
            buffer = new StringBuilder();
            buffer.append("LOWER(");
            buffer.append(DataHelper.KEY_GAME);
            buffer.append(") GLOB ?");
            args = new String[] { constraint.toString().toLowerCase() + "*" };
        }
        
        return dh.getDb().query(DataHelper.ARCHIVE_TABLE, DataHelper.projectArchive,
        		buffer == null ? null : buffer.toString(), args, null, null, 
        				DataHelper.SORT_GAME_ASC);
    }
}