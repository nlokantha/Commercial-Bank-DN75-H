package com.c3labs.dss.WebService.Asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.R;
import com.c3labs.dss.WebService.Refferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by c3 on 3/23/2018.
 */

public class AsyncURLConnection extends AsyncTask<ArrayList<File>, Void, Void> {
    Context context;
    private static final String TAG = "demo";

    public AsyncURLConnection(Context context) {
        this.context = context;
    }

    private SSLSocketFactory getSSLSocketFactory() {
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

    @Override
    protected Void doInBackground(ArrayList<File>[] arrayLists) {
//        String TAG = "AssURL";
        try {
            String files = "";
            ArrayList<File> arrayList = arrayLists[0];

            for (int i = 0; i < arrayList.size(); i++) {
                files += arrayList.get(i).getName().replace(".", ",").split(",")[0];
                if (i != arrayList.size() - 1) {
                    files += ",";
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodeId", Methods.getNodeId(context));
            jsonObject.put("files", files);

            URL postURL = new URL(Refferences.UpdateFileDownloadStatus.methodName);
            Log.d("MyURL----------", "doInBackground: " + Refferences.UpdateFileDownloadStatus.methodName + " - " + files);

//            HttpURLConnection con = (HttpURLConnection) postURL.openConnection();
            HttpsURLConnection con = (HttpsURLConnection) postURL.openConnection();
//            con.setSSLSocketFactory(new Methods().getSSLSocketFactory(context));
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(10 * 1000);
            con.setReadTimeout(30 * 1000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);


            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();

            con.connect();


            int status = con.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();

                    String response = sb.toString();
                    Log.d("Server Response : \n", response);
                    Log.d("Size postToServer : ", +response.toString().getBytes().length + " bytes");
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }
}
