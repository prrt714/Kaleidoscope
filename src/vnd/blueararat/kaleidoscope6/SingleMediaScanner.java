package vnd.blueararat.kaleidoscope6;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class SingleMediaScanner implements MediaScannerConnectionClient {

	private MediaScannerConnection mMs;
	private File mFile;
	private int i = 0, j;

	public SingleMediaScanner(Context context, File f) {
		mFile = f;
		mMs = new MediaScannerConnection(context, this);
		mMs.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		File[] files = mFile.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {

				return (filename.endsWith(".jpg") || filename.endsWith(".png"));
			}
		});
		if (files != null)
			j = files.length;
		if (files == null || j == 0)
			return;

		for (File file : files) {
			mMs.scanFile(file.getAbsolutePath(), null);
		}
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		i++;
		if (i == j) {
			mMs.disconnect();
			mMs = null;
		}
	}

}