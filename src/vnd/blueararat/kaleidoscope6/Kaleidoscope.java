package vnd.blueararat.kaleidoscope6;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Kaleidoscope extends Activity {
	SharedPreferences preferences;
	static final String KEY_IMAGE_URI = "image_uri";
	// private static final String TAG = "Kaleidoscope";
	static final int CHANGE_NUMBER_OF_MIRRORS = 1;
	private static final int OPEN_PICTURE = 2;

	private int mNumberOfMirrors;
	private Bitmap mBitmap;// sNewBitmap, sViewBitmap, mBitmap, sExportBitmap;
	private Uri imageUri;
	private KView mK;
	private String sStringUri = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		sStringUri = preferences.getString(KEY_IMAGE_URI, "");
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		if (sStringUri.length() != 0) {
			imageUri = Uri.parse(sStringUri);
			if (fileExists(imageUri)) {
				try {
					mBitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), imageUri);
				} catch (FileNotFoundException e) {
					loadDefaultBitmap(options);
					e.printStackTrace();
				} catch (IOException e) {
					loadDefaultBitmap(options);
					e.printStackTrace();
				}
			} else {
				loadDefaultBitmap(options);
			}
		} else {
			loadDefaultBitmap(options);
		}
		try {
			// logHeap(this.getClass());
			mK = new KView(this, mBitmap);
		} catch (OutOfMemoryError e) {
			mK = null;
			mBitmap = null;
			System.gc();
			loadDefaultBitmap(options);
			mK = new KView(this, mBitmap);
		}
		mNumberOfMirrors = mK.getNumberOfMirrors();
		setContentView(mK);
	}

	private void loadDefaultBitmap(Options options) {
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img2,
				options);
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// mK.onResumeKSurfaceView();
	// }
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	// mK.onPauseKSurfaceView();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate our menu which can gather user input for switching camera
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		// mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent intent = new Intent(this, Prefs.class);
			startActivityForResult(intent, CHANGE_NUMBER_OF_MIRRORS);
			return true;

		case R.id.open:
			Intent intent2 = new Intent();
			intent2.setType("image/*");
			intent2.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent2,
					getString(R.string.open_picture)), OPEN_PICTURE);
			return true;

		case R.id.camera:
			Intent intent3 = new Intent(this, KCamera.class);
			finish();
			startActivity(intent3);
			return true;

		case R.id.export:
			new Export().execute();
			return true;
		}

		return false;
	}

	private class Export extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return mK.exportImage();
		}

		@Override
		protected void onPostExecute(String result) {
			mK.toastString(result);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == OPEN_PICTURE) {
			if (resultCode == RESULT_OK) {
				imageUri = data.getData();
				sStringUri = imageUri.toString();
				Editor et = preferences.edit();
				et.putString(KEY_IMAGE_URI, sStringUri);
				et.commit();
				Intent it = getIntent();
				finish();
				startActivity(it);
			}
		} else {
			String s = preferences.getString(KEY_IMAGE_URI, "");
			boolean b = !s.equals(sStringUri);

			if (requestCode == CHANGE_NUMBER_OF_MIRRORS || b) {
				int numberOfMirrors = KView.MIN_NOM
						+ preferences.getInt(KView.KEY_NUMBER_OF_MIRRORS,
								6 - KView.MIN_NOM);
				if (numberOfMirrors != mNumberOfMirrors || b) {
					Intent intent = getIntent();
					finish();
					startActivity(intent);
					return;
				}

			}
			boolean blur = preferences.getBoolean(KView.KEY_BLUR, true);
			if (blur != mK.isBlur())
				mK.setBlur(blur);
			if (blur) {
				int blurValue = (int) (2.55 * (99.0 - preferences.getInt(
						KView.KEY_BLUR_VALUE, 49)));
				if (blurValue != mK.getBlurValue())
					mK.setBlurValue(blurValue);
			}
		}
	}

	private boolean fileExists(Uri uri) {
		String filePath;
		try {
			filePath = getPath(uri);
		} catch (CursorIndexOutOfBoundsException e) {
			return false;
		}
		if (new File(filePath).exists()) {
			return true;
		} else {
			return false;
		}
	}

	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}