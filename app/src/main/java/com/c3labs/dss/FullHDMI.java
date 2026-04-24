package com.c3labs.dss;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FullHDMI extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "FullHDMI";

    private SurfaceView hdmiSuraface;

    //
    //
    //
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private final FullscreenActivity.CameraErrorCallback mErrorCallback = new FullscreenActivity.CameraErrorCallback();
    private int mHdmiPreviewHeight;
    private int mHdmiPreviewWidth;
    private int mDesiredPreviewHeight;
    private int mDesiredPreviewWidth;
    private AudioStream mAudioStream;
    private CamcorderProfile mProfile;

    private File mFile_setwaittime = null;


    private final String resolution = "sys.hdmiin.resolution";//sys_graphi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_h_d_m_i);

        getSupportActionBar().hide();


        hdmiSuraface = findViewById(R.id.home_ac_hdmi);
        EventBus.getDefault().register(this);

        //
        //
        //
        //
        //
        //
        //
        //
        mFile_setwaittime = new File("/sys/class/lt6911c/setwaittime");
        Write2File(mFile_setwaittime, "1");
//        NavigationBarStatusBar(this, true);

        mAudioStream = new AudioStream(getApplicationContext());
        mAudioStream.start(5);
        mAudioStream.setOnAudioStreamStatusListener(new AudioStream.OnAudioStreamStatusListener() {
            @Override
            public void OnAudioStreamStatusChanged(int audiostatus) {
                switch (audiostatus) {
                    case 6:
                        //sendTextMessage("record video ",INITDONE);
                        break;
                    case 8:
                        //sendTextMessage("record video ",AUDIOSTREAM);
                        break;
                }
                Log.d(TAG, "--------OnItemClickListener-----audiostatus=" + audiostatus);
            }
        });
//        }
    }

    @Override
    public void onBackPressed() {
//        if (!mRealtekeHdmi.isDisPlay()) {
//        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        //
        //
        //
        //
        //
        //GIada
        //GIada
        //GIada
        //GIada
        if (SystemProperties.getInt("user.mouse.status", 0) >= 1)
            SystemProperties.set("user.mouse.switch", "true");
//        if (isStartAudio)
        EnableHDMIInAudio(false);
//        dhandler.removeCallbacks(runnable);
//        PollingManager.getInstance().stop();
        SystemClock.sleep(500);
        Log.d(TAG, "---------------Write2File mFile_setwaittime 10-----------------");
        Write2File(mFile_setwaittime, "10");
        finish();
    }

    @Subscribe
    public void onMessageEvent(Object event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        }, 500);
    };
    public void Write2File(File file, String mode) {
        boolean check_exist = true;
        try {

            if ((file == null) || (!file.exists()) || (mode == null))
                check_exist = file.createNewFile();

            if (check_exist) {
                FileOutputStream fout = new FileOutputStream(file);
                PrintWriter pWriter = new PrintWriter(fout);
                pWriter.println(mode);
                pWriter.flush();
                pWriter.close();
                fout.close();
            }

        } catch (IOException re) {
            Log.d("test", "write error:" + re);
            return;
        }
    }


