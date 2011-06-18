package org.ags.lparchive.fetch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class LPFetchTask extends AsyncTask<Context, Integer, String> {
	private ProgressDialog dialog;
	protected String url;
	protected Activity activity;
	protected int textViewResourceId;

	public LPFetchTask(Activity activity, String fetch_message, String url,
			int textViewResourceId) {
		super();
		this.url = url;
		this.activity = activity;
		this.textViewResourceId = textViewResourceId;
		// create a progress dialog
		dialog = new ProgressDialog(activity);
		dialog.setMessage(fetch_message);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog.show();
	}

	abstract protected String doInBackground(Context... params);

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		dialog.dismiss();
	}

}
