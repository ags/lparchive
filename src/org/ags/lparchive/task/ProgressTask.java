package org.ags.lparchive.task;

import org.ags.lparchive.RetCode;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProgressTask extends AsyncTask<Void, Integer, RetCode> {
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

	abstract protected RetCode doInBackground(Void... unused);

	@Override
	protected void onPostExecute(RetCode result) {
		super.onPostExecute(result);
		// some devices can crash here occasionally, this should handle that
		try {
			dialog.dismiss();
		} catch (Exception e) {
			// nothing
		}
	}

}
