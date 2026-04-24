package com.c3labs.dss.WebService;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.FullscreenActivity;
import com.c3labs.dss.R;
import com.c3labs.dss.Splash;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by c3 on 2/6/2018.
 */

public class CallWeb {
    private static final String TAG = "demo";
    private Context context;

    public CallWeb(Context context) {
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


    public String callService(String urlString) {
        StringBuilder sb = null;
        BufferedReader reader = null;
        String serverResponse = "**";
        try {

            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // Set the custom SSLSocketFactory to trust the self-signed certificate
//            connection.setSSLSocketFactory(new Methods().getSSLSocketFactory(context));


            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();
            int statusCode = connection.getResponseCode();
            //Log.e("statusCode", "" + statusCode);
            if (statusCode == 200) {
                sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }

            connection.disconnect();
            if (sb != null)
                serverResponse = sb.toString();
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            Log.d(TAG, "callService: "+e.getMessage());
            return "timeOutEx";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "callService: "+e.getMessage());
            return "failure:"+e.getMessage();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return serverResponse;

//        return "";
    }
}
