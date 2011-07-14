package org.ags.lparchive.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

import org.ags.lparchive.DataHelper;
import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.R;
import org.ags.lparchive.RetCode;
import org.jsoup.nodes.Element;

import android.app.ProgressDialog;
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
	
	public DownloadLPTask(Context context, long lpId) {
		super(context, "Downloading...");
		this.lpId = lpId;
		this.lpaa = (LPArchiveApplication) context.getApplicationContext();
		this.dh = lpaa.getDataHelper();
		this.cursor = dh.getChapters(lpId);
		this.max = cursor.getCount();
		
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		dialog.setMax(max);
		dialog.setProgress(0);
		// TODO dealing with incomplete fetches?
		dialog.setCancelable(true);
	}

	@Override
	protected RetCode doInBackground(Void... unused) {
		String extState = Environment.getExternalStorageState();
		if (!extState.equals(Environment.MEDIA_MOUNTED)) {
			return RetCode.MEDIA_UNMOUNTED;
		}

		String sdcard_path = Environment.getExternalStorageDirectory().toString() + "/";
//		String introUrl = context.getString(R.string.intro_url);
//		String baseUrl = context.getString(R.string.base_url);
		String lpUrl = dh.getLP(lpId).getUrl();
		String package_name = context.getString(R.string.package_name);
		String chapterUrl, getUrl, diskUrl, imgSrc, imgOut;
		boolean isIntro;
		int i = 0;
		if (cursor.moveToFirst()) {
			do {
				chapterUrl = cursor.getString(2);
				
				isIntro = chapterUrl.equals(LPArchiveApplication.introURL);
				diskUrl = sdcard_path + package_name + lpUrl;
				getUrl = LPArchiveApplication.baseURL + lpUrl;
				if(!isIntro) {
					diskUrl += chapterUrl;
					getUrl += chapterUrl;
				}
				
				try {
					Element content = PageFetchTask.getPageElement(getUrl, isIntro, lpaa);
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
		return RetCode.SUCCESS;
	}

	public void downloadFromUrl(String imageURL, String fileName)
			throws Exception {	
		Log.d("LPA", "img begin");
		// Connect to the URL
		URL url = new URL(imageURL);
		InputStream input = url.openConnection().getInputStream();

		// Get the bitmap
		Bitmap myBitmap = BitmapFactory.decodeStream(input);

		// Save the bitmap to the file
//		file.getParentFile().mkdirs();
		OutputStream fOut = new FileOutputStream(new File(fileName));

		myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
		fOut.close();
		Log.d("LPA", "img end");
	}

	protected void onProgressUpdate(Integer... progress) {
		Log.d("LPA", "progress: " + progress[0]);
		dialog.setProgress(progress[0]);
	}
}