//    @Override
//    public void onDestroy() {
//        Log.d(TAG, "---------------onDestroy----------------");
//        super.onDestroy();
//        if (SystemProperties.getInt("user.mouse.status", 0) >= 1)
//            SystemProperties.set("user.mouse.switch", "true");
////        if (isStartAudio)
//        EnableHDMIInAudio(false);
////        dhandler.removeCallbacks(runnable);
////        PollingManager.getInstance().stop();
//        SystemClock.sleep(500);
//        Log.d(TAG, "---------------Write2File mFile_setwaittime 10-----------------");
//        Write2File(mFile_setwaittime, "10");
//        finish();
////        System.exit(0);
//    }


    private void setup() {
//        dynamic.addView(currencyView = inflater.inflate(R.layout.layout_currency_v, null));
//
//        imageViewNormal = currencyView.findViewById(R.id.imgV_ImageLayoutCurrencyV);
//            videoViewNormal = currencyView.findViewById(R.id.vidV_VideoLayoutCurrencyV);
//            videoViewNormal.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                    ++selectedScheduleNormal;
//                    handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);
//                    handlerSchedules.postDelayed(runnableSchedules, 1);
////                    Uri mUri = null;
////                    try {
////                        Field mUriField = VideoView.class.getDeclaredField("mUri");
////                        mUriField.setAccessible(true);
////                        mUri = (Uri) mUriField.get(videoViewNormal);
////                        Log.d(TAG, "onError: VidNormal++++++++++++++ " + mUri.toString());
////                        Log.d(TAG, "onError: VidNormal++++++++++++++ " + mUri.toString());
////                    } catch (Exception e) {
////                    }
//
//                    return true;
//                }
//            });
//            videoViewNormal.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mpNormal = mediaPlayer;
//                }
//            });


//        currencyMediaWrapper = currencyView.findViewById(R.id.rl_mediaWrapperLayoutCurrencyV);
//        hdmiSuraface = currencyView.findViewById(R.id.home_ac_hdmi);
//        allHDMIWrapper = currencyView.findViewById(R.id.rl_allHDMIWrapperLayoutCurrencyV);

//        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openCamera();
//                StartPreview();
//            }
//        });
//
//        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                releaseCameraResource();
//            }
//        });
//        hdmiSuraface = (SurfaceView) findViewById(R.id.surface_camera);
//        recordingstatusLl = (LinearLayout) findViewById(R.id.recordingstatus);
//        recordingView = (ImageView) findViewById(R.id.recordingImg);
//        animDance = (AnimationDrawable) recordingView.getBackground();
//        //recordingstatusLl.setVisibility(View.INVISIBLE);
//        btn_ll = findViewById(R.id.btn_ll);
//        tvRecordTime = findViewById(R.id.record_time);
        mSurfaceHolder = hdmiSuraface.getHolder();
//        btnStart = (Button) findViewById(R.id.btn_start);
//        btnPip = (Button) findViewById(R.id.btn_pip);
//        btnStart.setOnClickListener(startAction);
//        btnBkRecord = (Button) findViewById(R.id.btn_back_record);
//        btnBkRecord.setOnClickListener(startAction);
//        btnSetting = (Button) findViewById(R.id.btn_setting);
//        btnSetting.setOnClickListener(startAction);
//        findViewById(R.id.btn_pip).setOnClickListener(startAction);
//        findViewById(R.id.btn_file).setOnClickListener(startAction);

//        image_nosignal = (ImageView) findViewById(R.id.image_nosignal);

        mSurfaceHolder.addCallback(this);
        openCamera();
//        btnStart.setEnabled(false);
//        btnPip.setEnabled(false);
//        if (OneActivity.isvicescreen||getIntent().getBooleanExtra("isVice",false))
//            btnPip.setVisibility(View.GONE);

/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnStart.setEnabled(true);
                btnPip.setEnabled(true);
            }
        }, 1500);*/
    }




    @Override
    public void onStart() {
        Log.d(TAG, "---------------onStart-----------------");
        setup();
        super.onStart();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        try {
            if (mCamera == null) {
                openCamera();
            }
            if (null != mCamera) {
                StartPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "surfaceCreated: Failed");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mSurfaceHolder = surfaceHolder;
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        hdmiSuraface = null;
        mSurfaceHolder = null;
//        releaseMediaRecorder();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //

    public void getHdmiPreview() {
        boolean isResolutionP = true;
        String resolution_str = SystemProperties.get(resolution, "1920x1080p60");
        Log.v("ccxbg", "getHdmiPreview - get " + resolution + " = " + resolution_str);
        if (resolution_str.contains("i") || resolution_str.contains("I")) {
            isResolutionP = false;
        }
        String res[] = resolution_str.split("x|p|i|P|X|I");
        if (res == null || res.length != 3) {
            Log.e("ccxbg", "getHdmiPreview - resolution property parse error, set default(1920x1080p60)");
            mHdmiPreviewWidth = 1920;
            mHdmiPreviewHeight = 1080;
        } else {
            mHdmiPreviewWidth = Integer.parseInt(res[0]);
            mHdmiPreviewHeight = Integer.parseInt(res[1]);
        }
        Log.v("ccxbg", "getHdmiPreview - mHdmiPreviewWidth=" + mHdmiPreviewWidth + " mHdmiPreviewHeight=" + mHdmiPreviewHeight);
    }

    public void StartPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            readVideoPreferences();
            try {
                mCamera.setErrorCallback(mErrorCallback);
                getHdmiPreview();
                mParameters.setPreviewSize(mHdmiPreviewWidth, mHdmiPreviewHeight);
                int[] fpsRange = CameraUtil.getMaxPreviewFpsRange(mParameters);
                if (fpsRange.length > 0) {
                    mParameters.setPreviewFpsRange(
                            fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                            fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
                } else {
                    mParameters.setPreviewFrameRate(mProfile.videoFrameRate);
                }
                mParameters.set("recording-hint", "true");

                String vstabSupported = mParameters.get("video-stabilization-supported");
                if ("true".equals(vstabSupported)) {
                    mParameters.set("video-stabilization", "true");
                }

                List<Camera.Size> supported = mParameters.getSupportedPictureSizes();
                Camera.Size optimalSize = CameraUtil.getOptimalVideoSnapshotPictureSize(supported,
                        (double) mDesiredPreviewWidth / mDesiredPreviewHeight);
                Camera.Size original = mParameters.getPictureSize();
                if (!original.equals(optimalSize)) {
                    mParameters.setPictureSize(optimalSize.width, optimalSize.height);
                }
                Log.v("ccxbg", "Video snapshot size is " + optimalSize.width + "x" +
                        optimalSize.height);

                // Set JPEG quality.
                int jpegQuality = CameraProfile.getJpegEncodingQualityParameter(0,
                        CameraProfile.QUALITY_HIGH);
                mParameters.setJpegQuality(jpegQuality);

                mCamera.setParameters(mParameters);
                // Keep preview size up to date.
                mParameters = mCamera.getParameters();

                mCamera.setPreviewDisplay(hdmiSuraface.getHolder());
            } catch (Exception ex) {
                Log.d(TAG, "setPreviewDisplay Exception: " + ex);
                ex.printStackTrace();
            }
            try {
                mCamera.startPreview();
//                if (!isStartAudio)
                EnableHDMIInAudio(true);
            } catch (Exception e) {
                Log.d(TAG, "startPreview Exception: " + e);
                e.printStackTrace();
                closeCamera();
            }
        }
    }

    private void EnableHDMIInAudio(boolean enable) {
//        isStartAudio = enable;
        if (enable) {
            mAudioStream.start(5);
        } else {
            mAudioStream.stop();
        }
    }

    private void closeCamera() {
        Log.d("ccxbg", "closeCamera");
        if (mCamera == null) {
            Log.d("ccxbg", "already stopped.");
            return;
        }
        mCamera.setZoomChangeListener(null);
        mCamera.setErrorCallback(null);
        hdmiSuraface.getHolder().getSurface().release();
        mCamera = null;
    }


    private void readVideoPreferences() {
        // The preference stores values from ListPreference and is thus string type for all values.
        // We need to convert it to int manually.
        int defaultQuality = CamcorderProfile.QUALITY_1080P;
        if ("2".equals(SystemProperties.get(resolution, "1"))) {
            defaultQuality = CamcorderProfile.QUALITY_720P;

            Log.i("ccxbg", "720p");
        }
        //int videoQuality = CameraUtil.getSupportedHighestVideoQuality(mCameraId, defaultQuality);
        int quality = Integer.valueOf(defaultQuality);
        mProfile = CamcorderProfile.get(0, quality);
        Log.i("ccxbg", "QUALITY" + quality);
        getDesiredPreviewSize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void getDesiredPreviewSize() {
        if (mCamera == null) {
            return;
        }
        mParameters = mCamera.getParameters();
        if (mParameters.getSupportedVideoSizes() == null) {
            mDesiredPreviewWidth = mProfile.videoFrameWidth;
            mDesiredPreviewHeight = mProfile.videoFrameHeight;

        } else { // Driver supports separates outputs for preview and video.
            List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
            Camera.Size preferred = mParameters.getPreferredPreviewSizeForVideo();
            int product = preferred.width * preferred.height;
            Iterator<Camera.Size> it = sizes.iterator();
            // Remove the preview sizes that are not preferred.
            while (it.hasNext()) {
                Camera.Size size = it.next();
                if (size.width * size.height > product) {
                    it.remove();
                }
            }
            DisplayMetrics dm = new DisplayMetrics();
            dm = getResources().getDisplayMetrics();
            Camera.Size optimalSize = CameraUtil.getOptimalPreviewSize(dm, sizes,
                    (double) mProfile.videoFrameWidth / mProfile.videoFrameHeight);
            //(double) 640 / 480);
            mDesiredPreviewWidth = optimalSize.width;
            mDesiredPreviewHeight = optimalSize.height;
        }
        Log.v("ccxbg", "mDesiredPreviewWidth=" + mDesiredPreviewWidth +
                ". mDesiredPreviewHeight=" + mDesiredPreviewHeight);
    }

    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //

    /**
     * 打开相机
     */
    private void openCamera() {
        if (null != mCamera) {
            releaseCameraResource();
        }
        try {
            mCamera = Camera.open(0);
            mCamera.enableShutterSound(false);
        } catch (Exception e) {
            e.printStackTrace();
            releaseCameraResource();
        }
    }

    /**
     * 释放摄像头资源
     */
    private void releaseCameraResource() {
        EnableHDMIInAudio(false);
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

}