package com.c3labs.dss;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.c3labs.dss.BroadCRecievers.MyBroadCastReciever;
import com.c3labs.dss.Clz.AutoScrollTextView;
import com.c3labs.dss.Clz.MyExceptionHandler;
import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.WebService.Asyncs.AsyncWebService;
import com.c3labs.dss.WebService.Asyncs.CustomWebViewClient;
import com.c3labs.dss.WebService.Asyncs.SSLHelper;
import com.c3labs.dss.WebService.Asyncs.Tasks.DoOnPostExecTasks;
import com.c3labs.dss.WebService.Refferences;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, SurfaceHolder.Callback {
    private static final String TAG = "+++++++++++++++++++++++";
    private final String[] imageFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
    //    LayoutControls layoutControls;
    public boolean isDownloading;
    //    Components
    public TextView progCount;
    public ImageView statusIcon;
    public ProgressBar progressBar;
    public AutoScrollTextView news;
    public String newsResult;
    LayoutInflater inflater;
    Handler handlerTemplateSequence;
    Runnable runnableTemplateSequence;
    Handler handlerSchedules;
    Runnable runnableSchedules;
    Handler handlerCatchFullPlayed;
    Runnable runnableCatchFullPlayed;
    Handler handlerSingleSecond;
    Runnable runnableSingleSecond;
    Handler handlerChangeBoolean;
    Runnable runnableChangeBoolean;
    Handler handler10Sec;
    Runnable runnable10Sec;
    Handler handlerDelay01;
    Runnable runnableDelay01;
    //
    Handler byHourHandler;
    Runnable byHourRunnable;
    //
    Surface surface;
    WebView webViewFull, webView;
    RelativeLayout.LayoutParams params;
    //    SimpleDateFormat simpleDateFormatTime;
    private JSONArray templateSeqJsonArray, normalSchedulesJsonArray, adFullScheduleJsonArray;
    private JSONObject jsonObjectInsideRunnableSchedules;
    private ArrayList<Date[]> adHocTimeArrayList;
    private int adHocStatus, tvOnwardsStatus;
    private int selectedLayout, selectedSchedule, selectedScheduleNormal;
    private int currentSecond = 0;
    //    private int networkSeconds = 0;
    private boolean changeAdFull, isDownloadStarted, isFirstTime = true;
    private SimpleDateFormat simpleDateFormatFull, simpleDateFormatTime;
    //    private ZidooHdmiDisPlay_New mRealtekeHdmi = null;
    private SurfaceView hdmiSuraface;
    private String[] currentLayoutDetails;
    private File file;
    private Date[] downloadStartTime;
    private Date date, dateTVOnwards;
    private RelativeLayout dynamic, currencyMediaWrapper, allHDMIWrapper;
    private TextView branchName, dateTime;
    private View adFullView, currencyView;
    //    private VideoView videoViewAdFull;
    private ImageView imageViewNormal, imageViewAdFull;
    private Bitmap bitmap;
    private TextureView mPreview;
    private MediaPlayer mMediaPlayer;
//    MediaPlayer mpNormal, mpAdFull;

    private BroadcastReceiver broadcastReceiver;
    //
    private Intent fullHdmiIntent;
    private boolean webViewLoaded = false;

    //
    //
    //
    //
    //
    //

    //    private static final String TAG = "MainActivity-----------";
//    private SurfaceView hdmiSuraface;

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();
    private int mHdmiPreviewHeight;
    private int mHdmiPreviewWidth;
    private int mDesiredPreviewHeight;
    private int mDesiredPreviewWidth;
    private AudioStream mAudioStream;
    private CamcorderProfile mProfile;

    private File mFile_setwaittime = null;


    private final String resolution = "sys.hdmiin.resolution";//sys_graphi

//    Namal add this...................

    boolean isCurrencyRound = true;

    //    .................................
    @Override
    public void onBackPressed() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.dialog_signin, null);
        ImageView asterikL = v.findViewById(R.id.imgV_asterikLeftActivitySplash);
        ImageView asterik = v.findViewById(R.id.imgV_asterikActivitySplash);
        ImageView asterikR = v.findViewById(R.id.imgV_asterikRightActivitySplash);
        final EditText userName = v.findViewById(R.id.et_dialog_signin_UserName);
        final EditText password = v.findViewById(R.id.et_dialog_signin_Password);
//        Button exit = v.findViewById(R.id.btn_dialog_signin_Exit);
        Button signIn = v.findViewById(R.id.btn_dialog_signin_SignIn);

        asterik.startAnimation(AnimationUtils.loadAnimation(this, R.anim.load_asterik));
        asterikL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        asterikR.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        signIn.setText("OK");
        userName.setVisibility(View.GONE);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(v).create();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().trim().equalsIgnoreCase(Methods.getSharedPref(FullscreenActivity.this).getString("ExitPassword", Math.random() + ""))) {
                    handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
                    handlerSchedules.removeCallbacks(runnableSchedules);
                    handlerSingleSecond.removeCallbacks(runnableSingleSecond);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    alertDialog.dismiss();
                }
            }
        });


        alertDialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
        initialize();


