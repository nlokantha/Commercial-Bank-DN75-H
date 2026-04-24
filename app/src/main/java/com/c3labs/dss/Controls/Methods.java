package com.c3labs.dss.Controls;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.c3labs.dss.BroadCRecievers.MyBroadCastReciever;
import com.c3labs.dss.R;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by c3 on 2/6/2018.
 */

public class Methods {
    private static final String TAG = "demo";
    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getNodeId(Context context) {
        return getSharedPref(context).getString("nodeId", "");
    }

    public static String getBranchName(Context context) {
        return getSharedPref(context).getString("branchName", "");
    }

    public static File createOrGetDirectory() {
        File myDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/C3DSS");

        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        return myDirectory;
    }

    public void setDateToSystem(Context context, String dateString) {
//        July 11, 2016 2:15 that would be date 0711141516
        try {
            String year = dateString.substring(0, 4);
            String month = dateString.substring(4, 6);
            String date = dateString.substring(6, 8);
            String hour = dateString.substring(9, 11);
            String min = dateString.substring(11, 13);
            String sec = dateString.substring(13, 15);

//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.YEAR, Integer.parseInt(year));
//            calendar.set(Calendar.MONTH, (Integer.parseInt(month) - 1));
//            calendar.set(Calendar.DATE, Integer.parseInt(date));
//            //
//            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
//            calendar.set(Calendar.MINUTE, Integer.parseInt(min));
//            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
//            calendar.set(Calendar.SECOND, Integer.parseInt(sec));
//
//            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(calendar.getTimeInMillis());

//            Log.d("?????????????????????", "setDateToSystem: " + month + date + hour + min + year);
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("date " + month + date + hour + min + year + "\n");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNetworkStatusIcon(Context context) {
        if (MyBroadCastReciever.isNetworkConnected(context)) {
            return R.drawable.online;
        }
        return R.drawable.offline;
    }

    public void restartDevice(Context context, String message) {

        saveToTextFile(new Date() + " - " + message, "/before-restart.txt");
        try {
            Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Intent intent = new Intent("zidoo.poser.off.action");
//        intent.putExtra("cmd", "reboot");
//        context.sendBroadcast(intent);
    }

    public void saveToTextFile(String value, String fileName) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Methods.createOrGetDirectory().toString() + fileName, true)));
            if (fileName.equalsIgnoreCase("/errors.txt")) {
                out.println(new Date());
            }
            out.println(value);
            out.close();

//            PrintWriter writer = new PrintWriter(Methods.createOrGetDirectory().toString() + fileName, "UTF-8");
//            writer.println(value);
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // Load the self-signed certificate from res/raw
            InputStream caInput = context.getResources().openRawResource(R.raw.combank_ca); // Make sure your certificate file is named my_cert.pem
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.d(TAG, "Certificate loaded: " + ((java.security.cert.X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing the trusted CA
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CA in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to create SSL socket factory", e);
            return null;
        }
    }
}
