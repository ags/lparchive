package org.ags.lparchive.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProgressTask extends AsyncTask<Context, Integer, String> {
	private ProgressDialog dialog;
	protected Activity activity;

	public ProgressTask(Activity activity, String message) {
		super();
		this.activity = activity;
		// create a progress dialog
		dialog = new ProgressDialog(activity);
		dialog.setMessage(message);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
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
