package org.ags.lparchive.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProgressTask extends AsyncTask<Void, Integer, String> {
	protected ProgressDialog dialog;
	protected Context context;

	public ProgressTask(Context context, String message) {
		super();
		this.context = context;

		// create a progress dialog
		dialog = new ProgressDialog(context);
		dialog.setMessage(message);
		dialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog.show();
	}

	abstract protected String doInBackground(Void... unused);

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		dialog.dismiss();
	}

}