//        initializeHandlers();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
//        SSLHelper.setDefaultSSLSocketFactory(this);

//        registerHDMIPluggedReceiver();


        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormatFull = new SimpleDateFormat("dd-MMM-yyyy   hh:mm a");

        dynamic = findViewById(R.id.rl_DynamicLayoutActivityFullScreen);
        branchName = findViewById(R.id.tv_branchNameActivityFullScreen);
        dateTime = findViewById(R.id.tv_DateTimeActivityFullScreen);
        news = findViewById(R.id.tv_newsActivityFullScreenNews);
        statusIcon = findViewById(R.id.imgV_NetworkStatusActivityFullScreen);
        progressBar = findViewById(R.id.prog_DownloadedActivitySplash);
        progCount = findViewById(R.id.tv_progressCountActivityFullScreen);
        news.setSelected(true);

        statusIcon.setImageResource(new Methods().getNetworkStatusIcon(this));

        Display display = getWindowManager().getDefaultDisplay();
        Point m_size = new Point();
        display.getSize(m_size);
        int m_width = m_size.x;
        int m_height = m_size.y;

        findViewById(R.id.btn_TestC3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyExceptionHandler.restartApp(FullscreenActivity.this, Splash.class, new Exception("Restarted On C3 press"));
            }
        });

        findViewById(R.id.btn_TestC3).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new Methods().restartDevice(getApplicationContext(), "On C3 Long click");

                return false;
            }
        });
//
        findViewById(R.id.btn_test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    Toast.makeText(FullscreenActivity.this, pInfo.versionName, Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.tv_dash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, FullscreenActivity.class));

        mFile_setwaittime = new File("/sys/class/lt6911c/setwaittime");
        Write2File(mFile_setwaittime, "1");

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
    }

    private void hideAllLayouts() {
        hdmiSuraface.setVisibility(View.GONE);
        currencyMediaWrapper.setVisibility(View.INVISIBLE);
        releaseCameraResource();

    }


