package vnd.blueararat.kaleidoscope6;

import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private final SurfaceHolder mHolder;
	private Camera mCamera;
	private final KView mKView;
	private int previewWidth, previewHeight;
	Camera.Parameters mParameters;
	private static String sCameraEffect = Camera.Parameters.EFFECT_NONE;
	private int numberOfCameras;
	int cameraCurrentlyLocked;
	int defaultCameraId;
	private int bufsize;

	CameraPreview(Context context, KView kaleidoscopeView) {
		super(context);

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL); // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// mContext = context;
		numberOfCameras = Camera.getNumberOfCameras();

		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				defaultCameraId = i;
			}
		}

		mKView = kaleidoscopeView;
	}

	private List<Size> supportedPreviewSizes;

	List<Size> getSupportedPreviewSizes() {
		return supportedPreviewSizes;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		cameraCurrentlyLocked = defaultCameraId;
		mParameters = mCamera.getParameters();

		guessPreviewSize();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	void setPreviewSize(Size previewSize) {
		setPreviewSize(previewSize.width, previewSize.height);
	}

	void setPreviewSize(int width, int height) {
		previewWidth = width;
		previewHeight = height;
	}

	int getPreviewWidth() {
		return previewWidth;
	}

	int getPreviewHeight() {
		return previewHeight;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();

		mKView.resetSizes(previewWidth, previewHeight);
		mParameters.setPreviewSize(previewWidth, previewHeight);

		mParameters.setColorEffect(sCameraEffect);
		mCamera.setParameters(mParameters);

		mCamera.setPreviewCallbackWithBuffer(mKView);
		mCamera.startPreview();

		int imgformat = mParameters.getPreviewFormat();
		int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
		bufsize = (previewWidth * previewHeight * bitsperpixel) / 8 + 1; // +1
		mCamera.addCallbackBuffer(new byte[bufsize]);
	}

	public void switchCamera() {
		if (numberOfCameras == 1)
			return;
		if (mCamera != null) {
			mCamera.setPreviewCallbackWithBuffer(null);
			mCamera.stopPreview();
			// mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
		mCamera = Camera.open((cameraCurrentlyLocked + 1) % numberOfCameras);
		cameraCurrentlyLocked = (cameraCurrentlyLocked + 1) % numberOfCameras;
		mParameters = mCamera.getParameters();
		guessPreviewSize();
		mParameters.setPreviewSize(previewWidth, previewHeight);
		// requestLayout();
		mKView.resetSizes(previewWidth, previewHeight);
		mCamera.setParameters(mParameters);
		// mPreview.switchCamera(mCamera);
		mCamera.setPreviewCallbackWithBuffer(mKView);

		int imgformat = mParameters.getPreviewFormat();
		int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);

		mCamera.addCallbackBuffer(new byte[(previewWidth * previewHeight * bitsperpixel) / 8 + 1]);
		mCamera.startPreview();

	}

	boolean canAutoFocus() {
		String focusMode = mParameters.getFocusMode();
		if (focusMode != null) {
			return focusMode.equals(Camera.Parameters.FOCUS_MODE_AUTO)
					|| focusMode.equals(Camera.Parameters.FOCUS_MODE_MACRO);
		}
		return false;
	}

	public void setCameraEffect() {
		mParameters.setColorEffect(sCameraEffect);
		mCamera.setParameters(mParameters);
	}

	public static void setEffect(String string) {
		sCameraEffect = string;
	}

	private void guessPreviewSize() {
		supportedPreviewSizes = mParameters.getSupportedPreviewSizes();
		int w = 0;
		int h = 0;
		for (Size sps : supportedPreviewSizes) {
			// Log.i(TAG, String.format("%d---%d", sps.width, sps.height));
			if (sps.width >= w) {
				if (sps.height >= h) {
					w = sps.width;
					h = sps.height;
				}
			}
		}
		previewWidth = w;
		previewHeight = h;
	}

	// private void guessPictureSize() {
	// supportedPictureSizes = mParameters.getSupportedPictureSizes();
	// int w = previewWidth;
	// int h = previewHeight;
	// for (Size sps : supportedPictureSizes) {
	// Log.i(TAG, String.format("%d===%d", sps.width, sps.height));
	// if (sps.width >= previewWidth) {
	// if (sps.height >= previewHeight) {
	// w = sps.width;
	// h = sps.height;
	// //break;
	// }
	// }
	// }
	// pictureWidth = w;
	// pictureHeight = h;
	// Log.i(TAG, String.format("%d+++%d", w, h));
	// }

	// private boolean validFocusMode(String mode) {
	// for (String m : supportedFocusModes)
	// if (m.equals(mode)) return true;
	// return false;
	// }

	int currentPreviewSizeIndex() {
		for (int i = 0; i < supportedPreviewSizes.size(); i++) {
			Size sps = supportedPreviewSizes.get(i);
			if (sps.width == previewWidth && sps.height == previewHeight)
				return i;
		}
		throw new Error("This should never happen");
	}

	void autoFocus(AutoFocusCallback cb) {
		mCamera.autoFocus(cb);
	}

	public int getNumberOfCameras() {
		return numberOfCameras;
	}

	public void takePicture() {
		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				return mKView.exportImage();
			}

			@Override
			protected void onPostExecute(String result) {
				mKView.toastString(result);
			}
		}).execute();
	}
}
