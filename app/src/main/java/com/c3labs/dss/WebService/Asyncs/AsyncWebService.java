package com.c3labs.dss.WebService.Asyncs;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.c3labs.dss.BroadCRecievers.MyBroadCastReciever;
import com.c3labs.dss.Constants.MyConstants;
import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.FullscreenActivity;
import com.c3labs.dss.Splash;
import com.c3labs.dss.WebService.Asyncs.Tasks.DoOnPostExecTasks;
import com.c3labs.dss.WebService.CallWeb;

import java.util.Date;

/**
 * Created by c3 on 2/6/2018.
 */

public class AsyncWebService extends AsyncTask<String, String, String> {
    private static final String TAG = "**************";
    Context context;
    int taskId;

    public AsyncWebService(Context context, int taskId) {
        this.context = context;
        this.taskId = taskId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (taskId == MyConstants.LOGIN) {
            Splash splash = (Splash) context;
            splash.statusMessage.setText("Verifying.....");
        } else if (taskId == MyConstants.GET_TIME) {
            if (!MyValues.isResetApp() && context instanceof Splash) {
                Splash splash = (Splash) context;
                splash.statusMessage.setText("Setting Time.....");
            }
        } else if (taskId == MyConstants.GET_NEWS) {
//            Splash splash = (Splash) context;
//            splash.statusMessage.setText("Getting News Lines...");
        } else if (taskId == MyConstants.GET_TEMPLATE_SEQUENCE) {
            if (!MyValues.isResetApp() && context instanceof Splash) {
                Splash splash = (Splash) context;
                splash.statusMessage.setText("Getting Template Sequence...");
            }
        } else if (taskId == MyConstants.GET_SCHEDULES) {
            if (!MyValues.isResetApp() && context instanceof Splash) {
                Splash splash = (Splash) context;
                splash.statusMessage.setText("Getting Schedules...");
            }
        } else if (taskId == MyConstants.GET_REQUIRED_FILES) {
//            FullscreenActivity fullscreenActivity = (FullscreenActivity) context;
//            if (MyBroadCastReciever.isNetworkConnected(context)) {
//
//            }
        }


    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: URL = " + strings[0]);
        if (MyBroadCastReciever.isNetworkConnected(context)) {
            try {
//                Thread.sleep(1000 * 40);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (taskId != MyConstants.HEART_BEAT) {
                new Methods().saveToTextFile(new Date() + strings[0] + "\n\n", "/webCalls.txt");
            }
            String response = new CallWeb(context).callService(strings[0]);
            if (response.equalsIgnoreCase("timeOutEx")) {
//                new AsyncWebService(context, taskId).execute(strings[0]);

            } else {
                return response;
            }
        }
        return "ErrorCalling";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onPostExecute ------------------------------------" + result);

        if ("success".equals(result)) {
            Toast.makeText(context, "Service Call Successful", Toast.LENGTH_SHORT).show();
        } else if ("no_response".equals(result)) {
            Toast.makeText(context, "No response from server", Toast.LENGTH_SHORT).show();
        } else if ("timeout".equals(result)) {
            Toast.makeText(context, "Connection Timed Out", Toast.LENGTH_SHORT).show();
        } else if (result.startsWith("failure:")){
            String errorMessage = result.substring("failure:".length());
            Toast.makeText(context, "Service Call Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            Splash splash = (Splash) context;
            splash.statusMessage.setText(errorMessage);

        }




        if (!result.equalsIgnoreCase("ErrorCalling")) {
            if (taskId == MyConstants.LOGIN) {
                new DoOnPostExecTasks().saveUserId(result, context);
            } else if (taskId == MyConstants.GET_TIME) {
                new DoOnPostExecTasks().setSystemTime(result, context);
            } else if (taskId == MyConstants.GET_TEMPLATE_SEQUENCE) {
                new DoOnPostExecTasks().saveTemplateSequence(result, context);
                new Methods().saveToTextFile(result + "\n", "/template-s.txt");

            } else if (taskId == MyConstants.GET_SCHEDULES) {
                new DoOnPostExecTasks().saveSchedules(result, context);
                if (!MyValues.isResetApp() && context instanceof Splash) {
                    Splash splash = (Splash) context;
                    splash.unregReciever();
                    context.startActivity(new Intent(this.context, FullscreenActivity.class));
                    splash.finish();
                    new Methods().saveToTextFile(result + "\n", "/schedule.txt");
                } else {
                    ((FullscreenActivity) context).initialize();
                }
            } else if (taskId == MyConstants.GET_REQUIRED_FILES) {
                new DoOnPostExecTasks().checkRequiredFiles(result, context, 20);
            } else if (taskId == MyConstants.GET_NEWS) {
                new DoOnPostExecTasks().saveNewsLines(result, context);
            } else if (taskId == MyConstants.HEART_BEAT) {
                new DoOnPostExecTasks().checkHeartBeatPacket(result, context);
            }
        } else {
            Toast.makeText(context, "Error Calling", Toast.LENGTH_SHORT).show();

        }
    }
}
