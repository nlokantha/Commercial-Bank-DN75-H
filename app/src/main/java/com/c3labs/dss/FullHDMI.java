package com.c3labs.dss;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public class FullHDMI extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "FullHDMI";
    private static final int CAMERA_ID_HDMI = 0;
    private static final int AUDIO_OUTPUT_ALL = 5;
    private static final long CLOSE_DELAY_MS = 500L;
    private static final long PREVIEW_RETRY_DELAY_MS = 250L;
    private static final int MAX_PREVIEW_RETRY_COUNT = 8;

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final Camera.ErrorCallback mCameraErrorCallback = new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera) {
            Log.e(TAG, "Camera error callback: " + error);
            if (error == Camera.CAMERA_ERROR_SERVER_DIED) {
                releaseCamera();
                finish();
            }
        }
    };
    private final Runnable mCloseRunnable = new Runnable() {
        @Override
        public void run() {
            onBackPressed();
        }
    };
    private final Runnable mPreviewStartRunnable = new Runnable() {
        @Override
        public void run() {
            startPreviewIfReady();
        }
    };

    private SurfaceView mHdmiSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private AudioStream mAudioStream;
    private CamcorderProfile mProfile;
    private File mSetWaitTimeFile;

    private int mHdmiPreviewHeight;
    private int mHdmiPreviewWidth;
    private int mDesiredPreviewHeight;
    private int mDesiredPreviewWidth;

    private boolean mSurfaceCreated;
    private boolean mSurfaceConfigured;
    private boolean mPreviewRunning;
    private boolean mAudioEnabled;
    private int mPreviewRetryCount;

    private final String resolution = "sys.hdmiin.resolution";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_h_d_m_i);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mHdmiSurfaceView = findViewById(R.id.home_ac_hdmi);
        mSurfaceHolder = mHdmiSurfaceView.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);

        EventBus.getDefault().register(this);

        mSetWaitTimeFile = new File("/sys/class/lt6911c/setwaittime");
        writeToFile(mSetWaitTimeFile, "1");

        mAudioStream = new AudioStream(getApplicationContext());
        mAudioStream.setOnAudioStreamStatusListener(new AudioStream.OnAudioStreamStatusListener() {
            @Override
            public void OnAudioStreamStatusChanged(int audioStatus) {
                Log.d(TAG, "Audio stream status=" + audioStatus);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mMainHandler.removeCallbacks(mPreviewStartRunnable);
        stopPreview();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        mMainHandler.removeCallbacks(mCloseRunnable);
        mMainHandler.removeCallbacks(mPreviewStartRunnable);

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
        }

        stopPreview();
        releaseCamera();

        if (SystemProperties.getInt("user.mouse.status", 0) >= 1) {
            SystemProperties.set("user.mouse.switch", "true");
        }

        SystemClock.sleep(500);
        writeToFile(mSetWaitTimeFile, "10");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Subscribe
    public void onMessageEvent(Object event) {
        if (!(event instanceof String) || !"FULLHDMI_CLOSE".equals(event)) {
            return;
        }
        mMainHandler.removeCallbacks(mCloseRunnable);
        mMainHandler.postDelayed(mCloseRunnable, CLOSE_DELAY_MS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        mSurfaceHolder = holder;
        mSurfaceCreated = true;
        mSurfaceConfigured = false;
        mPreviewRetryCount = 0;
        openCameraIfNeeded();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged " + width + "x" + height + " format=" + format);
        mSurfaceHolder = holder;
        mSurfaceConfigured = isSurfaceReadyForPreview();

        if (!mSurfaceConfigured) {
            Log.w(TAG, "surfaceChanged: surface not configured yet");
            schedulePreviewRetry();
            return;
        }

        mPreviewRetryCount = 0;
        schedulePreviewStart(0L);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mSurfaceCreated = false;
        mSurfaceConfigured = false;
        mPreviewRetryCount = 0;
        mMainHandler.removeCallbacks(mPreviewStartRunnable);
        stopPreview();
        releaseCamera();
        mSurfaceHolder = null;
    }

    public void StartPreview() {
        schedulePreviewStart(0L);
    }

    private void startPreviewIfReady() {
        mMainHandler.removeCallbacks(mPreviewStartRunnable);

        if (!isSurfaceReadyForPreview()) {
            Log.w(TAG, "startPreviewIfReady: surface not ready");
            schedulePreviewRetry();
            return;
        }

        mSurfaceConfigured = true;

        if (!openCameraIfNeeded()) {
            schedulePreviewRetry();
            return;
        }

        try {
            if (mPreviewRunning) {
                mCamera.stopPreview();
                mPreviewRunning = false;
            }

            mCamera.setPreviewDisplay(mSurfaceHolder);
            configureCamera();
            mCamera.startPreview();
            mPreviewRunning = true;
            mPreviewRetryCount = 0;
            setAudioEnabled(true);
            Log.d(TAG, "Preview started");
        } catch (Exception exception) {
            Log.e(TAG, "startPreviewIfReady failed", exception);
            stopPreview();
            releaseCamera();
            schedulePreviewRetry();
        }
    }

    private boolean isSurfaceReadyForPreview() {
        if (!mSurfaceCreated || mSurfaceHolder == null || mHdmiSurfaceView == null) {
            return false;
        }

        if (mSurfaceHolder.getSurface() == null || !mSurfaceHolder.getSurface().isValid()) {
            return false;
        }

        if (mSurfaceHolder.getSurfaceFrame() == null
                || mSurfaceHolder.getSurfaceFrame().width() <= 0
                || mSurfaceHolder.getSurfaceFrame().height() <= 0) {
            return false;
        }

        return mHdmiSurfaceView.getWidth() > 0 && mHdmiSurfaceView.getHeight() > 0;
    }

    private void schedulePreviewStart(long delayMs) {
        mMainHandler.removeCallbacks(mPreviewStartRunnable);
        mMainHandler.postDelayed(mPreviewStartRunnable, delayMs);
    }

    private void schedulePreviewRetry() {
        if (!mSurfaceCreated) {
            return;
        }

        if (mPreviewRetryCount >= MAX_PREVIEW_RETRY_COUNT) {
            Log.e(TAG, "schedulePreviewRetry: giving up after " + mPreviewRetryCount + " attempts");
            return;
        }

        mPreviewRetryCount++;
        Log.w(TAG, "schedulePreviewRetry: attempt " + mPreviewRetryCount);
        schedulePreviewStart(PREVIEW_RETRY_DELAY_MS);
    }

    private void configureCamera() {
        mCamera.setErrorCallback(mCameraErrorCallback);
        readVideoPreferences();
        getHdmiPreview();

        if (mParameters == null) {
            mParameters = mCamera.getParameters();
        }

        mParameters.setPreviewSize(mHdmiPreviewWidth, mHdmiPreviewHeight);
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setFixedSize(mHdmiPreviewWidth, mHdmiPreviewHeight);
        }

        int[] fpsRange = CameraUtil.getMaxPreviewFpsRange(mParameters);
        if (fpsRange.length > 0) {
            mParameters.setPreviewFpsRange(
                    fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                    fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
            );
        } else {
            mParameters.setPreviewFrameRate(mProfile.videoFrameRate);
        }

        mParameters.set("recording-hint", "true");

        String stabilizationSupported = mParameters.get("video-stabilization-supported");
        if ("true".equals(stabilizationSupported)) {
            mParameters.set("video-stabilization", "true");
        }

        List<Camera.Size> supportedPictureSizes = mParameters.getSupportedPictureSizes();
        Camera.Size optimalPictureSize = CameraUtil.getOptimalVideoSnapshotPictureSize(
                supportedPictureSizes,
                (double) mDesiredPreviewWidth / mDesiredPreviewHeight
        );
        Camera.Size originalPictureSize = mParameters.getPictureSize();
        if (optimalPictureSize != null && !originalPictureSize.equals(optimalPictureSize)) {
            mParameters.setPictureSize(optimalPictureSize.width, optimalPictureSize.height);
        }

        if (optimalPictureSize != null) {
            Log.v(
                    "ccxbg",
                    "Video snapshot size is " + optimalPictureSize.width + "x" + optimalPictureSize.height
            );
        }

        int jpegQuality = CameraProfile.getJpegEncodingQualityParameter(
                CAMERA_ID_HDMI,
                CameraProfile.QUALITY_HIGH
        );
        mParameters.setJpegQuality(jpegQuality);

        mCamera.setParameters(mParameters);
        mParameters = mCamera.getParameters();
    }

    private void stopPreview() {
        setAudioEnabled(false);

        if (mCamera == null || !mPreviewRunning) {
            mPreviewRunning = false;
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (RuntimeException exception) {
            Log.w(TAG, "stopPreview failed: " + exception.getMessage());
        }

        mPreviewRunning = false;
    }

    private boolean openCameraIfNeeded() {
        if (mCamera != null) {
            return true;
        }

        try {
            Log.d(TAG, "Opening camera device " + CAMERA_ID_HDMI);
            mCamera = Camera.open(CAMERA_ID_HDMI);
            if (mCamera == null) {
                Log.e(TAG, "Camera.open returned null");
                return false;
            }

            try {
                mCamera.enableShutterSound(false);
            } catch (RuntimeException exception) {
                Log.w(TAG, "Failed to disable shutter sound: " + exception.getMessage());
            }

            mParameters = mCamera.getParameters();
            return true;
        } catch (RuntimeException exception) {
            Log.e(TAG, "openCameraIfNeeded failed", exception);
            Toast.makeText(this, "Failed to open HDMI camera", Toast.LENGTH_SHORT).show();
            releaseCamera();
            return false;
        }
    }

    private void releaseCamera() {
        if (mCamera == null) {
            mParameters = null;
            mProfile = null;
            return;
        }

        try {
            mCamera.setPreviewCallback(null);
            mCamera.setErrorCallback(null);
            mCamera.release();
        } catch (RuntimeException exception) {
            Log.w(TAG, "releaseCamera failed: " + exception.getMessage());
        }

        mCamera = null;
        mParameters = null;
        mProfile = null;
        mPreviewRunning = false;
    }

    private void setAudioEnabled(boolean enable) {
        if (mAudioStream == null || mAudioEnabled == enable) {
            return;
        }

        if (enable) {
            mAudioStream.start(AUDIO_OUTPUT_ALL);
        } else {
            mAudioStream.stop();
        }

        mAudioEnabled = enable;
    }

    private void getHdmiPreview() {
        String resolutionValue = SystemProperties.get(resolution, "1920x1080p60");
        Log.v("ccxbg", "getHdmiPreview - get " + resolution + " = " + resolutionValue);

        String[] values = resolutionValue.split("x|p|i|P|X|I");
        if (values.length != 3) {
            Log.e("ccxbg", "getHdmiPreview - resolution property parse error, using 1920x1080p60");
            mHdmiPreviewWidth = 1920;
            mHdmiPreviewHeight = 1080;
        } else {
            mHdmiPreviewWidth = Integer.parseInt(values[0]);
            mHdmiPreviewHeight = Integer.parseInt(values[1]);
        }

        Log.v(
                "ccxbg",
                "getHdmiPreview - mHdmiPreviewWidth=" + mHdmiPreviewWidth
                        + " mHdmiPreviewHeight=" + mHdmiPreviewHeight
        );
    }

    private void readVideoPreferences() {
        if (mCamera == null) {
            return;
        }

        if (mParameters == null) {
            mParameters = mCamera.getParameters();
        }

        int defaultQuality = CamcorderProfile.QUALITY_1080P;
        if ("2".equals(SystemProperties.get(resolution, "1"))) {
            defaultQuality = CamcorderProfile.QUALITY_720P;
            Log.i("ccxbg", "720p");
        }

        mProfile = CamcorderProfile.get(CAMERA_ID_HDMI, defaultQuality);
        Log.i("ccxbg", "QUALITY" + defaultQuality);
        getDesiredPreviewSize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void getDesiredPreviewSize() {
        if (mCamera == null || mProfile == null) {
            return;
        }

        mParameters = mCamera.getParameters();
        if (mParameters.getSupportedVideoSizes() == null) {
            mDesiredPreviewWidth = mProfile.videoFrameWidth;
            mDesiredPreviewHeight = mProfile.videoFrameHeight;
        } else {
            List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
            Camera.Size preferred = mParameters.getPreferredPreviewSizeForVideo();
            int preferredArea = preferred.width * preferred.height;
            Iterator<Camera.Size> iterator = sizes.iterator();
            while (iterator.hasNext()) {
                Camera.Size size = iterator.next();
                if (size.width * size.height > preferredArea) {
                    iterator.remove();
                }
            }

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            Camera.Size optimalSize = CameraUtil.getOptimalPreviewSize(
                    displayMetrics,
                    sizes,
                    (double) mProfile.videoFrameWidth / mProfile.videoFrameHeight
            );
            if (optimalSize != null) {
                mDesiredPreviewWidth = optimalSize.width;
                mDesiredPreviewHeight = optimalSize.height;
            } else {
                mDesiredPreviewWidth = mProfile.videoFrameWidth;
                mDesiredPreviewHeight = mProfile.videoFrameHeight;
            }
        }

        Log.v(
                "ccxbg",
                "mDesiredPreviewWidth=" + mDesiredPreviewWidth
                        + ". mDesiredPreviewHeight=" + mDesiredPreviewHeight
        );
    }

    private void writeToFile(File file, String value) {
        if (file == null || value == null) {
            Log.w(TAG, "writeToFile skipped: file or value is null");
            return;
        }

        FileOutputStream outputStream = null;
        PrintWriter writer = null;
        try {
            outputStream = new FileOutputStream(file);
            writer = new PrintWriter(outputStream);
            writer.println(value);
            writer.flush();
        } catch (IOException exception) {
            Log.w(TAG, "writeToFile failed for " + file.getAbsolutePath() + ": " + exception.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            } else if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
