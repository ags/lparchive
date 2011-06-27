package org.ags.lparchive.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class DownloadLPTask extends ProgressTask {
	LPArchiveApplication lpaa;
	DataHelper dh;
	private long lpId;
	private Cursor cursor;
	int max;
	
	public DownloadLPTask(Activity activity, long lpId) {
		super(activity, "Downloading...");
		this.lpId = lpId;
		this.lpaa = (LPArchiveApplication) activity.getApplicationContext();
		this.dh = lpaa.getDataHelper();
		this.cursor = dh.getChapters(lpId);
		this.max = cursor.getCount();
		
		dialog.setMax(max);
		dialog.setProgress(0);
	}

	@Override
	protected String doInBackground(Context... params) {
		String extState = Environment.getExternalStorageState();
		if (!extState.equals(Environment.MEDIA_MOUNTED)) {
			// toast
		}

		String sdcard_path = Environment.getExternalStorageDirectory().toString() + "/";
		String introUrl = activity.getString(R.string.intro_url);
		String baseUrl = activity.getString(R.string.base_url);
		String lpUrl = dh.getLP(lpId).getUrl();
		String package_name = activity.getString(R.string.package_name);
		String chapterUrl, getUrl, diskUrl, imgSrc, imgOut;
		boolean isIntro;
		int i = 0;
		if (cursor.moveToFirst()) {
			do {
				chapterUrl = cursor.getString(2);
				
				isIntro = chapterUrl.equals(introUrl);
				diskUrl = sdcard_path + package_name + lpUrl;
				getUrl = baseUrl + lpUrl;
				if(!isIntro) {
					diskUrl += chapterUrl;
					getUrl += chapterUrl;
				}
				
				Element content = PageFetchTask.getPageElement(getUrl, isIntro, lpaa);

				try {
					// create path to file
					File f = new File(diskUrl + "/chapter.html");
					f.getParentFile().mkdirs();
					// write out
					PrintWriter pw = new PrintWriter(f);
					pw.write(content.toString());
					pw.close();
					
					// insert location into db
					
					// save images
					for (Element img : content.getElementsByTag("img")) {
						imgSrc = img.attr("src");
						imgOut = diskUrl + imgSrc;
						Log.d("LPA", "src: " + getUrl + imgSrc);
						Log.d("LPA", "out: " + imgOut);
						downloadFromUrl(getUrl + imgSrc, imgOut);
					}
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// update progress bar
				publishProgress((int) ((i++ / (float)max) * 100));
			} while (cursor.moveToNext());
		}
		return "done";
	}

	public void downloadFromUrl(String imageURL, String fileName)
			throws Exception {
		String extState = Environment.getExternalStorageState();
		if (!extState.equals(Environment.MEDIA_MOUNTED))
			throw new Exception();
		// Connect to the URL
		URL myImageURL = new URL(imageURL);
		HttpURLConnection connection = (HttpURLConnection) myImageURL
				.openConnection();
		connection.setDoInput(true);
		connection.connect();
		InputStream input = connection.getInputStream();

		// Get the bitmap
		Bitmap myBitmap = BitmapFactory.decodeStream(input);

		// Save the bitmap to the file
		String path = Environment.getExternalStorageDirectory().toString();
		OutputStream fOut = null;
		File file = new File(path, fileName);
		file.getParentFile().mkdirs();
		fOut = new FileOutputStream(file);

		myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
		fOut.flush();
		fOut.close();
	}

	protected void onProgressUpdate(Integer... progress) {
		dialog.setProgress(progress[0]);
	}
}
