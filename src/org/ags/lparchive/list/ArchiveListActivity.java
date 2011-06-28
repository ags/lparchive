package org.ags.lparchive.list;

import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.list.adapter.LPAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class ArchiveListActivity extends LPListActivity {
	private EditText filterText = null;
	private LPAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lp_list_filtered);
		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);

		LPArchiveApplication appState = ((LPArchiveApplication) getApplicationContext());
		Cursor cursor = appState.getDataHelper().getArchive();
		this.adapter = new LPAdapter(this, cursor);
		setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		filterText.removeTextChangedListener(filterTextWatcher);
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter.getFilter().filter(s);
			adapter.notifyDataSetChanged();
		}

	};
}
