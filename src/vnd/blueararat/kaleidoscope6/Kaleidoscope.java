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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Kaleidoscope extends Activity {
	SharedPreferences preferences;
	static final String KEY_IMAGE_URI = "image_uri";
//	private static final String TAG = "Kaleidoscope";
	static final int CHANGE_NUMBER_OF_MIRRORS = 1;
	private static final int OPEN_PICTURE = 2;

	private int mNumberOfMirrors;
	// private static float sAngle;
	// private static float sLocalAngle = 0;
	// private static int mBitmapNewHeight, mBitmapViewHeight;
	// private static int sRadius, mBitmapViewWidth, sCenterX, sCenterY,
	// sScreenRadius;
	// private static float sScale;
	private Bitmap mBitmap;// sNewBitmap, sViewBitmap, mBitmap, sExportBitmap;
	// private static Bitmap sScaledBitmap;
	private Uri imageUri;
	private KView mK;
	private String sStringUri = "";

	// private static int sWidth, sHeight, sScaledHeight, mBitmapWidth,
	// mBitmapHeight;
	// private Menu mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		sStringUri = preferences.getString(KEY_IMAGE_URI, "");
		// Log.i(TAG,sStringUri);//String.format("%d", mBitmap.getWidth()));
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		if (sStringUri.length() != 0) {
			imageUri = Uri.parse(sStringUri);
			// Log.i(TAG,imageUri.toString());
			if (fileExists(imageUri)) {
				// Log.i(TAG, imageUri.toString());
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
			// logHeap(this.getClass());
			mK = null;
			mBitmap = null;
			System.gc();
			loadDefaultBitmap(options);
			mK = new KView(this, mBitmap);
		}
		// } finally {
		// // get ready to be fired by your boss
		// }
		// mK = new KaleidoscopeView(this, mBitmap);
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
		// For "Title only": Examples of matching an ID with one assigned in
		// the XML
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
			// Intent intent2 = new Intent(this, GetImageActivity.class);
			// startActivityForResult(intent2, OPEN_PICTURE);
			//Toast.makeText(this, "open", Toast.LENGTH_SHORT).show();

			return true;

			// For "Groups": Toggle visibility of grouped menu items with
			// nongrouped menu items
		case R.id.camera:
			// Toast.makeText(this, "camera", Toast.LENGTH_SHORT).show();
			Intent intent3 = new Intent(this, KCamera.class);
			// Intent intent4 = getIntent();
			finish();
			startActivity(intent3);
			return true;

		case R.id.export:
			export();
			return true;

			// // Generic catch all for all the other menu resources
			// default:
			// // Don't toast text when a submenu is clicked
			// if (!item.hasSubMenu()) {
			// Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT)
			// .show();
			// return true;
			// }
			// break;
		}

		return false;
	}

	private void export() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mK.exportImage();
			}
		});
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
				// mBitmap =
				// MediaStore.Images.Media.getBitmap(this.getContentResolver(),
				// imageUri);
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
			return true; // do something if it exists
		} else {
			return false;// File was not found
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

	// public void logHeap(Class clazz) {
	// Double allocated = new Double(Debug.getNativeHeapAllocatedSize())
	// / new Double((1048576));
	// Double available = new Double(Debug.getNativeHeapSize() / 1048576.0);
	// Double free = new Double(Debug.getNativeHeapFreeSize() / 1048576.0);
	// DecimalFormat df = new DecimalFormat();
	// df.setMaximumFractionDigits(2);
	// df.setMinimumFractionDigits(2);
	//
	// Log.d(TAG, "debug. =================================");
	// Log.d(TAG,
	// "debug.heap native: allocated " + df.format(allocated)
	// + "MB of " + df.format(available) + "MB ("
	// + df.format(free) + "MB free) in ["
	// + clazz.getName().replaceAll("com.myapp.android.", "")
	// + "]");
	// Log.d(TAG,
	// "debug.memory: allocated: "
	// + df.format(new Double(Runtime.getRuntime()
	// .totalMemory() / 1048576))
	// + "MB of "
	// + df.format(new Double(
	// Runtime.getRuntime().maxMemory() / 1048576))
	// + "MB ("
	// + df.format(new Double(Runtime.getRuntime()
	// .freeMemory() / 1048576)) + "MB free)");
	// Toast.makeText(this, "debug.memory: allocated: "
	// + df.format(new Double(Runtime.getRuntime()
	// .totalMemory() / 1048576))
	// + "MB of "
	// + df.format(new Double(
	// Runtime.getRuntime().maxMemory() / 1048576))
	// + "MB ("
	// + df.format(new Double(Runtime.getRuntime()
	// .freeMemory() / 1048576)) + "MB free)", Toast.LENGTH_LONG).show();
	// // Log.d(TAG, Long.toString(Runtime.getRuntime().maxMemory()));
	// // System.gc();
	// // System.gc();
	//
	// // don't need to add the following lines, it's just an app specific
	// // handling in my app
	// if (allocated >= (new Double(Runtime.getRuntime().maxMemory()) / new
	// Double(
	// (1048576)))) { // -MEMORY_BUFFER_LIMIT_FOR_RESTART
	// android.os.Process.killProcess(android.os.Process.myPid());
	// }
	// }
}