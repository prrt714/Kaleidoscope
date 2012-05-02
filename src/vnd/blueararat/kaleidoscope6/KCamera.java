package vnd.blueararat.kaleidoscope6;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class KCamera extends Activity {
	static final String KEY_COLOR_FILTER = "color_filter";

	// private static final String TAG = "KCamera";
	private FrameLayout mFrame;
	private KView mKView;
	private CameraPreview mCameraPreview;
	// private Camera mCamera;
	private int numberOfCameras;
	SharedPreferences preferences;
	// private ListView lv;
	private boolean inMenu = false;
	private ListView mLv;
	// private LinearLayout mLl;
	private FrameLayout mFl;
	private YUVProcessor mYUVProcessor;

	// private boolean mAlpha;

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// //super.onConfigurationChanged(newConfig);
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Bitmap bitmap = Bitmap.createBitmap(640, 480,
		// Bitmap.Config.ARGB_8888);
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.transparent, options);
		mKView = new KView(this, bitmap);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		Integer processorIndex = preferences.getInt(KEY_COLOR_FILTER, 0);
		mYUVProcessor = YUVProcessor.YUV_PROCESSORS[processorIndex];
		mKView.setYUVProcessor(mYUVProcessor);
		// mAlpha = preferences.getBoolean(KView.KEY_BLUR, false);
		// mKView.setAlpha(mAlpha);
		CameraPreview.setEffect(mYUVProcessor.getEffect());
		// sStringUri = preferences.getString(KEY_IMAGE_URI, "");
		mCameraPreview = new CameraPreview(this, mKView);
		// mCamera = mCameraPreview.getCamera();

		// Log.v(TAG, processorIndex.toString());
		// SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		// int width = preferences.getInt("previewWidth", 0);
		// int height = preferences.getInt("previewHeight", 0);
		// mCameraPreview.setPreviewSize(640, 480);
		// mCameraPreview.setFocusMode(preferences.getString("focusMode",
		// null));

		setContentView(R.layout.main);
		mFrame = (FrameLayout) findViewById(R.id.frame);

		// ViewGroup.LayoutParams params = new
		// ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		// mCameraPreview.setLayoutParams(params);
		mFrame.addView(mCameraPreview);
		// mCameraPreview.setVisibility(View.GONE);
		mFrame.addView(mKView);
		numberOfCameras = mCameraPreview.getNumberOfCameras();
	}

	// private OrientationEventListener mOrientationEventListener;
	// private int mOrientation = -1;
	// private static final int ORIENTATION_PORTRAIT_NORMAL = 1;
	// private static final int ORIENTATION_PORTRAIT_INVERTED = 2;
	// private static final int ORIENTATION_LANDSCAPE_NORMAL = 3;
	// private static final int ORIENTATION_LANDSCAPE_INVERTED = 4;

	@Override
	protected void onResume() {
		super.onResume();

		// String s = preferences.getString(Kaleidoscope.KEY_IMAGE_URI, "");
		// boolean b = !s.equals(sStringUri);
		// Log.v(TAG, "123231234234234");
		// Log.v(TAG, s);
		// Log.v(TAG, b);

		int numberOfMirrors = KView.MIN_NOM
				+ preferences.getInt(KView.KEY_NUMBER_OF_MIRRORS,
						6 - KView.MIN_NOM);
		// Editor edit = preferences.edit();
		// edit.putInt(KEY_NUMBER_OF_MIRRORS, 2);
		// edit.commit();
		// Toast.makeText(this, Integer.toString(numberOfMirrors) +
		if (numberOfMirrors != mKView.getNumberOfMirrors()) {
			mKView.setNewSettings(numberOfMirrors);
			// mNumberOfMirrors = numberOfMirrors;
			// Intent intent = getIntent();
			// finish();
			// mKView = null;
			// mCameraPreview = null;
			// mFrame = null;
			// mYUVProcessor = null;
			// startActivity(intent);
			// Toast.makeText(
			// this,
			// getString(R.string.toast_preference)
			// + Integer.toString(mNumberOfMirrors),
			// Toast.LENGTH_LONG).show();
		}
		boolean blur = preferences.getBoolean(KView.KEY_BLUR, true);
		if (blur != mKView.isBlur())
			mKView.setBlur(blur);

		if (blur) {
			int blurValue = (int) (2.55 * (99.0 - preferences.getInt(
					KView.KEY_BLUR_VALUE, 49)));
			if (blurValue != mKView.getBlurValue())
				mKView.setBlurValue(blurValue);
		}

		// sAngle = (float)180/(float)mNumberOfMirrors;
		// mBitmapNewHeight =
		// (int)((double)sRadius*Math.sin(sAngle*Math.PI/180));
		// mBitmapViewHeight = (int)((float)mBitmapNewHeight*sScale);
		// sNewBitmap = Bitmap.createBitmap(sRadius, mBitmapNewHeight,
		// Bitmap.Config.ARGB_8888);
		// sViewBitmap = Bitmap.createBitmap(mBitmapViewWidth,
		// mBitmapViewHeight,
		// Bitmap.Config.ARGB_8888);
		// KaleidoscopeView.drawIntoBitmap(sViewBitmap, mBitmap, sRadius/2,
		// sRadius/2);
		// Intent intent = getIntent();
		// finish();
		// startActivity(intent);
	}

	// mCameraPreview.setCameraEffect(mYUVProcessor.getEffect());
	//
	// if (mOrientationEventListener == null) {
	// mOrientationEventListener = new OrientationEventListener(this,
	// SensorManager.SENSOR_DELAY_NORMAL) {
	//
	// @Override
	// public void onOrientationChanged(int orientation) {
	//
	// // determine our orientation based on sensor response
	// int lastOrientation = mOrientation;
	//
	// if (orientation >= 315 || orientation < 45) {
	// if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
	// mOrientation = ORIENTATION_PORTRAIT_NORMAL;
	// }
	// }
	// else if (orientation < 315 && orientation >= 225) {
	// if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
	// mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
	// }
	// }
	// else if (orientation < 225 && orientation >= 135) {
	// if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
	// mOrientation = ORIENTATION_PORTRAIT_INVERTED;
	// }
	// }
	// else { // orientation <135 && orientation > 45
	// if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
	// mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
	// }
	// }
	//
	// if (lastOrientation != mOrientation) {
	// // changeRotation(mOrientation, lastOrientation);
	// }
	// }
	// };
	// }
	// if (mOrientationEventListener.canDetectOrientation()) {
	// mOrientationEventListener.enable();
	// }

	@Override
	protected void onPause() {
		super.onPause();
		// mOrientationEventListener.disable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.camera_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem focus = menu.findItem(R.id.focus);
		if (!mCameraPreview.canAutoFocus()) {
			focus.setVisible(false);
		} else {
			focus.setVisible(true);
		}
		// Toast.makeText(getApplicationContext(), "" + numberOfCameras,
		// Toast.LENGTH_SHORT).show();
		if (numberOfCameras == 1) {
			menu.removeItem(R.id.switch_camera);
		}
		return true;
	}

	// private boolean inMenu = false;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.switch_camera:
			// check for availability of multiple cameras
			if (numberOfCameras == 1) {
				// AlertDialog.Builder builder = new AlertDialog.Builder(this);
				// builder.setMessage(this.getString(R.string.camera_alert))
				// .setNeutralButton("Close", null);
				// AlertDialog alert = builder.create();
				// alert.show();
				return true;
			}
			mCameraPreview.switchCamera();
			return true;
		case R.id.focus:
			mCameraPreview.autoFocus(null);
			return true;
		case R.id.take_picture:
			mCameraPreview.takePicture();
			return true;
		case R.id.color_mode:
			if (inMenu == true)
				return true;
			showEffectsMenu();
			return true;
		case R.id.settings_c:
			startActivity(new Intent(this, Prefs.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// switch (item.getItemId()) {
	// case R.id.set_preview_size:
	// showPreviewSizeMenu();
	// return true;
	// case R.id.set_preview_processing_mode:
	// showEffectsMenu();
	// return true;
	// case R.id.set_focus_mode:
	// showFocusModeMenu();
	// return true;
	// case R.id.focus_now:
	// mCameraPreview.autoFocus(null);
	// return true;
	// case R.id.quit:
	// finish();
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }

	private void showEffectsMenu() {
		inMenu = true;
		mFrame.addView(getEffectsMenu());
		goIn(mFl);
		// setContentView(getEffectsMenu());
	}

	private FrameLayout getEffectsMenu() {
		if (mLv == null)
			buildEffectsMenu();
		return mFl;
	}

	// public void goIn(View view) {
	// Animation goInAnimation = AnimationUtils.loadAnimation(this,
	// android.R.anim.fade_in);
	// // Now Set your animation
	// view.startAnimation(goInAnimation);
	// // if (view.getVisibility() == View.VISIBLE) return;
	// //
	// // view.setVisibility(View.VISIBLE);
	// // Animation animation = new AlphaAnimation(0F, 1F);
	// // animation.setDuration(400);
	// // view.startAnimation(animation);
	// }

	public void goIn(View view) {
		Animation goInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.translation_menu_in);
		// Now Set your animation
		view.startAnimation(goInAnimation);
		// if (view.getVisibility() == View.VISIBLE) return;
		//
		// view.setVisibility(View.VISIBLE);
		// Animation animation = new AlphaAnimation(0F, 1F);
		// animation.setDuration(400);
		// view.startAnimation(animation);
		// Animation animation = new TranslateAnimation(-400, 0,0, 0);
		// animation.setDuration(2000);
		// view.startAnimation(animation);
	}

	// public void goOut(View view) {
	// Animation goOutAnimation = AnimationUtils.loadAnimation(this,
	// android.R.anim.fade_out);
	// // Now Set your animation
	// view.startAnimation(goOutAnimation);
	// // if (view.getVisibility() != View.VISIBLE) return;
	// //
	// // Animation animation = new AlphaAnimation(1F, 0F);
	// // animation.setDuration(400);
	// // view.startAnimation(animation);
	// // view.setVisibility(View.GONE);
	// }

	public void goOut(View view) {
		Animation goOutAnimation = AnimationUtils.loadAnimation(this,
				R.anim.translation_menu_out);
		// Now Set your animation
		goOutAnimation.reset();
		view.startAnimation(goOutAnimation);
		// if (view.getVisibility() != View.VISIBLE) return;
		//
		// Animation animation = new AlphaAnimation(1F, 0F);
		// animation.setDuration(400);
		// view.startAnimation(animation);
		// view.setVisibility(View.GONE);
		// Animation animation = new TranslateAnimation(0, -400,0, 0);
		// animation.setDuration(2000);
		// view.startAnimation(animation);
		// RAnimation.setAnimationListener(new AnimationListener() {
		//
		//
		// public void onAnimationStart(Animation arg0) {
		//
		// }
		//
		// public void onAnimationRepeat(Animation arg0) {
		//
		// }
		//
		//
		// public void onAnimationEnd(Animation arg0) {
		//
		// int p=0;
		// while(p<=1){
		// if(RAnimation.hasEnded()){
		// Toast.makeText(getApplicationContext(), "End",
		// Toast.LENGTH_SHORT).show();
		// p=2;
		// }
		// }
		//
		//
		// Trainplace(85, 170,bmpy1);
		//
		// Trainmove(-100);
		// }
		// });

	}

	private void buildEffectsMenu() {
		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.shape_menu);
		// Log.i(TAG,
		// Float.toString(getResources().getDisplayMetrics().density));
		// Image img;// = DecodeRes
		final float SCALE = getResources().getDisplayMetrics().density;

		FrameLayout fl = new FrameLayout(this);
		int w = Math.round(SCALE * 200.f);
		int h = Math.round(SCALE * 170.f);
		int pd = Math.round(10.f * SCALE);

		// DisplayMetrics displaymetrics = new DisplayMetrics();
		// int dp = (int) TypedValue.applyDimension(
		// TypedValue.COMPLEX_UNIT_DIP, 200, displaymetrics );
		// Log.v(TAG, Integer.toString(dp));
		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(w, h); // (
		// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// flp.height = 150;//iv.getHeight();
		// flp.width = 200;//iv.getWidth();
		fl.setLayoutParams(flp);
		fl.setPadding(pd, pd, 0, 0);

		// LayoutParams params = new FrameLayout.LayoutParams;
		// Changes the height and width to the specified *pixels*

		// fl.setLayoutParams(params);

		fl.addView(iv);
		// LinearLayout ll = new LinearLayout(this);
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// ll.setLayoutParams(lp);
		// ll.setGravity(Gravity.RIGHT);
		// fl.addView(ll);

		// ll.setBackgroundResource(R.drawable.background);

		ListView lv = new ListView(this);

		TextView header = new TextView(this);
		header.setText(getString(R.string.color_effect));
		lv.addHeaderView(header);
		// lv.setBackgroundColor(Color.TRANSPARENT);//R.color.popup_background_color
		// ListView.LayoutParams p = new ListView.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// MarginLayoutParams mp = new MarginLayoutParams(p);

		lv.setPadding(pd, pd, pd, pd);
		// lv.setPadding(10, 10, 10, 10);
		// lv.set
		// lv.setLayoutParams(p);
		final int positionOffset = 1;

		YUVProcessor.YUV_PROCESSORS[0].setString(getString(R.string.normal));
		YUVProcessor.YUV_PROCESSORS[1].setString(getString(R.string.gray));
		YUVProcessor.YUV_PROCESSORS[2].setString(getString(R.string.aqua));
		YUVProcessor.YUV_PROCESSORS[3].setString(getString(R.string.negative));

		lv.setAdapter(new ArrayAdapter<YUVProcessor>(this,
				R.layout.simple_list_item_single_choice,
				YUVProcessor.YUV_PROCESSORS));
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// lv.setCacheColorHint(Color.TRANSPARENT);
		lv.setVerticalFadingEdgeEnabled(false);
		lv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		lv.setSelector(R.drawable.list_selector);
		lv.setItemChecked(mKView.currentYUVProcessor() + positionOffset, true);
		// lv.getAdapter()

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) // header clicked
					return;
				// TextView label = (TextView)view;
				// label.setTextColor(Color.BLUE);
				mYUVProcessor = YUVProcessor.YUV_PROCESSORS[position
						- positionOffset];
				CameraPreview.setEffect(mYUVProcessor.getEffect());
				mCameraPreview.setCameraEffect();
				mKView.setYUVProcessor(mYUVProcessor);
				// mKView.setAlpha((mYUVProcessor.getName().equals("Blur") ? 10
				// : 255));
				Editor edit = preferences.edit();
				edit.putInt(KEY_COLOR_FILTER, position - positionOffset); // String(KEY_COLOR_FILTER,
																			// y.getName());
				edit.commit();
				// exitMenu();
			}
		});
		fl.addView(lv);
		mLv = lv;
		// mLl = ll;
		// fl.setVisibility(View.GONE);
		mFl = fl;
	}

	private void exitMenu() {
		// setContentView(mFrame);
		goOut(mFl);
		mFrame.removeView(mFl);
		inMenu = false;
	}

	// private void showFocusModeMenu() {
	// inMenu = true;
	// setContentView(getFocusModeMenu());
	// }

	// ListView focusModeMenu;
	// private ListView getFocusModeMenu() {
	// if (focusModeMenu == null) buildFocusModeMenu();
	// return focusModeMenu;
	// }

	// private void buildFocusModeMenu() {
	// ListView lv = new ListView(this);
	//
	// TextView header = new TextView(this);
	// header.setText(getString(R.string.set_focus_mode));
	// lv.addHeaderView(header);
	// final int positionOffset = 1;
	//
	// final List<String> focusModes = mCameraPreview.getSupportedFocusModes();
	// lv.setAdapter(new ArrayAdapter<String>(this,
	// android.R.layout.simple_list_item_single_choice, focusModes));
	// lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	// lv.setItemChecked(mCameraPreview.currentFocusModeIndex()+positionOffset,
	// true);
	//
	// lv.setOnItemClickListener(new OnItemClickListener() {
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// mCameraPreview.setFocusMode(focusModes.get(position-positionOffset));
	// exitMenu();
	// }
	// });
	//
	// focusModeMenu = lv;
	// }

	// private void showPreviewSizeMenu() {
	// inMenu = true;
	// setContentView(getPreviewSizeMenu());
	// }

	// private ListView previewSizeMenu;
	// private ListView getPreviewSizeMenu() {
	// if (previewSizeMenu == null) buildPreviewSizeMenu();
	// return previewSizeMenu;
	// }

	// private void buildPreviewSizeMenu() {
	// ListView lv = new ListView(this);
	//
	// TextView header = new TextView(this);
	// header.setText(getString(R.string.set_preview_size));
	// lv.addHeaderView(header);
	// final int positionOffset = 1;
	//
	// final List<Size> previewSizes =
	// mCameraPreview.getSupportedPreviewSizes();
	// String[] previewSizeNames = new String[previewSizes.size()];
	// for (int i = 0; i < previewSizes.size(); i++) {
	// Size size = previewSizes.get(i);
	// previewSizeNames[i] = size.width + "x" + size.height;
	// }
	// lv.setAdapter(new ArrayAdapter<String>(this,
	// android.R.layout.simple_list_item_single_choice, previewSizeNames));
	// lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	//
	// int selectedPosition = mCameraPreview.currentPreviewSizeIndex() +
	// positionOffset;
	// lv.setItemChecked(selectedPosition, true);
	// lv.setSelection(selectedPosition);
	//
	// lv.setOnItemClickListener(new OnItemClickListener() {
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// mCameraPreview.setPreviewSize(previewSizes.get(position-positionOffset));
	// exitMenu();
	// }
	// });
	//
	// previewSizeMenu = lv;
	// }

	// private void showEffectsMenu() {
	// inMenu = true;
	// setContentView(getEffectsMenu());
	// }

	// private ListView mLv;
	// private ListView getEffectsMenu() {
	// if (mLv == null) buildEffectsMenu();
	// return mLv;
	// }

	// private void buildEffectsMenu() {
	// ListView lv = new ListView(this);
	//
	// TextView header = new TextView(this);
	// header.setText(getString(R.string.set_preview_processing_mode));
	// lv.addHeaderView(header);
	// final int positionOffset = 1;
	//
	// lv.setAdapter(new ArrayAdapter<YUVProcessor>(this,
	// android.R.layout.simple_list_item_single_choice,
	// YUVProcessor.YUV_PROCESSORS));
	// lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	// lv.setItemChecked(mKView.currentYUVProcessor()+positionOffset,
	// true);
	//
	// lv.setOnItemClickListener(new OnItemClickListener() {
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// mKView.setYUVProcessor(YUVProcessor.YUV_PROCESSORS[position-positionOffset]);
	// exitMenu();
	// }
	// });
	// mLv = lv;
	// }

	@Override
	public void onBackPressed() {
		if (inMenu) {
			exitMenu();
		} else {
			finish();
			startActivity(new Intent(this, Kaleidoscope.class));
		}
	}

	// private void exitMenu() {
	// setContentView(mFrame);
	// inMenu = false;
	// }

	// @Override
	// protected void onPause() {
	// super.onPause();
	//
	// SharedPreferences preferences = getPreferences(MODE_PRIVATE);
	// SharedPreferences.Editor editor = preferences.edit();
	//
	// editor.putString("color_filter",
	// mKView.getYUVProcessor().getName());
	// editor.putInt("previewWidth", mCameraPreview.getPreviewWidth());
	// editor.putInt("previewHeight", mCameraPreview.getPreviewHeight());
	// editor.putString("focusMode", mCameraPreview.getFocusMode());
	//
	// editor.commit();
	// }
}