package com.c3labs.dss.WebService.Asyncs;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.c3labs.dss.Constants.MyValues;
import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.WebService.Refferences;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by c3 on 3/27/2018.
 */

public class AsyncDownAndUpdateAPK extends AsyncTask<String, Void, String> {
    String apkName = MyValues.getNewApkName();
    String path = Methods.createOrGetDirectory().toString();
    String TAG = "AsyncDownAndUpdateAPK";

    Context context;

    public AsyncDownAndUpdateAPK(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        File file = new File(path + apkName);
        Log.d(TAG, "doInBackground: File-------------" + file.exists());
        if (file.exists()) {
            file.delete();
        }

        OutputStream output = null;
        URL url = null;
        InputStream input = null;
        try {
            url = new URL(Refferences.getAPKUpdate.method + apkName);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(new Methods().getSSLSocketFactory(context));
            connection.connect();

            input = connection.getInputStream();
            output = new FileOutputStream(path + apkName);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            file.delete();
            Log.d("updateapp", "cant download apk......");

        } finally {
            try {
                output.close();
                input.close();
                installApp(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: Done-----------Updated");
        super.onPostExecute(s);
    }

    private void installApp(String path) {
        try {
            String cmd = "pm install -r " + Methods.createOrGetDirectory().toString() + MyValues.getNewApkName() + "\n";

            Process root = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(root.getOutputStream());
//                    Toast.makeText(MainActivity.this, createOrGetDirectory().toString(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, cmd, Toast.LENGTH_SHORT).show();
            Log.d("APK---", "onClick: " + cmd);
            os.writeBytes(cmd);

            os.flush();

            int i1 = root.waitFor();
            Log.d(TAG, "installApp: " + i1);


            //Runtime.getRuntime().exec(new String[]{"su", "-c", "pm install -r /sdcard/C3DSS/ASY.apk"});
        } catch (IOException e) {
            Log.w("updateapp", "cant update......");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    Namal add this file..............

//    private void installApp(String path) throws IOException {
//        for (int i = 0; i < 5; i++) {
//            try {
//                Log.d("updateApp", "start updating......");
//                Thread.sleep(10000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    String cmd = "pm install -r " + path + apkName + "\n";
//
//                    Process root = Runtime.getRuntime().exec("su");
//                    DataOutputStream os = new DataOutputStream(root.getOutputStream());
////                    Toast.makeText(MainActivity.this, createOrGetDirectory().toString(), Toast.LENGTH_SHORT).show();
////                    Toast.makeText(MainActivity.this, cmd, Toast.LENGTH_SHORT).show();
//                    Log.d("APK---", "onClick: " + cmd);
//                    os.writeBytes(cmd);
//
//                    os.flush();
//
//                    int i1 = root.waitFor();
//                    Log.d("InstallApp..........", "installApp: " + i1);
//
//
//                    //Runtime.getRuntime().exec(new String[]{"su", "-c", "pm install -r /sdcard/C3DSS/ASY.apk"});
//                } catch (IOException e) {
//                    Log.w("updateapp", "cant update......");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    private void installApp(String path) throws IOException {
//        for (int i = 0; i < 5; i++) {
//            try {
//                Log.d("updateApp", "start updating......");
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    String cmd = "pm install -r " + path + apkName + "\n";
//
//                    Process root = Runtime.getRuntime().exec("su");
//                    DataOutputStream os = new DataOutputStream(root.getOutputStream());
//                    Log.d("APK---", "onClick: " + cmd);
//                    os.writeBytes(cmd);
//                    os.flush();
//
//                    int exitCode = root.waitFor();
//                    Log.d("InstallApp..........", "installApp exitCode: " + exitCode);
//
//                    if (exitCode == 0) {
//                        // Installation successful
//                        Log.d("Install Done", "installApp: Install Done");
//                        break;
//                    } else {
//                        // Handle installation failure
//                        Log.w("InstallApp..........", "Installation failed");
//                    }
//                } catch (IOException e) {
//                    Log.e("InstallApp..........", "IOException: " + e.getMessage());
//                } catch (InterruptedException e) {
//                    Log.e("InstallApp..........", "InterruptedException: " + e.getMessage());
//                }
//            }
//        }
//    }

}
