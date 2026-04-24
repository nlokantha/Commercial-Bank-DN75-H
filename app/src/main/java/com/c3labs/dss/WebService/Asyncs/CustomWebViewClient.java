package com.c3labs.dss.WebService.Asyncs;

import android.content.Context;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.http.SslError;

import com.c3labs.dss.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class CustomWebViewClient extends WebViewClient {
    private static final String TAG = "CustomWebViewClient";
    private Context context;

    public CustomWebViewClient(Context context) {
        this.context = context;
    }

    private SSLContext getSSLContext() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // Load the self-signed certificate from res/raw
            InputStream caInput = context.getResources().openRawResource(R.raw.combank_ca); // Use your certificate file name
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

            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to create SSL context", e);
            return null;
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        SSLContext sslContext = getSSLContext();

        if (sslContext != null) {
            // Trust the custom SSL context, ignore the error
            handler.proceed();
        } else {
            // Cancel the connection if SSL setup fails
            handler.cancel();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }
}
