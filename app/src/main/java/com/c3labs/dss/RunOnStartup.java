package com.c3labs.dss;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.Splash;

import java.io.File;

/**
 * Created by Diluf Thivanka
 */
public class RunOnStartup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getAction() + "------Myyyy----", Toast.LENGTH_LONG).show();
        Log.d("[[[[[[[[[[[[[[", "instance: " + (context instanceof MainActivity));
        try {
            Thread.sleep(1000 * 10);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
                    Intent i = new Intent(context, Splash.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                    if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
//                        Log.d("[[[[[[[[[[[---", "onReceive: Replaced");
//                        Log.d("[[[[[[[[[[[---", MyValues.getNewApkName());
//
                        File file = new File(Methods.createOrGetDirectory() + MyValues.getNewApkName());
                        if (file.exists()) {
                            file.delete();
                        }
                        MyValues.setUpdateBeat(true);

                    }


//                    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//                        MyValues.setBootCompleted(true);
//                    }




                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
