package org.ags.lparchive;

import java.util.List;

import org.ags.lparchive.model.LetsPlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LPAdapter extends ArrayAdapter<LetsPlay> {

	private List<LetsPlay> lps;
	private int textViewResourceId;

	public LPAdapter(Context context, int textViewResourceId,
			List<LetsPlay> lps) {
		super(context, textViewResourceId, lps);
		this.lps = lps;
		this.textViewResourceId = textViewResourceId;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);
		}

		LetsPlay lp = lps.get(position);
//		Log.d("lpa", lp.toString());
		if (lp != null) {
			TextView game = (TextView) v.findViewById(R.id.game);
			TextView author = (TextView) v.findViewById(R.id.author);
			ImageView icon = (ImageView) v.findViewById(R.id.icon);
			
			if(icon != null) {
				icon.setImageResource(lp.getIconResource());
			}
			if (game != null) {
				game.setText(lp.getGame());
			}
			if (author != null) {
				author.setText("by " + lp.getAuthor());
			}
		}
		return v;
	}

}