//    -------------------------
//    -------------------------
//    -------------------------
//    -------------------------
//    -------------------------

    private void initializeHandlers() {
        handlerChangeBoolean = new Handler();
        runnableChangeBoolean = new Runnable() {
            @Override
            public void run() {
                changeAdFull = true;
                handlerChangeBoolean.removeCallbacks(this);
            }
        };

        handlerTemplateSequence = new Handler();
        runnableTemplateSequence = new Runnable() {
            @Override
            public void run() {
                methodTemplateSeq();
            }
        };

        handlerTemplateSequence.postDelayed(runnableTemplateSequence, 1000);

        handlerSchedules = new Handler();
        runnableSchedules = new Runnable() {
            @Override
            public void run() {
                methodRunnableSchedules();
            }
        };
        handlerSchedules.postDelayed(runnableSchedules, 1500);

        handlerSingleSecond = new Handler();
        runnableSingleSecond = new Runnable() {
            @Override
            public void run() {
                methodHandlerSingleSecond();
            }
        };
        handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);

        handlerCatchFullPlayed = new Handler();
        runnableCatchFullPlayed = new Runnable() {
            @Override
            public void run() {
                ++selectedScheduleNormal;
                Log.d(TAG, "Current Sche: " + selectedScheduleNormal);
            }
        };

        handler10Sec = new Handler();
        runnable10Sec = new Runnable() {
            @Override
            public void run() {
                if (webView != null) {
                    webView.reload();
                    handler10Sec.removeCallbacks(runnable10Sec);
                    handler10Sec.postDelayed(runnable10Sec, (1000 * 60) * 10);
                } else {
                    handler10Sec.removeCallbacks(runnable10Sec);
                }

            }
        };
        handler10Sec.postDelayed(runnable10Sec, (1000 * 60) * 10);

        byHourHandler = new Handler();
        byHourRunnable = new Runnable() {
            @Override
            public void run() {
                byHourHandler.removeCallbacks(byHourRunnable);
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour <= 4) {
                    new Methods().restartDevice(FullscreenActivity.this, "Hour handler - " + hour);
                }
                byHourHandler.postDelayed(byHourRunnable, 60 * 1000 * 60);
            }
        };
        byHourHandler.postDelayed(byHourRunnable, 60 * 1000 * 60);

        handlerDelay01 = new Handler();
        runnableDelay01 = () -> {
            mPreview.setVisibility(View.VISIBLE);
            handlerDelay01.removeCallbacks(runnableDelay01);
        };
    }

    private void setTimePlusCheckDownloadingTimePlusAdHoc() {
        try {
            dateTime.setText(simpleDateFormatFull.format(date = new Date()));
            checkDownloadTime(date = simpleDateFormatTime.parse(simpleDateFormatTime.format(date)));
            if (dateTVOnwards != null) {
                if (date.after(dateTVOnwards)) {
                    if (tvOnwardsStatus == MyConstants.NOT_LOADED) {
                        tvOnwardsStatus = MyConstants.PLAYING;
                        news.stopScroll();
                        handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
                        handlerSchedules.removeCallbacks(runnableSchedules);
                        stopVideoView();
                        imageViewNormal.setImageResource(0);
                        releaseCameraResource();
                        setView("AdFull");
                        loadLayoutSchedules();
                        currentLayoutDetails = new String[]{"AdFull", ""};
                        startActivity(new Intent(this, FullHDMI.class));
                    }
//                    return;
                }
            }

            for (Date adHDate[] :
                    adHocTimeArrayList) {
                if (date.equals(adHDate[0]) ||
                        (date.after(adHDate[0]) && date.before(adHDate[1]))) {
                    Log.d(TAG, "setTimePlusCheckDownloadingTimePlusAdHoc: Insode" + adHocStatus);
                    tvOnwardsStatus = MyConstants.PAUSED;
                    getMyRequiredSS("OnStart");
                    if (adHocStatus == MyConstants.NOT_LOADED) {
                        EventBus.getDefault().post("");
                        if (dateTVOnwards != null && date.after(dateTVOnwards)) {
                            new DoOnPostExecTasks().saveNewsLines(newsResult, FullscreenActivity.this);
                        }
                        String url = Refferences.SchedulePlayStatus.methodName + Methods.getNodeId(FullscreenActivity.this) + File.separator +
                                configAdFullSchedules(getDateFromString(simpleDateFormatTime.format(date)));
                        Log.d(TAG, "startTime: " + url);
                        new AsyncWebService(FullscreenActivity.this, MyConstants.SCHEDULE_PLAY_STATUS).execute(url);


                        handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
                        handlerChangeBoolean.removeCallbacks(runnableChangeBoolean);
                        selectedSchedule = -1;
                        changeAdFull = false;

                        //                currentLayoutDetails = loadLayout();
                        setView("AdFull");
                        loadLayoutSchedules();
                        currentLayoutDetails = new String[]{"AdFull", ""};
                        //                handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
                        handlerSchedules.postDelayed(runnableSchedules, 1);
                        adHocStatus = MyConstants.PLAYING;
                    }
                    break;

//                    =======================================AdHoc End Function
                } else if (date.equals(adHDate[2]) ||
                        (date.after(adHDate[2]) && date.before(adHDate[3]))) {
                    getMyRequiredSS("OnEnd");
                    if (adHocStatus == MyConstants.PLAYING) {
                        MyValues.setResetApp(true);
                        adHocStatus = MyConstants.STOPPED;
                        if (tvOnwardsStatus == MyConstants.PAUSED) {
                            initialize();
                        } else {
                            adHocStatus = MyConstants.NOT_LOADED;
                        }
                        tvOnwardsStatus = MyConstants.NOT_LOADED;
                    }
                    break;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        if ()

    }

    private void checkDownloadTime(Date date) {
        Log.d(TAG, "checkDownloadTime: " + date.before(downloadStartTime[0]) + " end = " + date.after(downloadStartTime[1]));
        if (date.equals(downloadStartTime[0]) || (date.after(downloadStartTime[0]) && date.before(downloadStartTime[1]))) {
            if (!isDownloadStarted) {
                MyValues.setResetApp(true);
                beginDownload();
                isDownloadStarted = true;
            }

        }
    }


    private void loadLayoutSchedules() {
        if (currentLayoutDetails != null) {
            Toast.makeText(this, currentLayoutDetails[0], Toast.LENGTH_SHORT).show();
            if (currentLayoutDetails[0].equalsIgnoreCase("CurrencyHDMI")) {
                if (hdmiSuraface == null){
                    hdmiSuraface = currencyView.findViewById(R.id.home_ac_hdmi);
                }
                hdmiSuraface.setVisibility(View.VISIBLE);
                webView.setVisibility(View.VISIBLE);
                if (webView.getUrl() == null)
                    webView.loadUrl(Refferences.GetCurrency.method);
                currencyMediaWrapper.setVisibility(View.GONE);
                showHDMI();
                animateFullWebView(false);
                setHDMIAlignments(true);


            } else if (currentLayoutDetails[0].equalsIgnoreCase("BannerHDMI")) {
                webView.setVisibility(View.GONE);
                if (hdmiSuraface == null){
                    hdmiSuraface = currencyView.findViewById(R.id.home_ac_hdmi);
                }
                hdmiSuraface.setVisibility(View.VISIBLE);
                Log.d(TAG, "loadLayoutSchedules: " + "********************");
                currencyMediaWrapper.setVisibility(View.VISIBLE);
                showHDMI();
                animateFullWebView(false);

                setHDMIAlignments(false);
            }


            else {
                Toast.makeText(this, "Products", Toast.LENGTH_SHORT).show();
                animateFullWebView(true);
                releaseCameraResource();
            }
            stopVideoView();
        }
    }

    private void showHDMI() {
        if (null != mCamera) return;
        openCamera();
        StartPreview();
        if (hdmiSuraface.getHeight() == 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    releaseCameraResource();
                    showHDMI();
                }
            }, 1000);
    }

    private void setHDMIAlignments(boolean right) {
        params = (RelativeLayout.LayoutParams) hdmiSuraface.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (right) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }

        hdmiSuraface.setLayoutParams(params);
    }

    private void animateFullWebView(final boolean show) {
        boolean listenAnimation = true;
        if (show) {
            allHDMIWrapper.setVisibility(View.GONE);
            webViewFull.setVisibility(View.VISIBLE);
//            Namal edit this.........................
            if (webViewFull.getUrl() == null || webViewFull.getHeight() == 0) {
                webViewFull.loadUrl(Refferences.GetProduct.method);
            }
            if (!webViewLoaded) {
                webViewLoaded = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webViewFull.setVisibility(View.GONE);
                        webViewFull.setVisibility(View.VISIBLE);
                        if (webViewFull.getUrl() == null || webViewFull.getHeight() == 0) {
                            webViewFull.loadUrl(Refferences.GetProduct.method);
                        }
                    }
                }, 2000);
            }
        } else {
            allHDMIWrapper.setVisibility(View.VISIBLE);
            webViewFull.setVisibility(View.GONE);

        }
    }

    private void stopVideoView() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
            mMediaPlayer.stop();
        }
        imageViewAdFull.setImageResource(0);
    }

    private void setHeartBeatPacket() {
        if (currentSecond >= 30) {
            new Methods().saveToTextFile(new Date() + "\n\n", "/beat.txt");
            currentSecond = 0;
            new AsyncWebService(this, MyConstants.HEART_BEAT).execute(Refferences.UpdateNodeStatus.methodName + Methods.getNodeId(FullscreenActivity.this));
            System.gc();
        }
    }


    private void setNetStatusImage() {
        if (!isDownloading) {
            if (MyBroadCastReciever.isNetworkConnected(FullscreenActivity.this)) {
                statusIcon.setImageResource(R.drawable.online);
            } else {
                statusIcon.setImageResource(R.drawable.offline);
            }
        }

    }

    private boolean imageVideoConfigsWithIsImage(File file) {
        for (String extension :
                imageFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String[] loadLayout() {
        try {
            String[] viewMetas = getViewName(selectedLayout);
            if (viewMetas != null && viewMetas.length != 0) {
                setView(viewMetas[0]);
            }
            ++selectedLayout;
            return viewMetas;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] getViewName(int selectedLayoutInner) throws JSONException {
        selectedLayout = getSelectedLayoutPosition(selectedLayoutInner, MyConstants.SELECTED_TEMPLATE_SEQ);
        if (templateSeqJsonArray != null && templateSeqJsonArray.length() != 0) {
            return new String[]{templateSeqJsonArray.getJSONObject(selectedLayout).getString("TemplateName"),
                    templateSeqJsonArray.getJSONObject(selectedLayout).getString("Duration")};

        }

        return null;
    }

    private int getSelectedLayoutPosition(int selectedLayoutInner, int typeArray) {
        if (typeArray == MyConstants.SELECTED_TEMPLATE_SEQ) {
            if (selectedLayoutInner == -1 || (templateSeqJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        } else if (typeArray == MyConstants.SELECTED_SCHEDULE) {
            if (selectedLayoutInner == -1 || (adFullScheduleJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        } else if (typeArray == MyConstants.SELECTED_SCHEDULE_NORMAL) {
            if (selectedLayoutInner == -1 || (normalSchedulesJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        }

        return selectedLayoutInner;
    }

    public void initialize() {
        try {

            branchName.setText(Methods.getBranchName(this));
            templateSeqJsonArray = new JSONArray(Methods.getSharedPref(this).getString("sequence", ""));
            for (int i = 0; i < templateSeqJsonArray.length(); i++) {
                if (templateSeqJsonArray.getJSONObject(i).getString("TemplateName").trim().equalsIgnoreCase("TV")) {
                    dateTVOnwards = getDateFromString(templateSeqJsonArray.getJSONObject(i).getString("Duration").trim());
                    templateSeqJsonArray.remove(i);
                    break;
                }
            }
            adFullScheduleJsonArray = new JSONArray();
            adHocStatus = MyConstants.NOT_LOADED;
            tvOnwardsStatus = MyConstants.NOT_LOADED;
//            isDownloadStarted = false;

            configNormalSchedules();
            configAdFullSchedules(null);
//            if (dynamic.getChildCount() == 0) {
            loadLayouts(MyValues.isResetApp());
//            }
            hideAllLayouts();
            setNewsLines();
            loadWebView();
            loadWebViewFull();
            resetHandlersAndReInit();

            selectedLayout = -1;
            selectedSchedule = -1;
            selectedScheduleNormal = -1;
            Date startDate = simpleDateFormatTime.parse(Methods.getSharedPref(this).getString("DownloadTime", ""));
            downloadStartTime = new Date[]{startDate, add10Sec(startDate)};


            if (!MyValues.isResetApp()) {
                initializeHandlers();
                checkDownloadOnStart(Methods.getSharedPref(this).getString("DownloadStop", ""));
                Log.d(TAG, "initialize: -------111222333" + MyValues.isResetApp());
            }

            MyValues.setResetApp(false);
            Log.d(TAG, "initialize: =============================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDownloadOnStart(String downloadStop) {
        try {
            Date date = new Date();
            if (simpleDateFormatTime.parse(simpleDateFormatTime.format(date)).before(simpleDateFormatTime.parse(downloadStop))) {
                beginDownload();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void resetHandlersAndReInit() {
        if (handlerChangeBoolean != null && runnableChangeBoolean != null) {
            handlerChangeBoolean.removeCallbacks(runnableChangeBoolean);
        }
        if (handlerSingleSecond != null && runnableSingleSecond != null) {
            handlerSingleSecond.removeCallbacks(runnableSingleSecond);
            handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);

        }
        if (handlerSchedules != null && runnableSingleSecond != null) {
            handlerSchedules.removeCallbacks(runnableSchedules);
            handlerSchedules.postDelayed(runnableSchedules, 1500);

        }
        if (handlerTemplateSequence != null && runnableTemplateSequence != null) {
            handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
            handlerTemplateSequence.postDelayed(runnableTemplateSequence, 1000);

        }
        if (handlerCatchFullPlayed != null && runnableCatchFullPlayed != null) {
            handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);

        }


    }


    public void beginDownload() {
        String url = Refferences.GetDownloadMedia.methodName + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + Methods.getNodeId(FullscreenActivity.this);
        new AsyncWebService(this, MyConstants.GET_REQUIRED_FILES).execute(url);
    }

    private void loadLayouts(boolean resetApp) {
//        try {
        if (adFullView == null) {
            dynamic.addView(adFullView = inflater.inflate(R.layout.layout_ad_full, null));

            imageViewAdFull = adFullView.findViewById(R.id.img_MyFullLayoutAdFull);
            mPreview = adFullView.findViewById(R.id.vid_MyFullLayoutAdFull);
            mPreview.setSurfaceTextureListener(this);

        }
        if (!resetApp && currencyView == null) {

        }

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void loadWebView() {
        if (currencyView != null) {
            webView = currencyView.findViewById(R.id.webV_CurrencyLayoutCurrencyV);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setAppCacheEnabled(false);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.setWebViewClient(new CustomWebViewClient(this));
////            Namal edit this....................
//            webView.loadUrl(Refferences.GetCurrency.method);

        }
    }

    public void loadWebViewFull() {
        if (currencyView != null) {
            webViewFull = currencyView.findViewById(R.id.webV_Full);
            webViewFull.getSettings().setJavaScriptEnabled(true);
            webViewFull.getSettings().setAppCacheEnabled(false);
            webViewFull.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webViewFull.setWebViewClient(new CustomWebViewClient(this));
//            namal edit this..
//            webViewFull.loadUrl(Refferences.GetProduct.method);
        }


    }


    public void setNewsLines() {
        new AsyncWebService(this, MyConstants.GET_NEWS).execute(Refferences.GetNews.methodName + Methods.getNodeId(FullscreenActivity.this));
    }


    private void configNormalSchedules() {
        try {
            normalSchedulesJsonArray = new JSONArray();
            JSONArray schedulesJsonArray = new JSONArray(Methods.getSharedPref(this).getString("schedules", ""));
            JSONArray jsonArray;

            for (int i = 0; i < schedulesJsonArray.length(); i++) {
                if (schedulesJsonArray.getJSONObject(i).getInt("Panel") == 1) {
                    jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Default");
                    for (int l = 0; l < jsonArray.length(); l++) {
                        normalSchedulesJsonArray.put(jsonArray.getJSONObject(l));
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String configAdFullSchedules(Date now) {
        String playId = "";
        try {
            boolean isDefault = true;
//            if (adFullScheduleJsonArray.length() != 0) {
            adFullScheduleJsonArray = new JSONArray();
//            adHocStopTimeArrayList = new ArrayList<>();
            adHocTimeArrayList = new ArrayList<>();
            Log.d(TAG, "configAdFullSchedules: " + 1);
            JSONArray schedulesJsonArray = new JSONArray(Methods.getSharedPref(this).getString("schedules", ""));
            JSONArray jsonArray;
            for (int i = 0; i < schedulesJsonArray.length(); i++) {
                Log.d(TAG, "configAdFullSchedules: " + 2);

                if (schedulesJsonArray.getJSONObject(i).getInt("Panel") == 2) {
                    jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Schedule");
                    Log.d(TAG, "configAdFullSchedules: " + jsonArray.length() + "jLength");
//                    if (jsonArray.length() != 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObjectInner = jsonArray.getJSONObject(j);

                        if (now == null) {
                            now = getDateFromString(simpleDateFormatTime.format(new Date()));
                        }

                        Date start = getDateFromString(jsonObjectInner.getString("Start"));
                        Date end = getDateFromString(jsonObjectInner.getString("End"));
                        Log.d(TAG, "configAdFullSchedules: start" + start);
                        if (now.equals(start) || (now.after(start) && now.before(add10Sec(start)))) {
                            Methods methods = new Methods();
                            for (int k = 0; k < jsonObjectInner.getJSONArray("PlayItems").length(); k++) {
                                adFullScheduleJsonArray.put(jsonObjectInner.getJSONArray("PlayItems").getJSONObject(k));
                                methods.saveToTextFile(now + "" + jsonObjectInner.getJSONArray("PlayItems").getJSONObject(k), "/ad-hoc-status.txt");
                                File file = new File(Methods.createOrGetDirectory().toString() + "/" +
                                        jsonObjectInner.getJSONArray("PlayItems").getJSONObject(k).getString("Name"));
                                if (file.exists()) {
                                    methods.saveToTextFile("exists", "/ad-hoc-status.txt");
                                } else {
                                    methods.saveToTextFile("not exists", "/ad-hoc-status.txt");
                                }
                            }
                            isDefault = false;
//                            if (now.equals(start)) {
                            playId = jsonObjectInner.getString("PlaylistScheduleId");
//                            }
                        }

//                        adHocStartTimeArrayList.add(getDateArray(start));
//                        adHocStopTimeArrayList.add(getDateArray(end));
                        adHocTimeArrayList.add(getDateArray(start, end));
                        Log.d(TAG, "configAdFullSchedules: " + start);
                        Log.d(TAG, "configAdFullSchedules: " + end);
                    }
//                    }
                    if (isDefault) {
                        jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Default");
                        for (int l = 0; l < jsonArray.length(); l++) {
                            adFullScheduleJsonArray.put(jsonArray.getJSONObject(l));
                        }
//                        scheduleAdFullConfigs(MyConstants.DEFAULT_ARRAY, "");
                    }

                    break;
                }

            }
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return playId;
    }

    private Date[] getDateArray(Date start, Date end) {
        return new Date[]{start, add10Sec(start), end, add10Sec(end)};
    }

    private Date add10Sec(Date date) {
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 10);
        return calendar.getTime();
    }
    private Date getDateFromString(String format) throws ParseException {
        return simpleDateFormatTime.parse(format);
    }


    private void setView(String viewName) {
        if (viewName.equalsIgnoreCase("AdFull")) {
//            adFullView.setAlpha(1);
            mPreview.setVisibility(View.GONE);
            currencyView.setVisibility(View.GONE);
            adFullView.setVisibility(View.VISIBLE);
            webViewFull.setVisibility(View.GONE);

        } else {
            if (currencyView != null) {
                mPreview.setVisibility(View.GONE);
                currencyView.setVisibility(View.VISIBLE);
                adFullView.setVisibility(View.GONE);
                webViewFull.setVisibility(View.VISIBLE);
            }

        }
        initIfFirstTime();
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initIfFirstTime() {
        if (isFirstTime) {
            isFirstTime = false;
            final ImageView thumb = findViewById(R.id.thumb);
            thumb.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
            thumb.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((RelativeLayout) thumb.getParent()).removeView(thumb);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

    }


//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    Handlers Methods
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================


    private synchronized void methodTemplateSeq() {
        handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
        if (tvOnwardsStatus == MyConstants.NOT_LOADED) {
            currentLayoutDetails = loadLayout();

            if (handlerCatchFullPlayed != null && runnableCatchFullPlayed != null) {
                handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);
            }
            if (currentLayoutDetails != null && currentLayoutDetails.length != 0) {
                if (currentLayoutDetails[0].equalsIgnoreCase("AdFull")) {
                    handlerChangeBoolean.postDelayed(runnableChangeBoolean, getMillisFromTime(currentLayoutDetails[1]));
                    handlerSchedules.postDelayed(runnableSchedules, 10);
                    releaseCameraResource();
                    return;

                } else {
                    loadLayoutSchedules();
                }

                handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
                handlerSchedules.postDelayed(runnableSchedules, 10);
            }
        }


    }

    private synchronized void methodRunnableSchedules() {
        try {
            handlerSchedules.removeCallbacks(runnableSchedules);
            if (tvOnwardsStatus != MyConstants.PLAYING) {
                if (dynamic.getChildCount() != 0) {
                    Log.d(TAG, "run: *************" + currentLayoutDetails[0]);
//                        Toast.makeText(FullscreenActivity.this, currentLayoutDetails[0], Toast.LENGTH_SHORT).show();
                    if (currentLayoutDetails[0].equalsIgnoreCase("AdFull")) {

                        if (changeAdFull) {
                            currentLayoutDetails = loadLayout();
                            loadLayoutSchedules();
                            handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
                            handlerSchedules.postDelayed(runnableSchedules, 1);
                            changeAdFull = false;
                        } else {
                            clearSurface(surface);
//                                configAdFullSchedules(null);
                            selectedSchedule = getSelectedLayoutPosition(selectedSchedule, MyConstants.SELECTED_SCHEDULE);
                            jsonObjectInsideRunnableSchedules = adFullScheduleJsonArray.getJSONObject(selectedSchedule);
                            String fileName = jsonObjectInsideRunnableSchedules.getString("Name");

                            file = new File(Methods.createOrGetDirectory().toString() + "/" + jsonObjectInsideRunnableSchedules.getString("Name"));
                            Log.d(TAG, "run: " + fileName);
                            if (file.exists()) {
                                if (imageVideoConfigsWithIsImage(file)) {
                                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                                    mPreview.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
//                                    imageViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));

                                    mPreview.setVisibility(View.GONE);
                                    imageViewAdFull.setVisibility(View.VISIBLE);
                                    imageViewAdFull.setImageBitmap(bitmap);
                                } else {
//                                        videoView.setVisibility(View.VISIBLE);
//                                mPreview.setVisibility(View.VISIBLE);
//                                    imageViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
//                                    mPreview.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));

                                    imageViewAdFull.setVisibility(View.GONE);
                                    imageViewAdFull.setImageResource(0);

                                    try {
                                        mMediaPlayer.reset();
                                        mMediaPlayer.setDataSource(this, Uri.parse(file.toString()));
                                        mMediaPlayer.setSurface(surface);

                                        // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
                                        // creating MediaPlayer
                                        mMediaPlayer.prepareAsync();
                                        // Play video when the media source is ready for playback.
                                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mediaPlayer) {
                                                mMediaPlayer.start();
                                                handlerDelay01.postDelayed(runnableDelay01, 500);
//                                                mPreview.
                                            }
                                        });


                                    } catch (IllegalArgumentException e) {
                                        Log.d(TAG, e.getMessage());
                                    } catch (SecurityException e) {
                                        Log.d(TAG, e.getMessage());
                                    } catch (IllegalStateException e) {
                                        Log.d(TAG, e.getMessage());
                                    } catch (IOException e) {
                                        Log.d(TAG, e.getMessage());
                                    }


//                                videoViewAdFull.setVideoURI(Uri.parse(file.toString()));
//                                videoViewAdFull.start();
//                                    imageViewAdFull.getAnimation().setAnimationListener(new Animation.AnimationListener() {
//                                        @Override
//                                        public void onAnimationStart(Animation animation) {
//
//                                        }
//
//                                        @Override
//                                        public void onAnimationEnd(Animation animation) {
//                                            imageViewAdFull.setImageResource(0);
//                                        }
//
//                                        @Override
//                                        public void onAnimationRepeat(Animation animation) {
//
//                                        }
//                                    });

                                }


                                Log.d(TAG, "run: ===========" + getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                                handlerSchedules.postDelayed(runnableSchedules, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                            } else {
                                handlerSchedules.postDelayed(runnableSchedules, 10);
                            }
                            ++selectedSchedule;
//                                if (jsonObject.getString("Name"))
                        }
                    } else if (currentLayoutDetails[0].equalsIgnoreCase("BannerHDMI")) {
//                            configNormalSchedules();
                        selectedScheduleNormal = getSelectedLayoutPosition(selectedScheduleNormal, MyConstants.SELECTED_SCHEDULE_NORMAL);
                        jsonObjectInsideRunnableSchedules = normalSchedulesJsonArray.getJSONObject(selectedScheduleNormal);
                        String fileName = jsonObjectInsideRunnableSchedules.getString("Name");


                        file = new File(Methods.createOrGetDirectory().toString() + "/" + jsonObjectInsideRunnableSchedules.getString("Name"));
                        Log.d(TAG, "run:---------- " + fileName);
                        if (file.exists()) {
                            if (imageVideoConfigsWithIsImage(file)) {
                                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                                    videoViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
//                                    imageViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                                imageViewNormal.setImageBitmap(bitmap);
                                imageViewNormal.setVisibility(View.VISIBLE);
//                                    videoViewNormal.setVisibility(View.GONE);
                            }
//                                else {
////                                    imageViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
////                                    videoViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
//                                    videoViewNormal.setVideoURI(Uri.parse(file.toString()));
//                                    videoViewNormal.setVisibility(View.VISIBLE);
//                                    imageViewNormal.setVisibility(View.GONE);
////                                    videoViewNormal.get
//                                    videoViewNormal.start();
//
//                                }

//asd
                            handlerCatchFullPlayed.postDelayed(runnableCatchFullPlayed, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                            handlerSchedules.postDelayed(runnableSchedules, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                        } else {
                            handlerSchedules.postDelayed(runnableSchedules, 1);
                        }
//                            ++selectedScheduleNormal;
                    }

//                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                        videoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//
//
                } else {
                    handlerSchedules.postDelayed(runnableSchedules, 1000);
                    Log.d(TAG, "run:  else----------");
                }
//                    handlerSchedules.postDelayed(runnableSchedules, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSurface(Surface surface) {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, null);

        int[] attribList = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        egl.eglChooseConfig(display, attribList, configs, configs.length, numConfigs);
        EGLConfig config = configs[0];
        EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        });
        EGLSurface eglSurface = egl.eglCreateWindowSurface(display, config, surface,
                new int[]{
                        EGL14.EGL_NONE
                });

        egl.eglMakeCurrent(display, eglSurface, eglSurface, context);
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        egl.eglSwapBuffers(display, eglSurface);
        egl.eglDestroySurface(display, eglSurface);
        egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroyContext(display, context);
        egl.eglTerminate(display);
    }

    private synchronized void methodHandlerSingleSecond() {
        setTimePlusCheckDownloadingTimePlusAdHoc();
        currentSecond++;
        Log.d(TAG, "run: single=========================================" + currentSecond);
        setNetStatusImage();
        setHeartBeatPacket();
//                checkNetworkIssue();

        handlerSingleSecond.removeCallbacks(runnableSingleSecond);
        handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);
    }
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------

    public long getMillisFromTime(String dateString) {
        String[] splitStrings = dateString.split(":");
        return ((Long.parseLong(splitStrings[0]) * 60 * 60) + (Long.parseLong(splitStrings[1]) * 60) + (Long.parseLong(splitStrings[2]))) * 1000;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        surface = new Surface(surfaceTexture);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                handlerSchedules.postDelayed(runnableSchedules, 10);
                Log.d(TAG, "onError: VidAdFull++++++++++++++");
                return true;
            }
        });

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        if (mMediaPlayer != null) {
            // Make sure we stop video and release resources when activity is destroyed.
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

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

    private void takeScreenshot(String value, Bitmap bitmap) {
        Date now = new Date();
        Log.d(TAG, "takeScreenshot: ");
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + now + "_" + value + ".jpg";

            // create bitmap screen capture
            if (bitmap == null) {
                View v1 = getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(true);
            }

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


    private void getMyRequiredSS(final String value) {
        if (mMediaPlayer.isPlaying()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    takeScreenshot(value, mPreview.getBitmap());
                }
            };
            thread.start();
        } else {
            takeScreenshot(value, null);
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
//    Giada


    public static class CameraErrorCallback implements
            android.hardware.Camera.ErrorCallback {
        @Override
        public void onError(int error, android.hardware.Camera camera) {
            Log.e("ccxbg", "Got camera error callback. error=" + error);
            if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {
                // We are not sure about the current state of the app (in
                // preview or
                // snapshot or recording). Closing the app is better than
                // creating a
                // new Camera object.
                throw new RuntimeException("Media server died.");
            }
        }
    }

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
        dynamic.addView(currencyView = inflater.inflate(R.layout.layout_currency_v, null));

        imageViewNormal = currencyView.findViewById(R.id.imgV_ImageLayoutCurrencyV);
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


        currencyMediaWrapper = currencyView.findViewById(R.id.rl_mediaWrapperLayoutCurrencyV);
        hdmiSuraface = currencyView.findViewById(R.id.home_ac_hdmi);
        allHDMIWrapper = currencyView.findViewById(R.id.rl_allHDMIWrapperLayoutCurrencyV);

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
//        openCamera();
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
//        try {
//            if (mCamera == null) {
//                openCamera();
//            }
//            if (null != mCamera) {
//                StartPreview();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(TAG, "surfaceCreated: Failed");
//        }
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
    //5.55AM

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
