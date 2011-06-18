package org.ags.lparchive;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LPChapterAdapter extends ArrayAdapter<UpdateLink> {

	private List<UpdateLink> links;
	private int textViewResourceId;

	public LPChapterAdapter(Context context, int textViewResourceId,
			List<UpdateLink> links) {
		super(context, textViewResourceId, links);
		this.links = links;
		this.textViewResourceId = textViewResourceId;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);
		}
		
		UpdateLink link = links.get(position);
		if (link != null) {
			TextView title = (TextView) v.findViewById(R.id.update_title);

			if (title != null) {
				title.setText(link.getTitle());
			}
		}
		
		return v;
	}

}