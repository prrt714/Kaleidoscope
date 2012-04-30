package vnd.blueararat.kaleidoscope6;

import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private final SurfaceHolder mHolder;
	private Camera mCamera;
	// private Context mContext;
	private final KView mKView;
//	private static final String TAG = "CameraPreview.java";
	private int previewWidth, previewHeight;
	// private static int sWidth, sHeight;
	Camera.Parameters mParameters;
	private static String sCameraEffect = Camera.Parameters.EFFECT_NONE;
	private int numberOfCameras;
	int cameraCurrentlyLocked;
	// The first rear facing camera
	int defaultCameraId;
	// private List<Size> supportedPictureSizes;
	private int bufsize;

	CameraPreview(Context context, KView kaleidoscopeView) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL); // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// mContext = context;
		numberOfCameras = Camera.getNumberOfCameras();

		// Find the ID of the default camera
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				defaultCameraId = i;
			}
		}

		mKView = kaleidoscopeView;
		// int[] a = kaleidoscopeView.getScreenDimensions();
		// sWidth = a[0];
		// sHeight = a[1];
	}

	private List<Size> supportedPreviewSizes;

	List<Size> getSupportedPreviewSizes() {
		return supportedPreviewSizes;
	}

	//
	// private List<String> supportedFocusModes;
	// List<String> getSupportedFocusModes() { return supportedFocusModes; }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where to
		// draw.
		mCamera = Camera.open();
		cameraCurrentlyLocked = defaultCameraId;
		// This needs to be available to the preview size menu
		mParameters = mCamera.getParameters();
		// mCamera.setDisplayOrientation(90);

		guessPreviewSize();
		// guessPictureSize();
		// Toast.makeText(mContext,
		// Integer.toString(pictureWidth)+"_"+Integer.toString(pictureHeight),
		// Toast.LENGTH_LONG).show();
		// supportedFocusModes = parameters.getSupportedFocusModes();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	// Apparently I can't create a new Size() without a camera instance to say
	// mCamera.new Size().
	// I don't get this. But the upshot is that it's more convenient to store
	// width and height than a Size object.
	// This way width and height can be set before the camera is created in
	// surfaceCreated.

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

	// private String focusMode;
	// void setFocusMode(String mode) { focusMode = mode; }
	// String getFocusMode() { return focusMode; }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.stopPreview();

		// mParameters = mCamera.getParameters();

		// Check previewWidth and previewHeight, if invalid just use what the
		// camera wanted
		// if (previewWidth == 0 || previewHeight == 0)
		// setPreviewSize(mParameters.getPreviewSize());
		// if (!validPreviewSize(previewWidth, previewHeight))
		// setPreviewSize(mParameters.getPreviewSize());
		// for (int i = 0; i < supportedPreviewSizes.size(); i++) {
		// Log.i(TAG, String.format("%d: %d x %d", i,
		// supportedPreviewSizes.get(i).width,
		// supportedPreviewSizes.get(i).height));
		// }
		// Log.i(TAG, "here");
		// // Check focusMode, if invalid just use what the camera wanted
		// if (focusMode == null) setFocusMode(mParameters.getFocusMode());
		// if (!validFocusMode(focusMode))
		// setFocusMode(mParameters.getFocusMode());

		// Set preview size, focus mode, notify camera
		// setPreviewSize(320, 240);
		mKView.resetSizes(previewWidth, previewHeight);
		mParameters.setPreviewSize(previewWidth, previewHeight);
		// mParameters.setPictureSize(pictureWidth, pictureHeight);
		// mParameters.setPictureFormat(ImageFormat.NV16);
		// mParameters.setFocusMode(focusMode);
		mParameters.setColorEffect(sCameraEffect);
		mCamera.setParameters(mParameters);

		// Reset ProcessedView, notify of preview size, set callback and start
		// camera!
		// mKView.reset(mParameters.getPreviewSize());
		mCamera.setPreviewCallbackWithBuffer(mKView);
		mCamera.startPreview();

		int imgformat = mParameters.getPreviewFormat();
		int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
		bufsize = (previewWidth * previewHeight * bitsperpixel) / 8 + 1; // +1
		mCamera.addCallbackBuffer(new byte[bufsize]);
		// Log.i(TAG, String.format("previewWidth %d", previewWidth));
		// Log.i(TAG, String.format("buffer %d",
		// (previewWidth * previewHeight * bitsperpixel) / 8));
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
		// Log.i(TAG, String.format("previewWidth %d", previewWidth));
		// Log.i(TAG, String.format("buffer %d",
		// (previewWidth * previewHeight * bitsperpixel) / 8));
		// Start the preview
		mCamera.startPreview();

	}

	boolean canAutoFocus() {
		String focusMode = mParameters.getFocusMode();
		if (focusMode != null) {
			return focusMode.equals(Camera.Parameters.FOCUS_MODE_AUTO)
					|| focusMode.equals(Camera.Parameters.FOCUS_MODE_MACRO);
		}
		return false;
		// String q = Camera.Parameters.FOCUS_MODE_AUTO;
		// String w = Camera.Parameters.FOCUS_MODE_MACRO;
		// if (q != null) {
		// if (focusMode.equals(q)) return true;
		// }
		// if (w != null) {
		// if (focusMode.equals(w)) return true;
		// }
		// return false;
	}

	public void setCameraEffect() {
		// if (string == null) return;
		// if (mCamera == null) return;
		// mCamera.stopPreview();
		mParameters.setColorEffect(sCameraEffect);
		mCamera.setParameters(mParameters);
		// mCamera.startPreview();
	}

	public static void setEffect(String string) {
		// if (string == null) return;
		// if (mCamera == null) return;
		// mCamera.stopPreview();
		sCameraEffect = string;
		// mParameters.setColorEffect(string);
		// mCamera.setParameters(mParameters);
		// mCamera.startPreview();
	}

//	private boolean validPreviewSize(int width, int height) {
//		for (Size sps : supportedPreviewSizes)
//			if (sps.width == width && sps.height == height)
//				return true;
//		return false;
//	}

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
		// Log.i(TAG, String.format("%d---%d", w, h));
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

	// int currentFocusModeIndex() {
	// for (int i = 0; i < supportedFocusModes.size(); i++)
	// if (supportedFocusModes.get(i).equals(focusMode)) return i;
	// throw new Error("This should never happen");
	// }

	void autoFocus(AutoFocusCallback cb) {
		mCamera.autoFocus(cb);
	}

//	public Camera getCamera() {
//		return mCamera;
//	}
	
	public int getNumberOfCameras() {
		return numberOfCameras;
	}

//	public void setCamera(Camera camera) {
//		mCamera = camera;
//	}

	public void takePicture() {
		mKView.exportImage();
	}
}
